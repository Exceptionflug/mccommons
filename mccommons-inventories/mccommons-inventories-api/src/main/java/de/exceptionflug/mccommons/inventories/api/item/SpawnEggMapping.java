package de.exceptionflug.mccommons.inventories.api.item;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.StringTag;

public class SpawnEggMapping extends AbstractCustomMapping {

    private final String entityType;

    public SpawnEggMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final String entityType) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd, 383);
        this.entityType = entityType;
    }

    @Override
    public void apply(final ItemStackWrapper stack, final int protocolVersion) {
        final CompoundTag nbt = stack.getNBT();
        if(nbt != null) {
            CompoundTag entityData = (CompoundTag) nbt.getValue().get("EntityTag");
            if(entityData == null)
                entityData = new CompoundTag("EntityTag", new CompoundMap());
            entityData.getValue().put(new StringTag("id", entityType));
            stack.setNBT(entityData);
        }
    }

    @Override
    public boolean isApplicable(final ItemStackWrapper stack, final int version, final int id, final int durability) {
        if(id != 383)
            return false;
        final CompoundTag nbt = stack.getNBT();
        if(nbt != null) {
            final CompoundTag entityData = (CompoundTag) nbt.getValue().get("EntityTag");
            if(entityData == null)
                return false;
            final StringTag tag = (StringTag) entityData.getValue().get("id");
            if(tag == null)
                return false;
            return tag.getValue().equals(entityType);
        }
        return false;
    }


}
