package com.bobotweaks.inhabitants.items;

import com.bobotweaks.inhabitants.init.ModDataComponentTypes;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

public class SpikeDrillItem extends Item {
    public static final int BASE_TEMPERATURE_MAX = 120;
    public static final int OVERHEAT_COOLDOWN_TICKS = 40;

    public SpikeDrillItem(Settings settings) {
        super(settings.maxCount(1).component(ModDataComponentTypes.TEMPERATURE, 0).component(ModDataComponentTypes.LAST_TICK, 0L));
    }

    public static int getTemperatureMax(ItemStack stack) {
        // TODO: Port Enchantments (Thermal Capacity)
        return BASE_TEMPERATURE_MAX;
    }

    public static int getTemperature(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.TEMPERATURE, 0);
    }

    public static void setTemperature(ItemStack stack, int temperature) {
        stack.set(ModDataComponentTypes.TEMPERATURE, Math.max(0, Math.min(getTemperatureMax(stack), temperature)));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack drill = user.getStackInHand(hand);
        
        // Overheat check
        if (getTemperature(drill) >= getTemperatureMax(drill)) {
            user.getItemCooldownManager().set(drill, OVERHEAT_COOLDOWN_TICKS);
            if (world instanceof ServerWorld serverWorld) {
                user.damage(serverWorld, world.getDamageSources().onFire(), 2.0f);
                world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            user.sendMessage(Text.translatable("tooltip.inhabitants.spike_drill.overheated").formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        user.setCurrentHand(hand);
        drill.set(ModDataComponentTypes.LAST_TICK, world.getTime());
        return ActionResult.CONSUME;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (world.isClient()) return;

        int temperature = getTemperature(stack);
        if (temperature > 0) {
            long lastUsed = stack.getOrDefault(ModDataComponentTypes.LAST_TICK, 0L);
            long currentTime = world.getTime();
            long elapsed = currentTime - lastUsed;

            // Passive cooling: every 20 ticks after 60 ticks of inactivity
            if (elapsed >= 60 && (elapsed - 60) % 20 == 0) {
                setTemperature(stack, temperature - 1);
            }
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (otherStack.isOf(Items.SNOWBALL) && getTemperature(stack) > 0) {
            World world = player.getEntityWorld();
            if (!player.getItemCooldownManager().isCoolingDown(stack)) {
                setTemperature(stack, getTemperature(stack) - 30);
                if (!player.getAbilities().creativeMode) {
                    otherStack.decrement(1);
                }

                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
                player.getItemCooldownManager().set(stack, 20);
                return true;
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        int temp = getTemperature(stack);
        int max = getTemperatureMax(stack);
        Formatting color = temp >= max ? Formatting.RED : Formatting.GOLD;

        textConsumer.accept(Text.translatable("tooltip.inhabitants.spike_drill.temperature", temp, max).formatted(color));
        textConsumer.accept(Text.translatable("tooltip.inhabitants.spike_drill").formatted(Formatting.GRAY));
    }
}
