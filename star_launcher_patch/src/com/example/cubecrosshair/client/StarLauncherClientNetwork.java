package com.example.cubecrosshair.client;

import com.example.cubecrosshair.item.StarLauncherItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_310;

@Environment(EnvType.CLIENT)
public class StarLauncherClientNetwork {
	public static final class_2960 CHANGE_STAR_MODE_PACKET_ID =
		new class_2960("chaos_glove", "change_star_mode");

	public static void sendChangeModePacket(String mode) {
		// 1) Immediate client-side write so HUD updates right away
		try {
			class_310 client = class_310.method_1551();
			if (client != null && client.field_1724 != null) {
				class_1799 main = client.field_1724.method_5998(class_1268.field_5808);
				class_1799 off = client.field_1724.method_5998(class_1268.field_5810);
				if (StarLauncherItem.isStarLauncher(main)) {
					StarLauncherItem.setMode(main, mode);
				} else if (StarLauncherItem.isStarLauncher(off)) {
					StarLauncherItem.setMode(off, mode);
				} else {
					StarLauncherItem.clientSelectedMode = mode;
				}
			} else {
				StarLauncherItem.clientSelectedMode = mode;
			}
		} catch (Throwable t) {
			StarLauncherItem.clientSelectedMode = mode;
		}

		// 2) Server packet (same as ChaosGloveClientNetwork)
		class_2540 buf = PacketByteBufs.create();
		buf.method_10814(mode);
		ClientPlayNetworking.send(CHANGE_STAR_MODE_PACKET_ID, buf);
	}
}
