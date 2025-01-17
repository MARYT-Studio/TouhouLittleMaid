package com.github.tartaricacid.touhoulittlemaid.compat.jei;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.backpack.CraftingTableBackpackContainerScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.backpack.FurnaceBackpackContainerScreen;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.altar.AltarRecipeCategory;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.altar.AltarRecipeMaker;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.altar.EntityPlaceholderSubtype;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.CraftingTableBackpackContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.FurnaceBackpackContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class MaidPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(TouhouLittleMaid.MOD_ID, "jei");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AltarRecipeMaker.getInstance().getAltarRecipes(), AltarRecipeCategory.UID);
        registration.addIngredientInfo(InitItems.GARAGE_KIT.get().getDefaultInstance(), VanillaTypes.ITEM, "jei.touhou_little_maid.garage_kit.info");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(InitItems.HAKUREI_GOHEI.get().getDefaultInstance(), AltarRecipeCategory.UID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(InitItems.ENTITY_PLACEHOLDER.get(), new EntityPlaceholderSubtype());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CraftingTableBackpackContainer.class, VanillaRecipeCategoryUid.CRAFTING, 71, 9, 0, 70);
        registration.addRecipeTransferHandler(FurnaceBackpackContainer.class, VanillaRecipeCategoryUid.FURNACE, 70, 1, 0, 70);
        registration.addRecipeTransferHandler(FurnaceBackpackContainer.class, VanillaRecipeCategoryUid.FUEL, 71, 1, 0, 70);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CraftingTableBackpackContainerScreen.class, 213, 121, 13, 12, VanillaRecipeCategoryUid.CRAFTING);
        registration.addRecipeClickArea(FurnaceBackpackContainerScreen.class, 183, 118, 28, 24, VanillaRecipeCategoryUid.FURNACE, VanillaRecipeCategoryUid.FUEL);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
