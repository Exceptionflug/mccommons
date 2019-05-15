package de.exceptionflug.mccommons.config.remote.server;

import de.exceptionflug.mccommons.config.proxy.ProxyConfigProxyYamlConfigWrapper;
import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RemoteConfigServer {

    public static void main(final String[] args) {
        SpringApplication.run(RemoteConfigServer.class);
    }

    @PostConstruct
    public void setupConfigFactory() {
        ConfigFactory.register(ConfigWrapper.class, ProxyConfigProxyYamlConfigWrapper::new);
    }

}
