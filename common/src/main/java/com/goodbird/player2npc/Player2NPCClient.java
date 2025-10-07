//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.goodbird.player2npc;

import com.goodbird.player2npc.client.gui.CharacterSelectionScreen;
import com.goodbird.player2npc.client.render.RenderAutomaton;
import com.goodbird.player2npc.network.AutomatonSpawnPacket;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;

public class Player2NPCClient {
    private static KeyMapping openCharacterScreenKeybind;
    private static long lastHeartbeatTime = System.nanoTime();

    public Player2NPCClient() {
    }

    public static void onInitializeClient() {
        EntityRendererRegistry.register(Player2NPC.AUTOMATONE, RenderAutomaton::new);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, Player2NPC.SPAWN_PACKET_ID, AutomatonSpawnPacket::handle);
        openCharacterScreenKeybind = new KeyMapping("key.player2npc.open_character_screen", Type.KEYSYM, 72, "category.player2npc.keys");
        KeyMappingRegistry.register(openCharacterScreenKeybind);
        ClientTickEvent.CLIENT_POST.register((client) -> {
            if (openCharacterScreenKeybind.consumeClick() && client.level != null) {
                client.setScreen(new CharacterSelectionScreen());
            }
        });
    }
}
