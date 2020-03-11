package de.exceptionflug.mccommons.commands.api.command;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mccommons.commands.api.annotation.*;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCommandParser<C> {
    protected final AbstractCommand<?> mccCommand;
    protected final Class<? extends AbstractCommand> mccClazz;
    protected final ConfigWrapper configWrapper;

    protected AbstractCommandParser(final AbstractCommand<?> mccCommand, final ConfigWrapper configWrapper) {
        this.mccCommand = mccCommand;
        this.mccClazz = mccCommand.getClass();
        this.configWrapper = configWrapper;
    }


    public abstract C toCommand();

    // ----------------------------------------------------------------------------------------------------
    // Methods to make the work of the subclasses easier
    // ----------------------------------------------------------------------------------------------------

    protected boolean isAnnotationPresent(final Class<? extends Annotation> annotation) {
        return mccClazz.isAnnotationPresent(annotation);
    }

    protected <A extends Annotation> A getAnnotation(final Class<A> clazz) {
        return mccClazz.getAnnotation(clazz);
    }

    protected Command getCommandAnnotation() {
        final Command commandAnnotation = getAnnotation(Command.class);

        if (commandAnnotation == null) {
            throw new IllegalStateException(
                "@Command Annotation wasn't not found at " + mccClazz.getName() +
                    " Make sure to place it above your command class!");
        }

        return commandAnnotation;
    }

    @SneakyThrows
    protected Method getMainCommandMethod() {
        return mccClazz.getMethod("onCommand", CommandInput.class);
    }

    protected List<Method> getSubCommandMethods() {
        final List<Method> methods = new ArrayList<>();
        for (final Method method : getClass().getMethods()) {
            if (method.getName().equals("onCommand")) {
                continue;
            }

            if (!method.isAnnotationPresent(SubCommand.class)) {
                continue;
            }

            methods.add(method);
        }
        return methods;
    }

    protected Optional<String> getPermissionOfMethod(final Method method) {
        if (!method.isAnnotationPresent(Permission.class)) {
            return Optional.empty();
        }

        final Permission permission = method.getAnnotation(Permission.class);

        return Optional.of(permission.value());
    }

    protected boolean isInGameOnly(final Method method) {
        return method.isAnnotationPresent(InGameOnly.class);
    }

    protected int maxArguments(final Method method) {
        if (!method.isAnnotationPresent(CommandArgs.class)) {
            return -1;
        }

        final CommandArgs commandArgs = method.getAnnotation(CommandArgs.class);

        return commandArgs.maxArgsLength();
    }

    protected int minArguments(final Method method) {
        if (!method.isAnnotationPresent(CommandArgs.class)) {
            return -1;
        }

        final CommandArgs commandArgs = method.getAnnotation(CommandArgs.class);
        return commandArgs.minArgsLength();
    }
}
