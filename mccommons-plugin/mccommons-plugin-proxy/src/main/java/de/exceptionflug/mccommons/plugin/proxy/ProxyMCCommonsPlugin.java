package de.exceptionflug.mccommons.plugin.proxy;

import net.md_5.bungee.api.plugin.Plugin;

public class ProxyMCCommonsPlugin extends Plugin {

    @Override
    public void onEnable() {
        new MCCommonsProxyBootstrap(this).enableMCCommons();
    }
}
