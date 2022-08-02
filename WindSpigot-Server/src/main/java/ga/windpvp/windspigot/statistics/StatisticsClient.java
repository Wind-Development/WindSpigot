package ga.windpvp.windspigot.statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

// Statistics client for WindSpigot
public class StatisticsClient {

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	public boolean isConnected;

	// Connects to the statistics server
	public void start(String ip, int port) throws IOException {
		this.socket = new Socket(ip, port);
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.isConnected = true;
	}

	// Sends data to the statistics server
	public void sendMessage(String msg) throws IOException {
		// Check if connected first
		if (this.isConnected) {
			this.out.println(msg);
		} 
	}

	// Closes the connection
	public void stop() throws IOException {
		in.close();
		out.close();
		socket.close();
		this.isConnected = false;
	}

}
