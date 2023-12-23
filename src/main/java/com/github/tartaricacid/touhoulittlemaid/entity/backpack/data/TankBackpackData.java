package com.github.tartaricacid.touhoulittlemaid.entity.backpack.data;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IBackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TankBackpackData extends SimpleContainer implements IBackpackData {
    public static final int CAPACITY = 10 * FluidType.BUCKET_VOLUME;
    private static final int INPUT_INDEX = 0;
    private static final int OUTPUT_INDEX = 1;
    private final FluidTank tank = new FluidTank(CAPACITY);
    private final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            if (index == 0) {
                TankBackpackData.this.tank.getFluidAmount();
            }
            return 0;
        }

        public void set(int index, int value) {
            if (index == 0) {
                FluidStack fluid = TankBackpackData.this.tank.getFluid();
                if (!fluid.isEmpty()) {
                    fluid.setAmount(value);
                }
            }
        }

        public int getCount() {
            return 2;
        }
    };

    public TankBackpackData() {
        super(2);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
    }

    @Override
    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public void load(CompoundTag tag, EntityMaid maid) {
        tank.readFromNBT(tag.getCompound("Tanks"));
        this.fromTag(tag.getList("Items", Tag.TAG_COMPOUND));
    }

    @Override
    public void save(CompoundTag tag, EntityMaid maid) {
        tag.put("Tanks", tank.writeToNBT(new CompoundTag()));
        tag.put("Items", this.createTag());
    }

    @Override
    public void serverTick(EntityMaid maid) {
    }
}
