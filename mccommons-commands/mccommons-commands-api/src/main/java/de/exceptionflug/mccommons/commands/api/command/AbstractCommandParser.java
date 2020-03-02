package de.exceptionflug.mccommons.commands.api.command;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.annotation.Command;

import java.lang.annotation.Annotation;

public abstract class AbstractCommandParser<C> {
    protected final AbstractCommand<?> mccCommand;
    protected final Class<? extends AbstractCommand> mccClazz;

    protected AbstractCommandParser(final AbstractCommand<?> mccCommand) {
        this.mccCommand = mccCommand;
        this.mccClazz = mccCommand.getClass();
    }


    public abstract C toCommand();

    // ----------------------------------------------------------------------------------------------------
    // Utility methods to make the
    // ----------------------------------------------------------------------------------------------------

    protected boolean isAnnotationPresent(final Class<? extends Annotation> annotation) {
        return mccClazz.isAnnotationPresent(annotation);
    }

    public <A extends Annotation> A getAnnotation(final Class<A> clazz) {
        return mccClazz.getAnnotation(clazz);
    }

    public Command getCommandAnnotation() {
        final Command commandAnnotation = getAnnotation(Command.class);

        if (commandAnnotation == null) {
            throw new IllegalStateException(
                "@Command Annotation wasn't not found at " + mccClazz.getName() +
                    " Make sure to place it above your command class!");
        }

        return commandAnnotation;
    }
}
