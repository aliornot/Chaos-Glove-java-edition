package com.example.cubecrosshair.client;

import com.example.cubecrosshair.item.StarLauncherItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

@Environment(EnvType.CLIENT)
public class StarLauncherScreen extends class_437 {
	private static final String[] MODES = {
		StarLauncherItem.MODE_FIRE,
		StarLauncherItem.MODE_WATER,
		StarLauncherItem.MODE_EARTH,
		StarLauncherItem.MODE_STAR,
		StarLauncherItem.MODE_LIGHT,
		StarLauncherItem.MODE_DARK
	};
	private static final String[] LABELS = {
		"\u041e\u0433\u043e\u043d\u044c",
		"\u0412\u043e\u0434\u0430",
		"\u0417\u0435\u043c\u043b\u044f",
		"\u0417\u0432\u0435\u0437\u0434\u0430",
		"\u0421\u0432\u0435\u0442",
		"\u0422\u044c\u043c\u0430"
	};

	private int centerX;
	private int centerY;
	private int radius = 78;
	private int scrollW = 52;
	private int scrollH = 64;
	private int hovered = -1;

	public StarLauncherScreen() {
		super(class_2561.method_43470("\u0417\u0432\u0451\u0437\u0434\u043d\u044b\u0439 \u0432\u044b\u043f\u0443\u0441\u043a\u0430\u0442\u0435\u043b\u044c"));
	}

	@Override
	protected void method_25426() {
		super.method_25426();
		this.centerX = this.field_22789 / 2;
		this.centerY = this.field_22790 / 2 + 6;
	}

	@Override
	public void method_25394(class_332 g, int mouseX, int mouseY, float delta) {
		this.method_25420(g);

		this.centerX = this.field_22789 / 2;
		this.centerY = this.field_22790 / 2 + 6;

		drawFilledCircle(g, centerX, centerY, radius + 36, 0xFF3A2614);
		drawFilledCircle(g, centerX, centerY, radius + 30, 0xFF5C3A1E);
		drawFilledCircle(g, centerX, centerY, radius + 24, 0xFF7A5230);
		drawRing(g, centerX, centerY, radius + 20, 4, 0xFF2C180C);
		drawFilledCircle(g, centerX, centerY, 28, 0xFF4A3018);
		drawFilledCircle(g, centerX, centerY, 22, 0xFF8B5A2B);
		drawFilledCircle(g, centerX, centerY, 16, 0xFFC4A46C);

		drawRing(g, centerX, centerY, radius + 34, 2, 0xFFC9A227);
		drawRing(g, centerX, centerY, radius + 22, 1, 0xFFD4AF37);

		int titleW = 180;
		int titleH = 22;
		int tx = centerX - titleW / 2;
		int ty = centerY - radius - 58;
		drawWoodPanel(g, tx, ty, titleW, titleH);
		// method_25300 centers on x
		g.method_25300(this.field_22793, "\u00a76\u00a7l\u0417\u0412\u0401\u0417\u0414\u041d\u042b\u0419 \u0412\u042b\u041f\u0423\u0421\u041a\u0410\u0422\u0415\u041b\u042c", centerX, ty + 7, 0xFFFFFF);
		g.method_25300(this.field_22793, "\u00a77\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0441\u0442\u0438\u0445\u0438\u044e", centerX, ty + 26, 0xFFD0C0A0);

		hovered = hitTest(mouseX, mouseY);

		for (int i = 0; i < 6; i++) {
			double angle = Math.toRadians(-90 + i * 60);
			int sx = centerX + (int) (Math.cos(angle) * radius) - scrollW / 2;
			int sy = centerY + (int) (Math.sin(angle) * radius) - scrollH / 2;
			drawScroll(g, sx, sy, scrollW, scrollH, MODES[i], LABELS[i], i == hovered);
		}

		g.method_25300(this.field_22793, "\u00a7e\u041a\u043b\u0438\u043a \u043f\u043e \u0441\u0432\u0438\u0442\u043a\u0443", centerX, centerY - 4, 0xFFFFFF);
		g.method_25300(this.field_22793, "\u00a78ESC \u2014 \u0437\u0430\u043a\u0440\u044b\u0442\u044c", centerX, centerY + 8, 0xFFAAAAAA);
	}

