package com.example.cubecrosshair;

import com.example.cubecrosshair.item.StarLauncherItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_7923;
import net.minecraft.server.MinecraftServer;

public class StarLauncherBootstrap implements ModInitializer {
	public static final class_2960 CHANGE_STAR_MODE_PACKET_ID =
		new class_2960("chaos_glove", "change_star_mode");

	@Override
	public void onInitialize() {
		// Explicit handler class - NO lambdas (avoids bad synthetic descriptors)
		ServerPlayNetworking.registerGlobalReceiver(
			CHANGE_STAR_MODE_PACKET_ID,
			new ChangeStarModeHandler()
		);
	}

	public static final class ChangeStarModeHandler implements ServerPlayNetworking.PlayChannelHandler {
		@Override
		public void receive(MinecraftServer server, class_3222 player, class_3244 handler,
				class_2540 buf, PacketSender responseSender) {
			final String modeRaw = buf.method_19772();
			server.execute(new ApplyModeTask(player, modeRaw));
		}
	}

	public static final class ApplyModeTask implements Runnable {
		private final class_3222 player;
		private final String modeRaw;

		public ApplyModeTask(class_3222 player, String modeRaw) {
			this.player = player;
			this.modeRaw = modeRaw;
		}

		@Override
		public void run() {
			class_1799 stack = player.method_5998(class_1268.field_5808);
			if (!isStarLauncherStack(stack)) {
				stack = player.method_5998(class_1268.field_5810);
			}
			if (!isStarLauncherStack(stack)) {
				return;
			}
			String mode = isValidMode(modeRaw) ? modeRaw : StarLauncherItem.MODE_NONE;
			StarLauncherItem.ensureManaInit(stack);
			StarLauncherItem.setMode(stack, mode);
			player.method_7353(
				class_2561.method_43470(
					"\u00a7d[\u0417\u0432\u0451\u0437\u0434\u043d\u044b\u0439 \u0432\u044b\u043f\u0443\u0441\u043a\u0430\u0442\u0435\u043b\u044c] \u00a7r\u0420\u0435\u0436\u0438\u043c: "
						+ StarLauncherItem.getModeShortName(mode)
				),
				true
			);
		}
	}

	public static boolean isStarLauncherStack(class_1799 stack) {
		if (stack == null || stack.method_7960()) return false;
		if (stack.method_7909() instanceof StarLauncherItem) return true;
		try {
			class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
			return id != null
				&& "chaos_glove".equals(id.method_12836())
				&& "star_launcher".equals(id.method_12832());
		} catch (Throwable t) {
			return false;
		}
	}

	private static boolean isValidMode(String mode) {
		if (mode == null) return false;
		return mode.equals(StarLauncherItem.MODE_FIRE)
			|| mode.equals(StarLauncherItem.MODE_WATER)
			|| mode.equals(StarLauncherItem.MODE_EARTH)
			|| mode.equals(StarLauncherItem.MODE_STAR)
			|| mode.equals(StarLauncherItem.MODE_LIGHT)
			|| mode.equals(StarLauncherItem.MODE_DARK);
	}
}
