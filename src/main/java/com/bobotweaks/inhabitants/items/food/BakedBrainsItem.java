package com.bobotweaks.inhabitants.items.food;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class BakedBrainsItem extends Item {

    public static final FoodComponent FOOD_COMPONENT = new FoodComponent.Builder()
            .nutrition(4)
            .saturationModifier(0.3f)
            .alwaysEdible()
            .build();

    public static final ConsumableComponent CONSUMABLE = ConsumableComponent.builder()
            .consumeEffect(new ApplyEffectsConsumeEffect(
                    new StatusEffectInstance(ModEffects.UNDEAD_DISGUISE, 1800, 0), 1.0f))
            .build();

    public BakedBrainsItem(Settings settings) {
        super(settings.maxCount(1).food(FOOD_COMPONENT, CONSUMABLE));
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
