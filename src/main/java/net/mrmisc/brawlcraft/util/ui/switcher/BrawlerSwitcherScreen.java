package net.mrmisc.brawlcraft.util.ui.switcher;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerDataPacket;
import net.mrmisc.brawlcraft.networking.ModPacketHandler;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

import static net.mrmisc.brawlcraft.util.helpers.Constants.*;

@OnlyIn(Dist.CLIENT)
public class BrawlerSwitcherScreen extends Screen {
    static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("gamemode_switcher/slot");
    static final ResourceLocation SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("gamemode_switcher/selection");
    private static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/gamemode_switcher.png");
    private static final int ALL_SLOTS_WIDTH = BrawlerIcon.values().length * 31 - 5;
    private final BrawlerIcon previousHovered;
    private BrawlerIcon currentlyHovered;
    private int firstMouseX;
    private int firstMouseY;
    private boolean setFirstMousePos;
    private final List<BrawlerSlot> slots = Lists.newArrayList();

    public BrawlerSwitcherScreen() {
        super(GameNarrator.NO_TITLE);
        this.previousHovered = BrawlerIcon.NORMAL;
        this.currentlyHovered = this.previousHovered;
    }

    @Override
    protected void init() {
        super.init();
        this.currentlyHovered = this.previousHovered;
        for (int i = 0; i < BrawlerIcon.VALUES.length; i++) {
            BrawlerIcon icon = BrawlerIcon.VALUES[i];
            this.slots.add(
                    new BrawlerSlot(icon, this.width / 2 - ALL_SLOTS_WIDTH / 2 + i * 31, this.height / 2 - 31)
            );
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.checkToClose()) {
            guiGraphics.pose().pushPose();
            RenderSystem.enableBlend();
            int i = this.width / 2 - 62;
            int j = this.height / 2 - 31 - 27;
            guiGraphics.blit(GAMEMODE_SWITCHER_LOCATION, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
            guiGraphics.pose().popPose();

            super.render(guiGraphics, mouseX, mouseY, partialTick);

            guiGraphics.drawCenteredString(this.font, this.currentlyHovered.getName(), this.width / 2, this.height / 2 - 31 - 20, -1);
            guiGraphics.drawCenteredString(this.font, Component.translatable("debug.brawlers.select_next", Component.translatable("debug.gamemodes.press_O").withStyle(ChatFormatting.AQUA)), this.width / 2, this.height / 2 + 5, 16777215);

            if (!this.setFirstMousePos) {
                this.firstMouseX = mouseX;
                this.firstMouseY = mouseY;
                this.setFirstMousePos = true;
            }

            boolean flag = this.firstMouseX == mouseX && this.firstMouseY == mouseY;

            for (BrawlerSlot slot : this.slots) {
                slot.render(guiGraphics, mouseX, mouseY, partialTick);
                slot.setSelected(this.currentlyHovered == slot.icon);
                if (!flag && slot.isHoveredOrFocused()) {
                    this.currentlyHovered = slot.icon;
                }
            }
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    private boolean checkToClose() {
        assert this.minecraft != null;
        if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
            String brawlerNameString = this.currentlyHovered.getName().getString();
            int brawlerIndex = getBrawlerIndex(brawlerNameString);
            LocalPlayer player = this.minecraft.player;
            checkIfBrawlerUnlocked(player, brawlerIndex);
            this.minecraft.setScreen(null);
            return true;
        } else {
            return false;
        }
    }

    public int getBrawlerIndex(String string){
        return Objects.requireNonNull(Brawlers.fromName(string)).getId();
    }

    public String getBrawlerName(int index){
        return Brawlers.fromID(index).getName();
    }

    public void checkIfBrawlerUnlocked(Player player, int index) {
        String brawlerName = getBrawlerName(index);
        if (ClientBrawlerData.UNLOCKED_BRAWLERS.contains(brawlerName) || brawlerName.equals(NORMAL)) {
            ModPacketHandler.sendToServer(new BrawlerDataPacket(index));
        } else {
            player.displayClientMessage(Component.literal(brawlerName + " is not unlocked yet !"), true);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 79) { // F4 key in vanilla logic; replace if you bind another key
            this.setFirstMousePos = false;
            this.currentlyHovered = this.currentlyHovered.getNext();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public enum BrawlerIcon {
        NORMAL(Component.literal("Normal"), new ItemStack(Items.IRON_SWORD)),
        DYNAMIKE(Component.literal("Dynamike"), new ItemStack(Items.TNT)),
        MAX(Component.literal("Max"), new ItemStack(Items.WIND_CHARGE)),
        BEA(Component.literal("Bea"), new ItemStack(Items.HONEYCOMB)),
        JACKY(Component.literal("Jacky"), new ItemStack(Items.IRON_PICKAXE)),
        CROW(Component.literal("Crow"), new ItemStack(Items.POISONOUS_POTATO)),
        DOUG(Component.literal("Doug"), new ItemStack(Items.BREAD)),
        BO(Component.literal("Bo"), new ItemStack(Items.ARROW));

        static final BrawlerIcon[] VALUES = values();
        final Component name;
        final ItemStack renderStack;

        BrawlerIcon(Component name, ItemStack renderStack) {
            this.name = name;
            this.renderStack = renderStack;
        }

        void drawIcon(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.renderItem(this.renderStack, x, y);
        }

        Component getName() {
            return this.name;
        }

        BrawlerIcon getNext() {
            return switch (this) {
                case NORMAL -> DYNAMIKE;
                case DYNAMIKE -> MAX;
                case MAX -> BEA;
                case BEA -> JACKY;
                case JACKY -> CROW;
                case CROW -> DOUG;
                case DOUG -> BO;
                case BO -> NORMAL;
            };
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class BrawlerSlot extends AbstractWidget {
        final BrawlerIcon icon;
        private boolean isSelected;

        public BrawlerSlot(BrawlerIcon icon, int x, int y) {
            super(x, y, 26, 26, icon.getName());
            this.icon = icon;
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.drawSlot(guiGraphics);
            this.icon.drawIcon(guiGraphics, this.getX() + 5, this.getY() + 5);
            if (this.isSelected) {
                this.drawSelection(guiGraphics);
            }
        }

        @Override
        public void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }

        @Override
        public boolean isHoveredOrFocused() {
            return super.isHoveredOrFocused() || this.isSelected;
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
        }

        private void drawSlot(GuiGraphics guiGraphics) {
            guiGraphics.blitSprite(SLOT_SPRITE, this.getX(), this.getY(), 26, 26);
        }

        private void drawSelection(GuiGraphics guiGraphics) {
            guiGraphics.blitSprite(SELECTION_SPRITE, this.getX(), this.getY(), 26, 26);
        }
    }
}
