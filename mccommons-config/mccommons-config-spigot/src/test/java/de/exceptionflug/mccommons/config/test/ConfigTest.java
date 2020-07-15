package de.exceptionflug.mccommons.config.test;

import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import de.exceptionflug.mccommons.config.spigot.SpigotConfig;
import de.exceptionflug.mccommons.config.spigot.SpigotConfigSpigotYamlConfigWrapper;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigTest {

	@Test
	public void runTest() {
		// Set up
		ConfigFactory.register(SpigotConfig.class, SpigotConfigSpigotYamlConfigWrapper::new);

		final SpigotConfig config = ConfigFactory.create(new File("target/test.yml"), SpigotConfig.class);
		config.getItemStack("Test.itemStack");
		config.getOrSetDefault("Test.double", Math.PI);
		config.getOrSetDefault("Test.list", new ArrayList<>());
		config.getOrSetDefault("Test.map", new HashMap<>());
		config.getOrSetDefault("Test.string", "This is a test");
		System.out.println("Test passed.");
	}

}
