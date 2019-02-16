package de.exceptionflug.mccommons.core.providers;

import java.util.UUID;

public abstract class TextureProvider {

    public abstract String getSkin(final String skullOwner);
    public abstract String getSkin(final UUID skullOwner);

}
