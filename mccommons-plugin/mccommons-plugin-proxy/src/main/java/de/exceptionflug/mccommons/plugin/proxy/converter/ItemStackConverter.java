package de.exceptionflug.mccommons.plugin.proxy.converter;

import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.mccommons.inventories.proxy.utils.ItemUtils;
import de.exceptionflug.protocolize.items.ItemFlag;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ProxyServer;

public class ItemStackConverter implements Converter<ConfigItemStack, ItemStack> {

    @Override
    public ItemStack convert(final ConfigItemStack src) {
        ItemType protoMat;
        try {
            protoMat = ItemType.valueOf(src.getType());
        } catch (final IllegalArgumentException e) {
            ProxyServer.getInstance().getLogger().warning("[ItemStackConverter] WARN: "+src.getDisplayName()+" has invalid type "+src.getType());
            protoMat = ItemType.GRASS;
        }
        final ItemStack out = new ItemStack(protoMat, src.getAmount());
        if(protoMat == ItemType.PLAYER_HEAD) {
            String skullOwner = src.getSkull() == null ? "Exceptionflug" : src.getSkull();
            if(skullOwner.startsWith("texture:")) {
                final String[] spl = skullOwner.split(":");
                if(spl.length == 2) {
                    ItemUtils.setSkullTexture(out, spl[1]);
                }
            } else {
                ItemUtils.setSkullAndName(out, skullOwner);
            }
        }
        if(!src.getEnchantments().isEmpty())
            ItemUtils.addGlow(out);
        for(final String flagName : src.getFlags()) {
            try {
                out.setFlag(ItemFlag.valueOf(flagName), true);
            } catch (final Exception e) {
                ProxyServer.getInstance().getLogger().warning("[ItemStackConverter] WARN: "+src.getDisplayName()+" has invalid flag "+flagName);
            }
        }
        out.setLore(FormatUtils.formatAmpersandColorCodes(src.getLore()));
        out.setDisplayName(src.getDisplayName());
        return out;
    }

}
