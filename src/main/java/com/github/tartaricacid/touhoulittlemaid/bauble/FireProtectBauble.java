package com.github.tartaricacid.touhoulittlemaid.bauble;

import com.github.tartaricacid.touhoulittlemaid.api.AbstractEntityMaid;
import com.github.tartaricacid.touhoulittlemaid.api.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author TartaricAcid
 * @date 2019/10/1 21:54
 **/
public class FireProtectBauble implements IMaidBauble {
    public FireProtectBauble() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDamage(LivingDamageEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        DamageSource source = event.getSource();
        if (entity instanceof AbstractEntityMaid && source.isFireDamage()) {
            AbstractEntityMaid maid = (AbstractEntityMaid) entity;
            int slot = LittleMaidAPI.getBaubleSlotInMaid(maid, this);
            if (slot >= 0) {
                event.setCanceled(true);
                ItemStack stack = maid.getBaubleInv().getStackInSlot(slot);
                stack.damageItem(1, maid);
                maid.getBaubleInv().setStackInSlot(slot, stack);
                if (entity.isBurning()) {
                    maid.extinguish();
                    maid.spawnExplosionParticle();
                    maid.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F,
                            1.6F + (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.4F);
                }
            }
        }
    }
}
