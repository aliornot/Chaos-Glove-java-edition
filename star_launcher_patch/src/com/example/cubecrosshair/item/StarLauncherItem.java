package com.example.cubecrosshair.item;

import com.example.cubecrosshair.CubeCrosshair;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class StarLauncherItem extends class_1792 {
	public static final String NBT_MODE = "StarMode";
	public static final String NBT_MANA = "StarMana";
	public static final int MAX_MANA = 10;

	public static final String MODE_FIRE = "fire";
	public static final String MODE_WATER = "water";
	public static final String MODE_EARTH = "earth";
	public static final String MODE_STAR = "star";
	public static final String MODE_LIGHT = "light";
	public static final String MODE_DARK = "dark";
	public static final String MODE_NONE = "none";

	/** Client-side fallback so HUD updates immediately after menu click. */
	public static String clientSelectedMode = MODE_NONE;

	public StarLauncherItem(class_1792.class_1793 settings) {
		super(settings);
	}

	public static boolean isStarLauncher(class_1799 stack) {
		if (stack == null) return false;
		try {
			if (stack.method_7960()) return false;
		} catch (Throwable t) {
			return false;
		}
		try {
			if (stack.method_7909() instanceof StarLauncherItem) return true;
		} catch (Throwable ignored) {}
		try {
			class_1792 item = stack.method_7909();
			if (CubeCrosshair.STAR_LAUNCHER != null && item == CubeCrosshair.STAR_LAUNCHER) return true;
		} catch (Throwable ignored) {}
		try {
			if (CubeCrosshair.STAR_LAUNCHER != null && stack.method_31574(CubeCrosshair.STAR_LAUNCHER)) return true;
		} catch (Throwable ignored) {}
		try {
			class_1792 item = stack.method_7909();
			if (item != null && class_7923.field_41178 != null) {
				class_2960 id = class_7923.field_41178.method_10221(item);
				if (id != null
					&& "chaos_glove".equals(id.method_12836())
					&& "star_launcher".equals(id.method_12832())) {
					return true;
				}
			}
		} catch (Throwable ignored) {}
		try {
			class_1792 item = stack.method_7909();
			if (item != null) {
				String key = item.method_7876();
				if (key != null && key.contains("star_launcher")) return true;
			}
		} catch (Throwable ignored) {}
		return false;
	}

	public static class_2487 nbt(class_1799 stack) {
		return stack.method_7948();
	}

	public static String getMode(class_1799 stack) {
		// Prefer NBT on the stack
		try {
			if (stack != null) {
				// Always try getOrCreate path: read via getNbt if present, else getOrCreate
				class_2487 tag = null;
				if (stack.method_7985()) {
					tag = stack.method_7969();
				}
				if (tag != null) {
					String mode = tag.method_10558(NBT_MODE);
					if (mode != null && !mode.isEmpty() && !MODE_NONE.equals(mode)) {
						clientSelectedMode = mode;
						return mode;
					}
				}
			}
		} catch (Throwable ignored) {}
		// Fallback to client cache (set when user clicks a scroll)
		if (clientSelectedMode != null && !clientSelectedMode.isEmpty()) {
			return clientSelectedMode;
		}
		return MODE_NONE;
	}

	public static void setMode(class_1799 stack, String mode) {
		if (mode == null) mode = MODE_NONE;
		clientSelectedMode = mode;
		if (stack == null) return;
		try {
			// getOrCreateNbt + putString — same as Chaos Glove ChaosMode
			nbt(stack).method_10582(NBT_MODE, mode);
		} catch (Throwable ignored) {}
	}

	public static int getMana(class_1799 stack) {
		if (stack == null) return MAX_MANA;
		try {
			if (!stack.method_7985()) return MAX_MANA;
			class_2487 tag = stack.method_7969();
			if (tag == null || !tag.method_10577("StarManaInit")) return MAX_MANA;
			return Math.max(0, Math.min(MAX_MANA, tag.method_10550(NBT_MANA)));
		} catch (Throwable t) {
			return MAX_MANA;
		}
	}

	public static void ensureManaInit(class_1799 stack) {
		if (stack == null) return;
		try {
			class_2487 tag = nbt(stack);
			if (!tag.method_10577("StarManaInit")) {
				tag.method_10569(NBT_MANA, MAX_MANA);
				tag.method_10556("StarManaInit", true);
			}
		} catch (Throwable ignored) {}
	}

	public static void setMana(class_1799 stack, int mana) {
		if (stack == null) return;
		try {
			class_2487 tag = nbt(stack);
			tag.method_10569(NBT_MANA, Math.max(0, Math.min(MAX_MANA, mana)));
			tag.method_10556("StarManaInit", true);
		} catch (Throwable ignored) {}
	}

	public static String getModeDisplayName(String mode) {
		if (mode == null) mode = MODE_NONE;
		switch (mode) {
			case MODE_FIRE:  return "\u00a7c\u00a7l\u0421\u0422\u0418\u0425\u0418\u042f: \u041e\u0413\u041e\u041d\u042c";
			case MODE_WATER: return "\u00a79\u00a7l\u0421\u0422\u0418\u0425\u0418\u042f: \u0412\u041e\u0414\u0410";
			case MODE_EARTH: return "\u00a72\u00a7l\u0421\u0422\u0418\u0425\u0418\u042f: \u0417\u0415\u041c\u041b\u042f";
			case MODE_STAR:  return "\u00a7d\u00a7l\u0421\u0422\u0418\u0425\u0418\u042f: \u0417\u0412\u0415\u0417\u0414\u0410";
			case MODE_LIGHT: return "\u00a7f\u00a7l\u0421\u0422\u0418\u0425\u0418\u042f: \u0421\u0412\u0415\u0422";
			case MODE_DARK:  return "\u00a78\u00a7l\u0421\u0422\u0418\u0425\u0418\u042f: \u0422\u042c\u041c\u0410";
			default:         return "\u00a77\u00a7l\u0421\u0422\u0418\u0425\u0418\u042f \u041d\u0415 \u0412\u042b\u0411\u0420\u0410\u041d\u0410 (\u041d\u0430\u0436\u043c\u0438\u0442\u0435 J)";
		}
	}

	public static String getModeShortName(String mode) {
		if (mode == null) mode = MODE_NONE;
		switch (mode) {
			case MODE_FIRE:  return "\u00a7c\u041e\u0433\u043e\u043d\u044c";
			case MODE_WATER: return "\u00a79\u0412\u043e\u0434\u0430";
			case MODE_EARTH: return "\u00a72\u0417\u0435\u043c\u043b\u044f";
			case MODE_STAR:  return "\u00a7d\u0417\u0432\u0435\u0437\u0434\u0430";
			case MODE_LIGHT: return "\u00a7f\u0421\u0432\u0435\u0442";
			case MODE_DARK:  return "\u00a78\u0422\u044c\u043c\u0430";
			default:         return "\u00a77\u041d\u0435 \u0432\u044b\u0431\u0440\u0430\u043d\u0430";
		}
	}

	public static int getScrollColor(String mode) {
		if (mode == null) return 0xFFC4A574;
		switch (mode) {
			case MODE_FIRE:  return 0xFFB33A2B;
			case MODE_WATER: return 0xFF2F6FA8;
			case MODE_EARTH: return 0xFF5A7A3A;
			case MODE_STAR:  return 0xFF8B5CBF;
			case MODE_LIGHT: return 0xFFF2E6C8;
			case MODE_DARK:  return 0xFF2A2A32;
			default:         return 0xFFC4A574;
		}
	}

	public static int getScrollTextColor(String mode) {
		if (MODE_LIGHT.equals(mode) || MODE_STAR.equals(mode)) return 0xFF2A1A0A;
		return 0xFFF5E6C8;
	}
}
