package com.bobotweaks.inhabitants.networking.payloads;

import com.bobotweaks.inhabitants.Inhabitants;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AscendPayload() implements CustomPayload {
    public static final Id<AscendPayload> ID = new Id<>(Identifier.of(Inhabitants.MOD_ID, "ascend"));
    public static final PacketCodec<RegistryByteBuf, AscendPayload> CODEC = PacketCodec.unit(new AscendPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
