package de.hysky.skyblocker.skyblock.tabhud.config;

import de.hysky.skyblocker.skyblock.tabhud.config.entries.WidgetsListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class WidgetsElementList extends ElementListWidget<WidgetsListEntry> {
    static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_up_highlighted");
    static final Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla("transferable_list/move_up");
    static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_down_highlighted");
    static final Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla("transferable_list/move_down");

    private final WidgetsOrderingTab parent;
    private boolean rightUpArrowHovered = false;
    private boolean rightDownArrowHovered = false;
    private boolean leftUpArrowHovered = false;
    private boolean leftDownArrowHovered = false;

    private int editingPosition = - 1;

    public WidgetsElementList(WidgetsOrderingTab parent, MinecraftClient minecraftClient, int width, int height, int y) {
        super(minecraftClient, width, height, y, 32);
        this.parent = parent;
    }


    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    @Override
    public int addEntry(WidgetsListEntry entry) {
        return super.addEntry(entry);
    }

    private int x, y, entryWidth, entryHeight;

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        WidgetsListEntry hoveredEntry = getHoveredEntry();
        if (hoveredEntry != null) hoveredEntry.renderTooltip(context, x, y, entryWidth, entryHeight, mouseX, mouseY);
        if (rightUpArrowHovered || rightDownArrowHovered) {
            context.drawTooltip(client.textRenderer, Text.literal("Move widget"), mouseX, mouseY);
        }
        if (leftUpArrowHovered || leftDownArrowHovered) {
            context.drawTooltip(client.textRenderer, Text.literal("Change selection"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        super.renderEntry(context, mouseX, mouseY, delta, index, x, y, entryWidth, entryHeight);
        if (index == editingPosition) {
            boolean rightXGood = mouseX >= x + entryWidth && mouseX < x + entryWidth + 15;
            boolean leftXGood = mouseX >= x - 16 && mouseX < x - 1;
            boolean isOnUp = mouseY >= y && mouseY < y + entryHeight / 2;
            boolean isOnDown = mouseY >= y + entryHeight / 2 && mouseY < y + entryHeight;
            rightUpArrowHovered = rightXGood && isOnUp;
            rightDownArrowHovered = rightXGood && isOnDown;
            leftUpArrowHovered = leftXGood && isOnUp;
            leftDownArrowHovered = leftXGood && isOnDown;
            context.drawGuiTexture(rightUpArrowHovered ? MOVE_UP_HIGHLIGHTED_TEXTURE : MOVE_UP_TEXTURE, getRowRight() - 16, y, 32, 32);
            context.drawGuiTexture(rightDownArrowHovered ? MOVE_DOWN_HIGHLIGHTED_TEXTURE : MOVE_DOWN_TEXTURE, getRowRight() - 16, y, 32, 32);

            context.drawGuiTexture(leftUpArrowHovered ? MOVE_UP_HIGHLIGHTED_TEXTURE : MOVE_UP_TEXTURE, x - 33, y, 32, 32);
            context.drawGuiTexture(leftDownArrowHovered ? MOVE_DOWN_HIGHLIGHTED_TEXTURE : MOVE_DOWN_TEXTURE, x - 33, y, 32, 32);
        }
        if (Objects.equals(getHoveredEntry(), getEntry(index))) {
            this.x = x;
            this.y = y;
            this.entryWidth = entryWidth;
            this.entryHeight = entryHeight;
        }
    }

    @Override
    protected int getScrollbarX() {
        return super.getScrollbarX() + (editingPosition != -1 ? 15 : 0);
    }

    @Override
    public int getRowWidth() {
        return 280;
    }

    public void setEditingPosition(int editingPosition) {
        this.editingPosition = editingPosition;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (editingPosition == -1) return super.mouseClicked(mouseX, mouseY, button);
        if (rightUpArrowHovered) {
            parent.shiftClickAndWaitForServer(13, 1);
            return true;
        }
        if (rightDownArrowHovered) {
            parent.shiftClickAndWaitForServer(13, 0);
            return true;
        }
        if (leftUpArrowHovered) {
            parent.clickAndWaitForServer(13, 1);
        }
        if (leftDownArrowHovered) {
            parent.clickAndWaitForServer(13, 0);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
