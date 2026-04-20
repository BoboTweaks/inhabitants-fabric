package com.bobotweaks.inhabitants.items.food;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.component.type.FoodComponent;

public class BakedBrainsItem extends Item {
    public static final FoodComponent FOOD_COMPONENT = new FoodComponent.Builder()
            .nutrition(4)
            .saturationModifier(0.3f)
            .alwaysEdible()
            .build();

    public BakedBrainsItem(Settings settings) {
        super(settings.maxCount(1).food(FOOD_COMPONENT));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (user instanceof PlayerEntity player && player.getAbilities().creativeMode) {
            return result;
        }

        return new ItemStack(Items.SKELETON_SKULL);
    }
}
