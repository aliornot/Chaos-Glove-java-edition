package com.example.cubecrosshair.client;

import com.example.cubecrosshair.item.StarLauncherItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;

@Environment(EnvType.CLIENT)
public class StarLauncherClient implements ClientModInitializer {
	private static class_304 openMenuKey;

	@Override
	public void onInitializeClient() {
		// J = 74 (G=71 is Chaos Glove only)
		openMenuKey = KeyBindingHelper.registerKeyBinding(
			new class_304("key.chaos_glove.star_launcher_menu", 74, "category.chaos_glove")
		);
		ClientTickEvents.END_CLIENT_TICK.register(new MenuTickHandler());
		HudRenderCallback.EVENT.register(new ManaHudHandler());
	}

	public static final class MenuTickHandler implements ClientTickEvents.EndTick {
		@Override
		public void onEndTick(class_310 client) {
			if (openMenuKey == null) return;
			while (openMenuKey.method_1436()) {
				if (client.field_1724 == null) continue;
				class_1799 main = client.field_1724.method_5998(class_1268.field_5808);
				class_1799 off = client.field_1724.method_5998(class_1268.field_5810);
				if (!StarLauncherItem.isStarLauncher(main) && !StarLauncherItem.isStarLauncher(off)) continue;
				client.method_1507((class_437) new StarLauncherScreen());
			}
		}
	}

	public static final class ManaHudHandler implements HudRenderCallback {
		@Override
		public void onHudRender(class_332 gui, float tickDelta) {
			class_310 client = class_310.method_1551();
			if (client == null || client.field_1724 == null) return;
			if (client.field_1690 != null && client.field_1690.field_1842) return;

			class_1799 main = client.field_1724.method_5998(class_1268.field_5808);
			class_1799 off = client.field_1724.method_5998(class_1268.field_5810);
			class_1799 stack = null;
			if (StarLauncherItem.isStarLauncher(main)) stack = main;
			else if (StarLauncherItem.isStarLauncher(off)) stack = off;
			if (stack == null) return;

			try { StarLauncherItem.ensureManaInit(stack); } catch (Throwable ignored) {}

			String mode = StarLauncherItem.getMode(stack);
			int mana = StarLauncherItem.getMana(stack);
			int width = client.method_22683().method_4486();
			int screenHeight = client.method_22683().method_4502();

			// Same draw API as Chaos Glove HUD
			gui.method_25300(client.field_1772, StarLauncherItem.getModeDisplayName(mode), width / 2, 10, 0xFFFFFF);

			int left = 12;
			int right = 22;
			int barHeight = 90;
			int top = screenHeight / 2 - barHeight / 2;
			int bottom = top + barHeight;

			gui.method_25294(left - 4, top - 14, right + 4, bottom + 4, 0xFF3A2614);
			gui.method_25294(left - 3, top - 13, right + 3, bottom + 3, 0xFFC9A227);
			gui.method_25294(left - 2, top - 12, right + 2, bottom + 2, 0xFF5C3A1E);
			gui.method_25294(left, top, right, bottom, 0xFF0A2030);

			int filled = (int) (barHeight * (mana / (float) StarLauncherItem.MAX_MANA));
			if (filled > 0) {
				int fillTop = bottom - filled;
				gui.method_25294(left, fillTop, right, bottom, 0xFF1EC8E0);
				gui.method_25294(left, fillTop, left + 3, bottom, 0xFF7CF0FF);
			}

			int barCx = (left + right) / 2;
			gui.method_25300(client.field_1772, "\u00a7b\u041c\u0430\u043d\u0430", barCx, top - 11, 0xFFFFFF);
			gui.method_25300(client.field_1772, "\u00a7b" + mana + "/" + StarLauncherItem.MAX_MANA, barCx, bottom + 6, 0xFFFFFF);
		}
	}
}
