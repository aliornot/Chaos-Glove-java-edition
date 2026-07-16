package net.fabricmc.fabric.api.networking.v1;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.server.MinecraftServer;

public class ServerPlayNetworking {
  @FunctionalInterface
  public interface PlayChannelHandler {
    void receive(MinecraftServer server, class_3222 player, class_3244 handler, class_2540 buf, PacketSender responseSender);
  }
  public static void registerGlobalReceiver(class_2960 id, PlayChannelHandler h) {}
}
