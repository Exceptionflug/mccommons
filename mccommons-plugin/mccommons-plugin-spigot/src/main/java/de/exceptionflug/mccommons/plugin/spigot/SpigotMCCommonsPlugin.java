package de.exceptionflug.mccommons.plugin.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotMCCommonsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new MCCommonsSpigotBootstrap().enable(this);
    }
}
