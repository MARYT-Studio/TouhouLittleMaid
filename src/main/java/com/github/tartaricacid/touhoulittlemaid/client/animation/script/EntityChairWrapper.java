package com.github.tartaricacid.touhoulittlemaid.client.animation.script;

import com.github.tartaricacid.touhoulittlemaid.api.animation.IChairData;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import net.minecraft.world.entity.player.Player;

public final class EntityChairWrapper implements IChairData {
    private final WorldWrapper world = new WorldWrapper();
    private EntityChair chair;

    public void setData(EntityChair chair) {
        this.chair = chair;
        this.world.setData(chair.level);
    }

    public void clearData() {
        this.chair = null;
        this.world.clearData();
    }

    @Override
    public boolean isRidingPlayer() {
        return chair.getControllingPassenger() instanceof Player;
    }

    @Override
    public boolean hasPassenger() {
        return !chair.getPassengers().isEmpty();
    }

    @Override
    public float getPassengerYaw() {
        if (!chair.getPassengers().isEmpty()) {
            return chair.getPassengers().get(0).getYRot();
        }
        return 0;
    }

    @Override
    public float getYaw() {
        return chair.getYRot();
    }

    @Override
    public float getPassengerPitch() {
        if (!chair.getPassengers().isEmpty()) {
            return chair.getPassengers().get(0).getXRot();
        }
        return 0;
    }

    @Override
    public WorldWrapper getWorld() {
        return world;
    }

    @Override
    public long getSeed() {
        return Math.abs(chair.getUUID().getLeastSignificantBits());
    }

    @Override
    @Deprecated
    public int getDim() {
        return chair.getDim();
    }
}
