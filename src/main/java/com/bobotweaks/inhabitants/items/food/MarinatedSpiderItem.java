package com.bobotweaks.inhabitants.items.food;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;

public class MarinatedSpiderItem extends Item {
    public static final FoodComponent FOOD =
        new FoodComponent.Builder()
            .nutrition(6)
            .saturationModifier(0.6f)
            .alwaysEdible()
            .build();

    public MarinatedSpiderItem(Settings settings) {
        super(settings.maxCount(1).food(FOOD).useRemainder(Items.BOWL));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            user.addStatusEffect(new StatusEffectInstance(ModEffects.STICKY_LEGS, 1800, 0, false, true, true));
        }

        ItemStack result = super.finishUsing(stack, world, user);
        
        if (user instanceof PlayerEntity player && player.getAbilities().creativeMode) {
            return result;
        }

        return new ItemStack(Items.BOWL);
    }
}
