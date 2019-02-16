package de.exceptionflug.mccommons.config.test;

import de.exceptionflug.mccommons.config.proxy.ProxyConfig;
import de.exceptionflug.mccommons.config.proxy.ProxyConfigProxyYamlConfigWrapper;
import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import org.junit.Test;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigTest {

    @Test
    public void runTest() {
        // Set up
        ConfigFactory.register(ProxyConfig.class, ProxyConfigProxyYamlConfigWrapper::new);

        final ProxyConfig config = ConfigFactory.create(new File("target/test.yml"), ProxyConfig.class);
        config.getItemStack("Test.itemStack");
        config.getOrSetDefault("Test.double", Math.PI);
        config.getOrSetDefault("Test.list", new ArrayList<>());
        config.getOrSetDefault("Test.map", new HashMap<>());
        config.getOrSetDefault("Test.string", "This is a test");
        System.out.println("Test passed.");
    }

}
