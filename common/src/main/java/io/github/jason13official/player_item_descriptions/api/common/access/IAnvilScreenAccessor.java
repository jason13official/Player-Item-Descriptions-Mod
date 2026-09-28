package io.github.jason13official.player_item_descriptions.api.common.access;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public interface IAnvilScreenAccessor {

  void player_item_descriptions$init();

  void player_item_descriptions$removed();

  boolean player_item_descriptions$isPageVisible();

  void player_item_descriptions$extractPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a);

  boolean player_item_descriptions$isOverPanel(double mouseX, double mouseY);

  boolean player_item_descriptions$mouseClicked(MouseButtonEvent event, boolean doubleClick);
}
