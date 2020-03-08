package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.AbstractCommandFramework;
import de.exceptionflug.mccommons.commands.api.input.InputSerializable;
import de.exceptionflug.mccommons.commands.api.input.InputSerializer;
import de.exceptionflug.mccommons.commands.spigot.impl.SpigotCommandParser;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpigotCommandFramework extends AbstractCommandFramework {


    private static volatile SpigotCommandFramework instance;

    public SpigotCommandFramework(final ConfigWrapper messageConfig) {
        super(messageConfig);
    }

    @Override
    public void registerCommand(@NonNull final AbstractCommand<?> command) {
        registeredCommands.add(command);
        Commands.registerCommand(new SpigotCommandParser(command).toCommand());
    }

    public void registerDefaultInputSerializables() {

        InputSerializer.registerSerializable(new InputSerializable<Player>() {
            @Override
            public Class<Player> getClazz() {
                return Player.class;
            }

            @Override
            public Player serialize(final String input) {
                final Player player;
                if (input.length() == 36) {
                    final UUID targetUUID = UUID.fromString(input);
                    player = Bukkit.getPlayer(targetUUID);
                } else {
                    player = Bukkit.getPlayer(input);
                }

                if (player == null) {
                }
                return player;
            }
        });


    }


}
