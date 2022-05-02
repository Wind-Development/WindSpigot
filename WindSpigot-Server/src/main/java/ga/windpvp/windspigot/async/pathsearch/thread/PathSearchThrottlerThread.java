package ga.windpvp.windspigot.async.pathsearch.thread;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ga.windpvp.windspigot.WindSpigot;
import ga.windpvp.windspigot.async.pathsearch.job.PathSearchJob;

// This is based on Minetick's async path searching
public class PathSearchThrottlerThread extends ThreadPoolExecutor {

	private int queueLimit;
	private Map<PathSearchJob, PathSearchJob> filter;
	private Set<Integer> activeSearchHashes;
	private static PathSearchThrottlerThread INSTANCE;

	public PathSearchThrottlerThread(int poolSize) {
		super(poolSize, poolSize, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(),
				new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Path Search Thread %d").build());
		INSTANCE = this;
		adjustPoolSize(poolSize);
		this.filter = new LinkedHashMap<PathSearchJob, PathSearchJob>();
		this.activeSearchHashes = new HashSet<Integer>();
	}

	public boolean queuePathSearch(PathSearchJob newJob) {
		boolean jobHasBeenQueued = false;
		if (newJob != null) {
			synchronized (this.filter) {
				if (this.filter.containsKey(newJob) || this.filter.size() < 1000) {
					jobHasBeenQueued = true;
					PathSearchJob previousJob = this.filter.put(newJob, newJob);
					if (previousJob != null) {
						previousJob.cleanup();
					}
				}
			}
		}
		PathSearchJob jobToExecute = null;
		synchronized (this.filter) {
			Iterator<Entry<PathSearchJob, PathSearchJob>> iter = this.filter.entrySet().iterator();
			while (iter.hasNext() && this.getQueue().size() < this.queueLimit) {
				jobToExecute = iter.next().getValue();
				if (!this.activeSearchHashes.contains(jobToExecute.getSearchHash())) {
					iter.remove();
					if (jobToExecute != null) {
						this.activeSearchHashes.add(jobToExecute.getSearchHash());
						this.submit(jobToExecute);
						WindSpigot.debug("Executing async path search...");
					}
					if (newJob != null) {
						break;
					}
				}
			}
		}
		return jobHasBeenQueued;
	}

	@Override
	public void shutdown() {
		this.getQueue().clear();
		super.shutdown();
	}

	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
	    super.afterExecute(runnable, throwable);
	    if(runnable instanceof FutureTask) {
	        FutureTask<PathSearchJob> task = (FutureTask<PathSearchJob>) runnable;
	        PathSearchJob job = null;
	        
            try {
				job = task.get();
			} catch (InterruptedException | ExecutionException ignored) {}

	        if(job != null) {
	            synchronized(this.filter) {
	                this.activeSearchHashes.remove(job.getSearchHash());
	            }
	        }
	    }
	    this.queuePathSearch(null);
	}

	public static void adjustPoolSize(int size) {
		if (INSTANCE != null) {
			if (size > INSTANCE.getMaximumPoolSize()) {
				INSTANCE.setMaximumPoolSize(size);
				INSTANCE.setCorePoolSize(size);
			} else if (size < INSTANCE.getMaximumPoolSize()) {
				INSTANCE.setCorePoolSize(size);
				INSTANCE.setMaximumPoolSize(size);
			}
			INSTANCE.queueLimit = size * 8;
		}
	}
}