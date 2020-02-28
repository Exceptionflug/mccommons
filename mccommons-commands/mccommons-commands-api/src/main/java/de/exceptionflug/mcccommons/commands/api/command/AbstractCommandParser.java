package de.exceptionflug.mcccommons.commands.api.command;

import de.exceptionflug.mcccommons.commands.api.annotation.Command;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AbstractCommandParser {
    private final Class<? extends Command> clazz;


}
