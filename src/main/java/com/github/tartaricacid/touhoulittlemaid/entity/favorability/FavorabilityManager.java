package com.github.tartaricacid.touhoulittlemaid.entity.favorability;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.SpawnParticleMessage;
import com.google.common.collect.Maps;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import java.util.Map;

public class FavorabilityManager {
    public static final Map<String, Type> TYPES = Maps.newHashMap();

    private static final int LEVEL_0 = 0;
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;

    private static final int LEVEL_0_POINT = 0;
    private static final int LEVEL_1_POINT = 64;
    private static final int LEVEL_2_POINT = 192;
    private static final int LEVEL_3_POINT = 384;

    private static final int LEVEL_0_HEALTH = 20;
    private static final int LEVEL_1_HEALTH = 30;
    private static final int LEVEL_2_HEALTH = 40;
    private static final int LEVEL_3_HEALTH = 80;

    private static final int LEVEL_0_ATTACK_DAMAGE = 2;
    private static final int LEVEL_1_ATTACK_DAMAGE = 2;
    private static final int LEVEL_2_ATTACK_DAMAGE = 5;
    private static final int LEVEL_3_ATTACK_DAMAGE = 10;

    private static final String TAG_NAME = "FavorabilityManagerCounter";

    private final Map<String, Time> counter;
    private final EntityMaid maid;

    public FavorabilityManager(EntityMaid maid) {
        this.counter = Maps.newHashMap();
        this.maid = maid;
    }

    public void tick() {
        counter.values().forEach(Time::tick);
    }

    public void apply(String type) {
        Type typeInstance = TYPES.get(type);
        if (typeInstance != null) {
            this.apply(typeInstance);
        }
    }

    public void apply(Type type) {
        if (this.canAdd(type.getTypeName())) {
            if (type.isReduce()) {
                this.reduce(type.getPoint());
            } else {
                this.add(type.getPoint());
            }
            this.addCooldown(type.getTypeName(), type.getCooldown());
        }
    }

    private void addCooldown(String type, int tickCount) {
        this.counter.put(type, new Time(tickCount));
    }

    private boolean canAdd(String type) {
        if (this.counter.containsKey(type)) {
            return this.counter.get(type).isZero();
        }
        return true;
    }

    public int getLevel() {
        return this.getLevel(maid.getFavorability());
    }

    private int getLevel(int favorability) {
        if (favorability < LEVEL_1_POINT) {
            return LEVEL_0;
        } else if (favorability < LEVEL_2_POINT) {
            return LEVEL_1;
        } else if (favorability < LEVEL_3_POINT) {
            return LEVEL_2;
        } else {
            return LEVEL_3;
        }
    }

    public double getLevelPercent() {
        int favorability = this.maid.getFavorability();
        if (favorability < LEVEL_1_POINT) {
            return favorability / (double) LEVEL_1_POINT;
        } else if (favorability < LEVEL_2_POINT) {
            return (favorability - LEVEL_1_POINT) / (double) (LEVEL_2_POINT - LEVEL_1_POINT);
        } else if (favorability < LEVEL_3_POINT) {
            return (favorability - LEVEL_2_POINT) / (double) (LEVEL_3_POINT - LEVEL_2_POINT);
        } else {
            return 0;
        }
    }

    public int nextLevelPoint() {
        int level = this.getLevel();
        if (level == LEVEL_3) {
            return 0;
        }
        int pointByLevel = getPointByLevel(level + 1);
        return pointByLevel - this.maid.getFavorability();
    }

    public int getHealthByLevel(int level) {
        switch (level) {
            case LEVEL_1:
                return LEVEL_1_HEALTH;
            case LEVEL_2:
                return LEVEL_2_HEALTH;
            case LEVEL_3:
                return LEVEL_3_HEALTH;
            default:
                return LEVEL_0_HEALTH;
        }
    }

    public int getAttackByLevel(int level) {
        switch (level) {
            case LEVEL_1:
                return LEVEL_1_ATTACK_DAMAGE;
            case LEVEL_2:
                return LEVEL_2_ATTACK_DAMAGE;
            case LEVEL_3:
                return LEVEL_3_ATTACK_DAMAGE;
            default:
                return LEVEL_0_ATTACK_DAMAGE;
        }
    }

