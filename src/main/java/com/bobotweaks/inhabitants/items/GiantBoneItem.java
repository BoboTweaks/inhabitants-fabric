package com.bobotweaks.inhabitants.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class GiantBoneItem extends Item {

    private static final float SHOCKWAVE_RADIUS = 9;
    private static final float SHOCKWAVE_DAMAGE = 20f;
    private static final int SHOCKWAVE_DURATION = 40;
    private static final int COOLDOWN = 100;

    public GiantBoneItem(Settings settings) {
        super(settings.sword(ToolMaterial.NETHERITE, 7, -3.5f).maxCount(1));
    }

    @Override
    public void appendTooltip(
        ItemStack stack,
        TooltipContext context,
        TooltipDisplayComponent displayComponent,
        Consumer<Text> textConsumer,
        TooltipType type
    ) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        textConsumer.accept(Text.literal("  "));
        textConsumer.accept(Text.translatable("tooltip.inhabitants.special_effect").formatted(Formatting.WHITE));
        textConsumer.accept(Text.literal("  • ").append(Text.translatable("tooltip.inhabitants.giant_bone")).formatted(Formatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (world.isClient() || !(entity instanceof PlayerEntity player)) return;
        
        if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) return;

        if (!player.hasStatusEffect(StatusEffects.STRENGTH)) {
            StatusEffectInstance current = player.getStatusEffect(StatusEffects.SLOWNESS);

            if (current == null || current.getAmplifier() < 1 || current.getDuration() <= 10) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1, true, false, true));
            }
        }
    }

    // TODO: fabric doesn't have canDisableShield on Item
    /*
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }
    */

    public static float getShockwaveRadius() { return SHOCKWAVE_RADIUS; }
    public static float getShockwaveDamage() { return SHOCKWAVE_DAMAGE; }
    public static int getShockwaveDuration() { return SHOCKWAVE_DURATION; }
    public static int getCooldown() { return COOLDOWN; }
}
