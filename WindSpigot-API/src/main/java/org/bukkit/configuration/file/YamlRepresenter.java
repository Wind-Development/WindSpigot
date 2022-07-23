package org.bukkit.configuration.file;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.Map;

public class YamlRepresenter extends Representer {

	public YamlRepresenter() {
		this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
	}

	private class RepresentConfigurationSerializable extends RepresentMap {
		@Override
		public Node representData(Object data) {
			ConfigurationSerializable serializable = (ConfigurationSerializable) data;
			Map<String, Object> values = new LinkedHashMap<String, Object>();
			values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
					ConfigurationSerialization.getAlias(serializable.getClass()));
			values.putAll(serializable.serialize());

			return super.representData(values);
		}
	}
}
