package com.bobotweaks.inhabitants.items.food;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class DimensionalServingItem extends Item {
    public static final FoodComponent FOOD = new FoodComponent.Builder()
        .nutrition(2)
        .saturationModifier(0.2f)
        .alwaysEdible()
        .build();

    public DimensionalServingItem(Settings settings) {
        super(settings.maxCount(1).food(FOOD).useRemainder(Items.BOWL));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            user.addStatusEffect(new StatusEffectInstance(ModEffects.IMMATERIAL, 1800));
        }

        ItemStack result = super.finishUsing(stack, world, user);
        
        if (user instanceof PlayerEntity player && player.getAbilities().creativeMode) {
            return result;
        }

        return new ItemStack(Items.BOWL);
    }
}
