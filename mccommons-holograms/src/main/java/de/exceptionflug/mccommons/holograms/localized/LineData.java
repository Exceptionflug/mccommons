package de.exceptionflug.mccommons.holograms.localized;

import org.bukkit.inventory.ItemStack;

public class LineData {

    private final Object content;
    private final String defaultMessage;
    private final PlayerboundReplacementSupplier replacementSupplier;

    public LineData(final String messageKey, final String defaultMessage, final PlayerboundReplacementSupplier replacementSupplier) {
        this.content = messageKey;
        this.defaultMessage = defaultMessage;
        this.replacementSupplier = replacementSupplier;
    }

    public LineData(final ItemStack itemStack) {
        this.content = itemStack;
        this.defaultMessage = null;
        this.replacementSupplier = null;
    }

    public Object getContent() {
        return content;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public PlayerboundReplacementSupplier getReplacementSupplier() {
        return replacementSupplier;
    }
}