    public int getPointByLevel(int level) {
        switch (level) {
            case LEVEL_1:
                return LEVEL_1_POINT;
            case LEVEL_2:
                return LEVEL_2_POINT;
            case LEVEL_3:
                return LEVEL_3_POINT;
            default:
                return LEVEL_0_POINT;
        }
    }

    public void add(int addPoint) {
        int favorability = maid.getFavorability();
        int levelBefore = getLevel();
        int result = MathHelper.clamp(favorability + addPoint, 0, LEVEL_3_POINT);
        maid.setFavorability(result);
        int levelAfter = getLevel();
        if (levelBefore < levelAfter) {
            ModifiableAttributeInstance attack = maid.getAttribute(Attributes.ATTACK_DAMAGE);
            ModifiableAttributeInstance health = maid.getAttribute(Attributes.MAX_HEALTH);
            if (attack != null) {
                attack.setBaseValue(this.getAttackByLevel(levelAfter));
            }
            if (health != null) {
                if (maid.isStruckByLightning()) {
                    health.setBaseValue(this.getHealthByLevel(levelAfter) + 20);
                } else {
                    health.setBaseValue(this.getHealthByLevel(levelAfter));
                }
                if (maid.getHealth() > maid.getMaxHealth()) {
                    maid.setHealth(maid.getMaxHealth());
                }
            }
            maid.playSound(SoundEvents.PLAYER_LEVELUP, 0.5F, maid.getRandom().nextFloat() * 0.1F + 0.9F);
        }
        NetworkHandler.sendToNearby(maid, new SpawnParticleMessage(maid.getId(), SpawnParticleMessage.Type.HEART));
    }

    public void reduce(int reducePoint) {
        int favorability = maid.getFavorability();
        int pointByLevel = getPointByLevel(getLevel());
        int result = MathHelper.clamp(favorability - reducePoint, pointByLevel, LEVEL_3_POINT);
        maid.setFavorability(result);
    }

    public void reduceWithoutLevel(int reducePoint) {
        int favorability = maid.getFavorability();
        int levelBefore = getLevel();
        int result = MathHelper.clamp(favorability - reducePoint, 0, LEVEL_3_POINT);
        maid.setFavorability(result);
        int levelAfter = getLevel();
        if (levelBefore > levelAfter) {
            ModifiableAttributeInstance attack = maid.getAttribute(Attributes.ATTACK_DAMAGE);
            ModifiableAttributeInstance health = maid.getAttribute(Attributes.MAX_HEALTH);
            if (attack != null) {
                attack.setBaseValue(this.getAttackByLevel(levelAfter));
            }
            if (health != null) {
                if (maid.isStruckByLightning()) {
                    health.setBaseValue(this.getHealthByLevel(levelAfter) + 20);
                } else {
                    health.setBaseValue(this.getHealthByLevel(levelAfter));
                }
                if (maid.getHealth() > maid.getMaxHealth()) {
                    maid.setHealth(maid.getMaxHealth());
                }
            }
        }
    }

    public void max() {
        this.add(LEVEL_3_POINT);
    }

    public void addAdditionalSaveData(CompoundNBT compound) {
        CompoundNBT data = new CompoundNBT();
        this.counter.forEach((name, time) -> data.putInt(name, time.getTickCount()));
        compound.put(TAG_NAME, data);
    }

    public void readAdditionalSaveData(CompoundNBT compound) {
        if (compound.contains(TAG_NAME, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT data = compound.getCompound(TAG_NAME);
            for (String name : data.getAllKeys()) {
                this.counter.put(name, new Time(data.getInt(name)));
            }
        }
    }

    public static class Time {
        private int tickCount;

        public Time(int tickCount) {
            this.tickCount = tickCount;
        }

        public int getTickCount() {
            return tickCount;
        }

        public void setTickCount(int tickCount) {
            this.tickCount = tickCount;
        }

        public void tick() {
            if (tickCount > 0) {
                tickCount--;
            }
        }

        public boolean isZero() {
            return this.tickCount <= 0;
        }
    }
}
