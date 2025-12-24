//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.goodbird.player2npc.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;

public class SkinManager {
    private static final ResourceLocation STEVE_SKIN_ID = ResourceLocation.parse("textures/entity/player/wide/steve.png");

    public SkinManager() {
    }

    public static ResourceLocation getSkinIdentifier(String skinUrl) {
        if (skinUrl != null && !skinUrl.isEmpty()) {
            ResourceLocation location = ResourceDownloader.getUrlResourceLocation(skinUrl, true);
            if (Minecraft.getInstance().getTextureManager().getTexture(location) != null) {
                return location;
            } else {
                File cacheFile = ResourceDownloader.getUrlFile(skinUrl, true);
                ImageDownloadAlt downloader = new ImageDownloadAlt(cacheFile, skinUrl, location, STEVE_SKIN_ID, true, () -> {
                });
                ResourceDownloader.load(downloader);
                return STEVE_SKIN_ID;
            }
        } else {
            return STEVE_SKIN_ID;
        }
    }

    public static void renderSkinHead(GuiGraphics graphics, int x, int y, int size, ResourceLocation skinIdentifier) {
        //RenderSystem.setShaderTexture(0, skinIdentifier);
//        RenderSystem.disableBlend();
//        RenderSystem.enableDepthTest();
        int faceU = 8;
        int faceV = 8;
        int faceWidth = 8;
        int faceHeight = 8;
        int textureWidth = 64;
        int textureHeight = 64;
        graphics.blit(RenderPipelines.GUI_TEXTURED, skinIdentifier, x, y, (float)faceU, (float)faceV, size, size, faceWidth, faceHeight, textureWidth, textureHeight);
        int hatU = 40;
        int hatV = 8;
        int hatWidth = 8;
        int hatHeight = 8;
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
        graphics.blit(RenderPipelines.GUI_TEXTURED, skinIdentifier, x, y, (float)hatU, (float)hatV, size, size, hatWidth, hatHeight, textureWidth, textureHeight);
//        RenderSystem.disableBlend();
    }
}
