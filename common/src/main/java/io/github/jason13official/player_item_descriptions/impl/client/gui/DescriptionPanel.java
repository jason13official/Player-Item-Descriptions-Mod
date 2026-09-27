package io.github.jason13official.player_item_descriptions.impl.client.gui;

import io.github.jason13official.player_item_descriptions.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/// The framed "Description" panel drawn behind the anvil's description text box:
/// a header, an inset text area (where the edit box sits) and a footer with the close hint and character count.
public final class DescriptionPanel {

  public static final int WIDTH = 122;
  public static final int LINE_LIMIT = 14;
  public static final int CHARACTER_LIMIT = 1024;

  /// lines shown at once; the edit box scrolls to the cursor for the rest
  private static final int VISIBLE_LINES = 4;
  private static final int LINE_HEIGHT = 9;
  private static final int PADDING = 4;
  /// MultiLineEditBox's own inner padding, on each side
  private static final int TEXT_PADDING = 4;

  private static final int HEADER_Y = PADDING + 1;
  private static final int AREA_Y = HEADER_Y + LINE_HEIGHT + 1;
  private static final int AREA_WIDTH = WIDTH - PADDING * 2;
  private static final int AREA_HEIGHT = VISIBLE_LINES * LINE_HEIGHT + TEXT_PADDING * 2 + 2;
  private static final int FOOTER_Y = AREA_Y + AREA_HEIGHT + 3;

  public static final int HEIGHT = FOOTER_Y + LINE_HEIGHT + PADDING;

  /// the edit box fills the inset text area, inside its 1px border
  public static final int TEXT_BOX_X = PADDING + 1;
  public static final int TEXT_BOX_Y = AREA_Y + 1;
  public static final int TEXT_BOX_WIDTH = AREA_WIDTH - 2;
  public static final int TEXT_BOX_HEIGHT = AREA_HEIGHT - 2;

  public static final int TEXT_COLOR = 0xFF404040;
  private static final int OUTLINE_COLOR = 0xFF373737;
  private static final int HIGHLIGHT_COLOR = 0xFFFFFFFF;
  private static final int SHADOW_COLOR = 0xFF555555;
  private static final int BACKGROUND_COLOR = 0xFFC6C6C6;

  public static final Component TITLE = Component.translatable("gui." + Constants.MOD_ID + ".description.title");
  private static final Component CLOSE_HINT = Component.translatable("gui." + Constants.MOD_ID + ".description.close_hint");

  public static void extract(GuiGraphicsExtractor graphics, Font font, int x, int y, int characterCount) {

    // raised frame, like vanilla container backgrounds
    graphics.fill(x, y, x + WIDTH, y + HEIGHT, OUTLINE_COLOR);
    graphics.fill(x + 1, y + 1, x + WIDTH - 1, y + HEIGHT - 1, SHADOW_COLOR);
    graphics.fill(x + 1, y + 1, x + WIDTH - 3, y + HEIGHT - 3, HIGHLIGHT_COLOR);
    graphics.fill(x + 3, y + 3, x + WIDTH - 3, y + HEIGHT - 3, BACKGROUND_COLOR);

    // inset text area: dark top/left edge, light bottom/right edge
    int areaX = x + PADDING;
    int areaY = y + AREA_Y;
    graphics.fill(areaX, areaY, areaX + AREA_WIDTH, areaY + AREA_HEIGHT, OUTLINE_COLOR);
    graphics.fill(areaX + 1, areaY + 1, areaX + AREA_WIDTH, areaY + AREA_HEIGHT, HIGHLIGHT_COLOR);
    graphics.fill(areaX + 1, areaY + 1, areaX + AREA_WIDTH - 1, areaY + AREA_HEIGHT - 1, BACKGROUND_COLOR);

    int textLeft = x + PADDING + 1;
    int textRight = x + WIDTH - PADDING - 1;

    graphics.text(font, TITLE, textLeft, y + HEADER_Y, TEXT_COLOR, false);
    graphics.text(font, CLOSE_HINT, textLeft, y + FOOTER_Y, TEXT_COLOR, false);

    Component count = Component.translatable("gui.multiLineEditBox.character_limit", characterCount, CHARACTER_LIMIT);
    graphics.text(font, count, textRight - font.width(count), y + FOOTER_Y, TEXT_COLOR, false);
  }

  public static boolean isMouseOver(int x, int y, double mouseX, double mouseY) {
    return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
  }
}
