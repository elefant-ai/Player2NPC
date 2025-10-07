//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.goodbird.player2npc.client.gui;

import com.player2.playerengine.player2api.Character;
import com.player2.playerengine.player2api.utils.CharacterUtils;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CharacterSelectionScreen extends Screen {
    private Character[] characters = null;
    private boolean isLoading = true;

    public CharacterSelectionScreen() {
        super(Component.nullToEmpty("Select a Character"));
    }

    protected void init() {
        super.init();
        this.clearWidgets();
        this.isLoading = true;
        CompletableFuture.supplyAsync(() -> CharacterUtils.requestCharacters(Minecraft.getInstance().player, "player2-ai-npc-minecraft")).thenAcceptAsync((result) -> {
            this.characters = result;
            this.isLoading = false;
            this.minecraft.execute(this::createCharacterCards);
        }, this.minecraft);
    }

    private void createCharacterCards() {
        if (this.characters != null && this.characters.length != 0) {
            int cardWidth = 100;
            int cardHeight = 130;
            int padding = 30;
            int cardsPerRow = Math.max(1, (this.width - padding) / (cardWidth + padding));
            int totalWidth = cardsPerRow * (cardWidth + padding) - padding;
            int startX = this.width / 2 - totalWidth / 2;
            int startY = 70;
            int currentX = startX;
            int currentY = startY;

            for(Character character : this.characters) {
                this.addRenderableWidget(new CharacterCardWidget(currentX, currentY, cardWidth, cardHeight, character, this::onCharacterClicked));
                currentX += cardWidth + padding;
                if (currentX + cardWidth > startX + totalWidth) {
                    currentX = startX;
                    currentY += cardHeight + padding;
                }
            }

        }
    }

    private void onCharacterClicked(Character character) {
        if (this.minecraft != null) {
            this.minecraft.setScreen(new CharacterDetailScreen(this, character));
        }

    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, "Select a Character", this.width / 2, 20, 16777215);
        if (this.isLoading) {
            graphics.drawCenteredString(this.font, "Loading...", this.width / 2, this.height / 2, 11184810);
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    public boolean isPauseScreen() {
        return false;
    }
}
