package de.exceptionflug.mccommons.commands.proxy;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.plugin.Command;

@UtilityClass
public class Commands {

    @SneakyThrows
    public void registerCommand(@NonNull final Command command) {

    }

    public void removeCommands(final String... cmds) {

    }

    public boolean isRegistered(final String command) {
        return false;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private void removeCommand(final String command) {

    }
}
