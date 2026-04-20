package com.bobotweaks.inhabitants.items.food;

import com.bobotweaks.inhabitants.init.ModParticleTypes;
import com.bobotweaks.inhabitants.init.ModSoundEvents;
import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.component.type.FoodComponent;

public class UncannyPottageItem extends Item {
    public static final FoodComponent FOOD =
        new FoodComponent.Builder()
            .nutrition(5)
            .saturationModifier(0.6f)
            .alwaysEdible()
            .build();

    public UncannyPottageItem(Item.Settings settings) {
        super(settings.maxCount(1).food(FOOD).useRemainder(Items.BOWL));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            boolean hasEffectAlready = user.hasStatusEffect(ModEffects.REVERSE_GROWTH);
            
            if (!hasEffectAlready) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), 
                        ModSoundEvents.REVERSE_GROWTH, SoundCategory.PLAYERS, 1.0F, 1.0F);
                
                if (world instanceof ServerWorld serverWorld) {

                    for (int i = 0; i < 20; i++) {
                        double px = user.getX() + (world.random.nextDouble() - 0.5) * 1.0;
                        double py = user.getY() + world.random.nextDouble() * user.getHeight();
                        double pz = user.getZ() + (world.random.nextDouble() - 0.5) * 1.0;

                        serverWorld.spawnParticles(ModParticleTypes.ABRACADABRA, px, py, pz, 1, 0, 0, 0, 0.05);
                    }
                }
            }
        }

        user.addStatusEffect(new StatusEffectInstance(ModEffects.REVERSE_GROWTH, 1800, 0, false, true, true));
        
        ItemStack result = super.finishUsing(stack, world, user);
        if (user instanceof PlayerEntity player &&
            player.getAbilities().creativeMode) {
                
            return result;
        }

        return new ItemStack(Items.BOWL);
    }
}
