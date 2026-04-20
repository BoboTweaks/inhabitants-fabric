package com.bobotweaks.inhabitants.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class AbracadabraParticle extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected AbracadabraParticle(
        ClientWorld world,
        double x, double y, double z,
        double velocityX, double velocityY, double velocityZ,
        SpriteProvider spriteProvider
    ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getSprite(0, 1));

        this.spriteProvider = spriteProvider;
        this.velocityMultiplier = 0.96F;
        this.gravityStrength = -0.1F;

        this.velocityX *= 0.1;
        this.velocityY *= 0.1;
        this.velocityZ *= 0.1;
        
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;

        this.scale *= 2.5F;
        this.maxAge = 20 + this.random.nextInt(10);
        this.updateSprite(spriteProvider);
        
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
    }

    @Override
    public void tick() {
        super.tick();
        this.updateSprite(this.spriteProvider);
        this.alpha = 1.0f - ((float)this.age / (float)this.maxAge);
    }

    @Override
    public @NotNull BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(
            @NotNull SimpleParticleType type,
            @NotNull ClientWorld level,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ,
            Random random
        ) {
            return new AbracadabraParticle(
                level, x, y, z,
                velocityX, velocityY, velocityZ,
                this.spriteProvider
            );
        }
    }
}
