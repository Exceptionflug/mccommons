package de.exceptionflug.mccommons.config.proxy;

import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProxyConfigProxyYamlConfigWrapper implements ProxyConfig {

    private Configuration fileConfiguration;
    private final File file;

    public ProxyConfigProxyYamlConfigWrapper(final File file) {
        try {
            fileConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.file = file;
    }

    @Override
    public <T> T getHandle() {
        return (T) fileConfiguration;
    }

    @Override
    public void set(final String path, final Object obj) {
        fileConfiguration.set(path, obj);
        save();
    }

    @Override
    public <T> T getOrSetDefault(String path, T def) {
        final Object o = fileConfiguration.get(path);
        if(o == null) {
            fileConfiguration.set(path, def);
            save();
            return def;
        }
        return (T) o;
    }

    @Override
    public Set<String> getKeys(String path) {
        return new HashSet<>(fileConfiguration.getSection(path).getKeys());
    }

    @Override
    public boolean isSet(String path) {
        return fileConfiguration.contains(path);
    }

    @Override
    public ConfigItemStack getItemStack(String path, Locale locale, Map<String, String> replacements) {
        String material = getOrSetDefault(path+".type", ItemType.GRASS.name());
        final int amount = getOrSetDefault(path+".amount", 1);
        String displayName = getLocalizedString(locale, path, ".displayName", path);
        List<String> lore = getLocalizedStringList(locale, path, ".lore", new ArrayList<>());
        final List<String> enchantments = getOrSetDefault(path+".enchantments", new ArrayList<>());
        final List<String> flags = getOrSetDefault(path+".flags", new ArrayList<>());
        displayName = ChatColor.translateAlternateColorCodes('&', FormatUtils.format(displayName, replacements));
        material = FormatUtils.format(material, replacements);
        lore = FormatUtils.format(lore, replacements);
        return new ConfigItemStack().setAmount(amount).setDisplayName(displayName).setEnchantments(enchantments).setFlags(flags).setLore(lore).setType(material).setSkull(fileConfiguration.getString(path+".skull")).setEntityType(fileConfiguration.getString(path+".entityType"));
    }

    @Override
    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(fileConfiguration, file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        try {
            fileConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
