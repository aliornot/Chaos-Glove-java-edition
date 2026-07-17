package com.example.cubecrosshair.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.class_2540;
import net.minecraft.class_2960;

/**
 * Same pattern as ChaosGloveClientNetwork:
 * PacketByteBufs.create(); buf.writeString(mode); ClientPlayNetworking.send(id, buf);
 * method_10814 returns PacketByteBuf (not void).
 */
@Environment(EnvType.CLIENT)
public class StarLauncherClientNetwork {
	public static final class_2960 CHANGE_STAR_MODE_PACKET_ID =
		new class_2960("chaos_glove", "change_star_mode");

	public static void sendChangeModePacket(String mode) {
		class_2540 buf = PacketByteBufs.create();
		buf.method_10814(mode); // returns PacketByteBuf — must not be void in bytecode
		ClientPlayNetworking.send(CHANGE_STAR_MODE_PACKET_ID, buf);
	}
}
