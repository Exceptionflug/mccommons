package de.exceptionflug.mccommons.config.spigot;

import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class SpigotConfigSpigotYamlConfigWrapper implements SpigotConfig {

    private final YamlConfiguration fileConfiguration = new YamlConfiguration();
    private File file;
    private boolean damaged;

    public SpigotConfigSpigotYamlConfigWrapper(final File file) {
        this.file = file;
        try {
            fileConfiguration.load(file);
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.SEVERE,"[MCCommons] Unable to read "+file.getAbsolutePath()+":", e);
            damaged = true;
        }
    }

    @Override
    public Location getLocation(final String path) {
        final String worldName = getOrSetDefault(path+".world", "world");
        final double x = getOrSetDefault(path+".x", 0D);
        final double y = getOrSetDefault(path+".y", 60D);
        final double z = getOrSetDefault(path+".z", 0D);
        final double yaw = getOrSetDefault(path+".yaw", 0D);
        final double pitch = getOrSetDefault(path+".pitch", 0D);
        return new Location(Bukkit.getWorld(worldName), x, y, z, (float) yaw, (float) pitch);
    }

    @Override
    public SoundData getSoundData(final String path) {
        final String soundName = getOrSetDefault(path+".sound", "CLICK");
        final float volume = getOrSetDefault(path+".volume", 1F);
        final float pitch = getOrSetDefault(path+".pitch", 1F);
        Sound sound;
        try {
            sound = Sound.valueOf(soundName);
        } catch (final IllegalArgumentException e) {
            sound = Sound.CLICK;
            Bukkit.getLogger().warning("[SpigotConfig] WARN: "+path+" has invalid sound "+soundName);
        }
        return new SoundData(sound, volume, pitch);
    }

    @Override
    public PositionedSound getPositionedSound(final String path) {
        final SoundData sound = getSoundData(path);
        return new PositionedSound(getLocation(path+".location"), sound.getSound(), sound.getVolume(), sound.getPitch());
    }



    @Override
    public <T> T getHandle() {
        return (T) fileConfiguration;
    }

    @Override
    public void set(final String path, final Object obj) {
        if(obj instanceof Location) {
            set(path+".x", ((Location) obj).getX());
            set(path+".y", ((Location) obj).getY());
            set(path+".z", ((Location) obj).getZ());
            set(path+".yaw", ((Location) obj).getYaw());
            set(path+".pitch", ((Location) obj).getPitch());
            return;
        }
        fileConfiguration.set(path, obj);
        save();
    }

    @Override
    public <T> T getOrSetDefault(String path, T def) {
        final Object o = fileConfiguration.get(path);
        if(o == null) {
            if(def != null) {
                fileConfiguration.set(path, def);
                save();
            }
            return def;
        }
        return (T) o;
    }

    @Override
    public Set<String> getKeys(String path) {
        final ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(path);
        if(configurationSection == null)
            return Collections.emptySet();
        return configurationSection.getKeys(false);
    }

    @Override
    public boolean isSet(String path) {
        return fileConfiguration.isSet(path);
    }

    @Override
    public void save() {
        if(damaged) {
            Bukkit.getLogger().warning("[MCCommons] Save process of "+file.getAbsolutePath()+" cancelled to protect unloaded configuration file!");
            return;
        }
        try {
            fileConfiguration.options().header("MCCommons v"+ (Providers.has(JavaPlugin.class) ? Providers.get(JavaPlugin.class).getDescription().getVersion() : "2") +" configuration file; impl = "+getClass().getSimpleName());
            fileConfiguration.save(file);
        } catch (final IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "[MCCommons] Unable to save "+file.getAbsolutePath()+":", e);
        }
    }

    @Override
    public void reload() {
        try {
            fileConfiguration.load(file);
            damaged = false;
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.SEVERE,"[MCCommons] Unable to read "+file.getAbsolutePath()+":", e);
            damaged = true;
        }
    }
}
