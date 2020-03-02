package de.exceptionflug.mccommons.commands.spigot.command;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.Map;

@UtilityClass
public class Commands {

    private final String PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName();
    private final String VERSION = PACKAGE_NAME.substring(PACKAGE_NAME.lastIndexOf(".") + 1);

    @SneakyThrows
    public void registerCommand(@NonNull final Command command) {
        final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);

        final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        commandMap.register(command.getLabel(), command);
    }

    public void removeCommands(final String... cmds) {
        for (final String cmd : cmds) {
            removeCommand(cmd);
        }
    }

    //TODO check: Does this work?
    public boolean isRegistered(final String command) {
        final String[] splitted = command.split(" ");
        return Bukkit.getServer().getHelpMap().getHelpTopic(splitted[0]) == null;
    }

    @SneakyThrows
    private void removeCommand(final String command) {
        Class<?> serverClass = Class.forName("org.bukkit.craftbukkit." + VERSION + ".CraftServer");

        Field field = serverClass.getDeclaredField("commandMap");
        field.setAccessible(true);
        SimpleCommandMap commandMap = (SimpleCommandMap) field.get(Bukkit.getServer());

        Field field2 = SimpleCommandMap.class.getDeclaredField("knownCommands");
        field2.setAccessible(true);
        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, Command> knownCommands = (Map) field2.get(commandMap);

        knownCommands.remove(command.toLowerCase());
    }

}
