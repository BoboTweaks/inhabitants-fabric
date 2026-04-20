package com.bobotweaks.inhabitants.client.audio;

// import com.bobotweaks.inhabitants.effects.ModEffects;
import com.bobotweaks.inhabitants.init.ModSoundEvents;
import com.bobotweaks.inhabitants.items.SpikeDrillItem;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

public class ModTickableSounds {

    public static class DrillLoop extends MovingSoundInstance {
        private final ClientPlayerEntity player;
        public static DrillLoop currentSound = null;
        private int stopGrace = 0;
        private static final int STOP_GRACE_TICKS = 4;

        public DrillLoop(ClientPlayerEntity player) {
            super(ModSoundEvents.DRILL_LOOP, SoundCategory.PLAYERS, SoundInstance.createRandom());
            this.player = player;
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 0.25f;
            this.pitch = 1.0f;
            this.relative = true;
        }

        @Override
        public void tick() {
            if (!this.player.isAlive()) {
                this.stopLoop();
                return;
            }
            boolean drilling = this.player.isUsingItem() &&
                    this.player.getActiveItem().getItem() instanceof SpikeDrillItem;
            if (!drilling) {
                if (++stopGrace > STOP_GRACE_TICKS) {
                    this.stopLoop();
                }
            } else {
                stopGrace = 0;
            }
        }

        public void stopLoop() {
            this.setDone();
            if (currentSound == this) {
                currentSound = null;
            }
        }
    }

    public static class ConcussionBuzz extends MovingSoundInstance {
        private final ClientPlayerEntity player;

        public ConcussionBuzz(ClientPlayerEntity player) {
            super(ModSoundEvents.CONCUSSION_BUZZ, SoundCategory.AMBIENT, SoundInstance.createRandom());
            this.player = player;
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 3.0F;
            this.pitch = 1.25F;
            this.relative = true;
        }

        @Override
        public void tick() {
            if (!this.player.isAlive()) { // TODO || !this.player.hasStatusEffect(ModEffects.CONCUSSION)
                this.setDone();
            }
        }
    }

    public static class ImmaterialInsideLoop extends MovingSoundInstance {
        private final ClientPlayerEntity player;

        public ImmaterialInsideLoop(ClientPlayerEntity player) {
            super(ModSoundEvents.IMMATERIAL_INSIDE, SoundCategory.AMBIENT, SoundInstance.createRandom());
            this.player = player;
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 5.0F;
            this.pitch = 1.0F;
            this.relative = true;
        }

        @Override
        public void tick() {
            if (!this.player.isAlive()) { // TODO || !this.player.hasStatusEffect(ModEffects.IMMATERIAL)
                this.setDone();
            }
        }

        public void stopSound() {
            this.setDone();
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }
    }
}
