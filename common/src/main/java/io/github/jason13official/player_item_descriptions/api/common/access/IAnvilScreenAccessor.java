package io.github.jason13official.player_item_descriptions.api.common.access;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface IAnvilScreenAccessor {

  void player_item_descriptions$init();

  void player_item_descriptions$removed();

  boolean player_item_descriptions$isPageVisible();

  void player_item_descriptions$extractPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a);

  /// true when the open page's panel should swallow a click at this position (so slots underneath aren't clicked)
  boolean player_item_descriptions$blocksMouse(double mouseX, double mouseY);
}
