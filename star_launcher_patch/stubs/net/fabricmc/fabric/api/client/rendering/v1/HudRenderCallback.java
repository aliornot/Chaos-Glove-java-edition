package net.fabricmc.fabric.api.client.rendering.v1;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.class_332;
public interface HudRenderCallback {
  void onHudRender(class_332 drawContext, float tickDelta);
  Event<HudRenderCallback> EVENT = new Event<HudRenderCallback>();
}
