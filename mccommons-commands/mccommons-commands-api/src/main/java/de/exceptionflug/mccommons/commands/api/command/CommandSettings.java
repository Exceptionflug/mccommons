package de.exceptionflug.mccommons.commands.api.command;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;


/**
 * Class for defining settings
 * for our command.
 * <p>
 * They will override the specific settings of our
 * subcommands.
 * <p>
 * For example if you set a specific permission here,
 * every subcommand (and the main-command) will need this permissions, too
 * <p>
 * They can only be done using the @Command Annotation.
 */
@Data
@Builder
public final class CommandSettings {
    private final String permission;
    private final boolean inGameOnly;
    private String[] name;

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }
}
