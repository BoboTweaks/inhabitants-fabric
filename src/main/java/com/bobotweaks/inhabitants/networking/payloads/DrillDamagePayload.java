package com.bobotweaks.inhabitants.networking.payloads;

import com.bobotweaks.inhabitants.Inhabitants;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DrillDamagePayload() implements CustomPayload {

    public static final Id<DrillDamagePayload> ID =
        new Id<>(Identifier.of(Inhabitants.MOD_ID, "drill_damage"));

    public static final PacketCodec<RegistryByteBuf, DrillDamagePayload> CODEC =
        PacketCodec.unit(new DrillDamagePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
