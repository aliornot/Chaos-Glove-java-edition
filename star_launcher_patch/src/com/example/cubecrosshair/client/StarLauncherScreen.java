package com.example.cubecrosshair.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;

/**
 * Star Launcher element menu — same structure as ChaosGloveScreen
 * (ButtonWidget list + decorative panel), 6 elements instead of 4 modes.
 */
@Environment(EnvType.CLIENT)
public class StarLauncherScreen extends class_437 {

	public StarLauncherScreen() {
		// Same as ChaosGloveScreen: method_43470 returns MutableText (class_5250)
		super((class_2561) class_2561.method_43470("Star Launcher Elements"));
	}

	@Override
	protected void method_25426() {
		super.method_25426();
		int buttonWidth = 140;
		int buttonHeight = 18;
		int spacing = 6;
		int startX = (this.field_22789 - buttonWidth) / 2;
		// 6 buttons
		int startY = (this.field_22790 - (buttonHeight * 6 + spacing * 5)) / 2 + 10;

		this.method_37063((class_364) class_4185.method_46430(
			(class_2561) class_2561.method_43470("\u00a7c\u041e\u0433\u043e\u043d\u044c"),
			button -> {
				StarLauncherClientNetwork.sendChangeModePacket("fire");
				this.method_25419();
			}
		).method_46434(startX, startY, buttonWidth, buttonHeight).method_46431());

		this.method_37063((class_364) class_4185.method_46430(
			(class_2561) class_2561.method_43470("\u00a79\u0412\u043e\u0434\u0430"),
			button -> {
				StarLauncherClientNetwork.sendChangeModePacket("water");
				this.method_25419();
			}
		).method_46434(startX, startY + (buttonHeight + spacing), buttonWidth, buttonHeight).method_46431());

		this.method_37063((class_364) class_4185.method_46430(
			(class_2561) class_2561.method_43470("\u00a72\u0417\u0435\u043c\u043b\u044f"),
			button -> {
				StarLauncherClientNetwork.sendChangeModePacket("earth");
				this.method_25419();
			}
		).method_46434(startX, startY + (buttonHeight + spacing) * 2, buttonWidth, buttonHeight).method_46431());

		this.method_37063((class_364) class_4185.method_46430(
			(class_2561) class_2561.method_43470("\u00a7d\u0417\u0432\u0435\u0437\u0434\u0430"),
			button -> {
				StarLauncherClientNetwork.sendChangeModePacket("star");
				this.method_25419();
			}
		).method_46434(startX, startY + (buttonHeight + spacing) * 3, buttonWidth, buttonHeight).method_46431());

		this.method_37063((class_364) class_4185.method_46430(
			(class_2561) class_2561.method_43470("\u00a7f\u0421\u0432\u0435\u0442"),
			button -> {
				StarLauncherClientNetwork.sendChangeModePacket("light");
				this.method_25419();
			}
		).method_46434(startX, startY + (buttonHeight + spacing) * 4, buttonWidth, buttonHeight).method_46431());

		this.method_37063((class_364) class_4185.method_46430(
			(class_2561) class_2561.method_43470("\u00a78\u0422\u044c\u043c\u0430"),
			button -> {
				StarLauncherClientNetwork.sendChangeModePacket("dark");
				this.method_25419();
			}
		).method_46434(startX, startY + (buttonHeight + spacing) * 5, buttonWidth, buttonHeight).method_46431());
	}

	@Override
	public void method_25394(class_332 guiGraphics, int mouseX, int mouseY, float delta) {
		// Same decorative panel approach as ChaosGloveScreen
		this.method_25420(guiGraphics);
		int boxWidth = 180;
		int boxHeight = 190;
		int left = (this.field_22789 - boxWidth) / 2;
		int top = (this.field_22790 - boxHeight) / 2 + 5;
		int right = left + boxWidth;
		int bottom = top + boxHeight;

		// Dark wood panel
		guiGraphics.method_25294(left, top, right, bottom, 0xFF3A2614);
		guiGraphics.method_25294(left, bottom - 3, right, bottom, 0xFF2C180C);
		guiGraphics.method_25294(right - 3, top, right, bottom, 0xFF2C180C);
		guiGraphics.method_25294(left, top, right, top + 3, 0xFF5C3A1E);
		guiGraphics.method_25294(left, top, left + 3, bottom, 0xFF5C3A1E);

		// Gold border (like glove gold border)
		int bLeft = left + 4;
		int bTop = top + 4;
		int bRight = right - 4;
		int bBottom = bottom - 4;
		guiGraphics.method_25294(bLeft, bTop, bRight, bTop + 2, 0xFFC9A227);
		guiGraphics.method_25294(bLeft, bBottom - 2, bRight, bBottom, 0xFFC9A227);
		guiGraphics.method_25294(bLeft, bTop, bLeft + 2, bBottom, 0xFFC9A227);
		guiGraphics.method_25294(bRight - 2, bTop, bRight, bBottom, 0xFFC9A227);
		guiGraphics.method_25294(bLeft, bTop, bRight, bTop + 1, 0xFFFFD700);
		guiGraphics.method_25294(bLeft, bTop, bLeft + 1, bBottom, 0xFFFFD700);
		guiGraphics.method_25294(bLeft + 1, bBottom - 1, bRight - 1, bBottom, 0xFF8B6914);
		guiGraphics.method_25294(bRight - 1, bTop + 1, bRight, bBottom - 1, 0xFF8B6914);

		// Title — method_25300 centered like ChaosGloveScreen
		guiGraphics.method_25300(this.field_22793,
			"\u00a76\u00a7l\u0417\u0412\u0401\u0417\u0414\u041d\u042b\u0419 \u0412\u042b\u041f\u0423\u0421\u041a\u0410\u0422\u0415\u041b\u042c",
			this.field_22789 / 2, top - 14, 0xFFFFFF);

		super.method_25394(guiGraphics, mouseX, mouseY, delta);
	}

	@Override
	public boolean method_25421() {
		return false;
	}
}
