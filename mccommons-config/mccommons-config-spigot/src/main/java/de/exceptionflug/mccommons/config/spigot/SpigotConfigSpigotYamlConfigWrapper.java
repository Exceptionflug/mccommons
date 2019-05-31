package de.exceptionflug.mccommons.config.spigot;

import de.exceptionflug.mccommons.config.remote.model.ConfigData;
import de.exceptionflug.mccommons.core.Providers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class SpigotConfigSpigotYamlConfigWrapper implements SpigotConfig {

    private final YamlConfiguration fileConfiguration = new YamlConfiguration();
    private File file;
    private boolean damaged;

    private ConfigData configData;
    private final Consumer<ConfigData> updateConsumer;
    private final Supplier<ConfigData> reloadSupplier;

    public SpigotConfigSpigotYamlConfigWrapper(final File file) {
        this.file = file;
        try {
            fileConfiguration.load(file);
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.SEVERE,"[MCCommons] Unable to read "+file.getAbsolutePath()+":", e);
            damaged = true;
        }
        updateConsumer = null;
        reloadSupplier = null;
    }

    public SpigotConfigSpigotYamlConfigWrapper(final ConfigData data, final Consumer<ConfigData> updateConsumer, final Supplier<ConfigData> reloadSupplier) {
        try {
            fileConfiguration.load(new StringReader(data.getConfigData()));
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.SEVERE,"[MCCommons] Unable to read "+data.getRemotePath()+":", e);
            damaged = true;
        }
        this.file = null;
        this.updateConsumer = updateConsumer;
        this.configData = data;
        this.reloadSupplier = reloadSupplier;
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
            sound = Sound.UI_BUTTON_CLICK;
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
            if(configData != null) {
                setConfigData(reloadSupplier.get());
                return;
            }
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

    @Override
    public ConfigData getConfigData() {
        return configData;
    }

    @Override
    public void setConfigData(final ConfigData configData) {
        this.configData = configData;
        try {
            fileConfiguration.load(new StringReader(configData.getConfigData()));
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.SEVERE,"[MCCommons] Unable to read "+configData.getRemotePath()+":", e);
            damaged = true;
        }
    }
}
