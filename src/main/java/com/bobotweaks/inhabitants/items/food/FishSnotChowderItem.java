package com.bobotweaks.inhabitants.items.food;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.component.type.FoodComponent;

public class FishSnotChowderItem extends Item {
    public static final FoodComponent foodComponent = new FoodComponent.Builder()
        .nutrition(10)
        .saturationModifier(0.8f)
        .alwaysEdible()
        .build();

    public FishSnotChowderItem(Settings settings) {
        super(settings.maxCount(1).food(foodComponent).useRemainder(Items.BOWL));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 1, false, true, true));
        user.setAir(user.getMaxAir());

        return super.finishUsing(stack, world, user);
    }
}