	private void drawScroll(class_332 g, int x, int y, int w, int h, String mode, String label, boolean hover) {
		int body = StarLauncherItem.getScrollColor(mode);
		int textCol = StarLauncherItem.getScrollTextColor(mode);

		if (hover) {
			x -= 2;
			y -= 3;
			g.method_25294(x - 3, y - 3, x + w + 3, y + h + 3, 0x66FFD700);
		}

		int rollerH = 7;
		g.method_25294(x - 2, y, x + w + 2, y + rollerH, 0xFF5A3A1A);
		g.method_25294(x - 2, y, x + w + 2, y + 2, 0xFF8B5A2B);
		g.method_25294(x - 2, y + h - rollerH, x + w + 2, y + h, 0xFF5A3A1A);
		g.method_25294(x - 2, y + h - 2, x + w + 2, y + h, 0xFF3A2410);

		g.method_25294(x - 5, y + 1, x - 1, y + rollerH - 1, 0xFFC9A227);
		g.method_25294(x + w + 1, y + 1, x + w + 5, y + rollerH - 1, 0xFFC9A227);
		g.method_25294(x - 5, y + h - rollerH + 1, x - 1, y + h - 1, 0xFFC9A227);
		g.method_25294(x + w + 1, y + h - rollerH + 1, x + w + 5, y + h - 1, 0xFFC9A227);

		int by = y + rollerH - 1;
		int bh = h - rollerH * 2 + 2;
		g.method_25294(x, by, x + w, by + bh, body);

		int edgeDark = darken(body, 40);
		int edgeLight = lighten(body, 30);
		g.method_25294(x, by, x + 2, by + bh, edgeLight);
		g.method_25294(x + w - 2, by, x + w, by + bh, edgeDark);

		int gem = body;
		if (StarLauncherItem.MODE_FIRE.equals(mode)) gem = 0xFFFF5522;
		else if (StarLauncherItem.MODE_WATER.equals(mode)) gem = 0xFF44AAFF;
		else if (StarLauncherItem.MODE_EARTH.equals(mode)) gem = 0xFF88CC44;
		else if (StarLauncherItem.MODE_STAR.equals(mode)) gem = 0xFFE0A0FF;
		else if (StarLauncherItem.MODE_LIGHT.equals(mode)) gem = 0xFFFFFFAA;
		else if (StarLauncherItem.MODE_DARK.equals(mode)) gem = 0xFF666688;
		int gx = x + w / 2 - 5;
		int gy = by + 8;
		g.method_25294(gx, gy, gx + 10, gy + 10, gem);
		g.method_25294(gx + 1, gy + 1, gx + 5, gy + 5, lighten(gem, 50));

		// Center label on scroll with method_25300
		g.method_25300(this.field_22793, label, x + w / 2, by + bh / 2 + 4, textCol);

		if (hover) {
			g.method_25294(x, by, x + w, by + 1, 0xFFFFD700);
			g.method_25294(x, by + bh - 1, x + w, by + bh, 0xFFFFD700);
			g.method_25294(x, by, x + 1, by + bh, 0xFFFFD700);
			g.method_25294(x + w - 1, by, x + w, by + bh, 0xFFFFD700);
		}
	}

	private void drawWoodPanel(class_332 g, int x, int y, int w, int h) {
		g.method_25294(x, y, x + w, y + h, 0xFF5C3A1E);
		g.method_25294(x, y, x + w, y + 2, 0xFFC9A227);
		g.method_25294(x, y + h - 2, x + w, y + h, 0xFFC9A227);
		g.method_25294(x, y, x + 2, y + h, 0xFFC9A227);
		g.method_25294(x + w - 2, y, x + w, y + h, 0xFFC9A227);
		g.method_25294(x + 3, y + 3, x + w - 3, y + h - 3, 0xFF7A5230);
	}

	private void drawFilledCircle(class_332 g, int cx, int cy, int r, int color) {
		for (int dy = -r; dy <= r; dy++) {
			int dx = (int) Math.sqrt((double) r * r - (double) dy * dy);
			g.method_25294(cx - dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
		}
	}

	private void drawRing(class_332 g, int cx, int cy, int r, int thickness, int color) {
		for (int t = 0; t < thickness; t++) {
			int rr = r - t;
			for (int a = 0; a < 360; a += 2) {
				double rad = Math.toRadians(a);
				int x = cx + (int) (Math.cos(rad) * rr);
				int y = cy + (int) (Math.sin(rad) * rr);
				g.method_25294(x, y, x + 2, y + 2, color);
			}
		}
	}

	private int hitTest(int mx, int my) {
		for (int i = 0; i < 6; i++) {
			double angle = Math.toRadians(-90 + i * 60);
			int sx = centerX + (int) (Math.cos(angle) * radius) - scrollW / 2;
			int sy = centerY + (int) (Math.sin(angle) * radius) - scrollH / 2;
			if (mx >= sx - 4 && mx <= sx + scrollW + 4 && my >= sy - 2 && my <= sy + scrollH + 2) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean method_25402(double mouseX, double mouseY, int button) {
		if (button == 0) {
			int hit = hitTest((int) mouseX, (int) mouseY);
			if (hit >= 0) {
				StarLauncherClientNetwork.sendChangeModePacket(MODES[hit]);
				this.method_25419();
				return true;
			}
		}
		return super.method_25402(mouseX, mouseY, button);
	}

	@Override
	public boolean method_25421() {
		return false;
	}

	private static int darken(int argb, int amount) {
		int a = (argb >>> 24) & 0xFF;
		int r = Math.max(0, ((argb >>> 16) & 0xFF) - amount);
		int g = Math.max(0, ((argb >>> 8) & 0xFF) - amount);
		int b = Math.max(0, (argb & 0xFF) - amount);
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	private static int lighten(int argb, int amount) {
		int a = (argb >>> 24) & 0xFF;
		int r = Math.min(255, ((argb >>> 16) & 0xFF) + amount);
		int g = Math.min(255, ((argb >>> 8) & 0xFF) + amount);
		int b = Math.min(255, (argb & 0xFF) + amount);
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}
