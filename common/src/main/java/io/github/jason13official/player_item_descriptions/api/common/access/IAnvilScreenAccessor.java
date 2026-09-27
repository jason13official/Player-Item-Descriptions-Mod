package io.github.jason13official.player_item_descriptions.api.common.access;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface IAnvilScreenAccessor {

  void player_item_descriptions$init();

  void player_item_descriptions$removed();

  boolean player_item_descriptions$isPageVisible();

  void player_item_descriptions$extractPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a);

  boolean player_item_descriptions$isOverPanel(double mouseX, double mouseY);
}
