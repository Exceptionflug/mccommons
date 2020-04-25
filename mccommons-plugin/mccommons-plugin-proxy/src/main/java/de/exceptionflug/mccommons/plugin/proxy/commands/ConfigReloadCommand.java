package de.exceptionflug.mccommons.plugin.proxy.commands;

import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ConfigReloadCommand extends Command {

    public ConfigReloadCommand() {
        super("gmcrl", "mccommons.command.crl");
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] strings) {
        ConfigFactory.reloadAll();
        commandSender.sendMessage("§8[§6MCCommons§8] §5Configs reloaded.");
    }

}
