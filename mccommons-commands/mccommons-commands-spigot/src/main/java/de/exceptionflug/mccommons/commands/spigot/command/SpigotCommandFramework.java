package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mcccommons.commands.api.AbstractCommand;
import de.exceptionflug.mcccommons.commands.api.AbstractCommandFramework;
import de.exceptionflug.mcccommons.commands.api.input.InputSerializable;
import de.exceptionflug.mcccommons.commands.api.input.InputSerializer;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpigotCommandFramework extends AbstractCommandFramework {

    protected SpigotCommandFramework(final ConfigWrapper messageConfig) {
        super(messageConfig);
    }

    @Override
    public void registerCommand(@NonNull final AbstractCommand<?> command) {
        registeredCommands.add(command);
    }

    public void registerDefaultInputSerializables() {
        InputSerializer.registerSerializable(new InputSerializable<Player>() {
            @Override
            public Class<Player> getClazz() {
                return Player.class;

            }

            @Override
            protected void handleError(final Throwable throwable, final String input) {

            }

            @Override
            protected Player serialize0(final String input) throws Throwable {
                return Bukkit.getPlayer(input);
            }
        });
//


    }


}
