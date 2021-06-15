package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor.MaidHostilesSensor;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class InitEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, TouhouLittleMaid.MOD_ID);

    public static RegistryObject<EntityType<EntityMaid>> MAID = ENTITY_TYPES.register("maid", () -> EntityMaid.TYPE);
    public static RegistryObject<EntityType<EntityChair>> CHAIR = ENTITY_TYPES.register("chair", () -> EntityChair.TYPE);

    public static RegistryObject<Attribute> HUNGER = ATTRIBUTES.register("maid.hunger", () -> (new RangedAttribute("attribute.name.maid.hunger", 0.0D, -1024.0D, 1024.0D)).setSyncable(true));
    public static RegistryObject<Attribute> FAVORABILITY = ATTRIBUTES.register("maid.favorability", () -> (new RangedAttribute("attribute.name.maid.favorability", 0.0D, -2147483648.0D, 2147483647.0D)).setSyncable(true));
    public static RegistryObject<Attribute> EXPERIENCE = ATTRIBUTES.register("maid.experience", () -> (new RangedAttribute("attribute.name.maid.experience", 0.0D, 0.0D, 2147483647.0D)).setSyncable(true));

    public static RegistryObject<SensorType<MaidHostilesSensor>> MAID_HOSTILES_SENSOR = SENSOR_TYPES.register("maid_hostiles", () -> new SensorType<>(MaidHostilesSensor::new));

    @SubscribeEvent
    public static void addEntityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EntityMaid.TYPE, EntityMaid.createMaidAttributes().build());
        event.put(EntityChair.TYPE, LivingEntity.createLivingAttributes().build());
    }
}