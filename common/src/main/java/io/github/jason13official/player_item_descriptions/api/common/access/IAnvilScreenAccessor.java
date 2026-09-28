package io.github.jason13official.player_item_descriptions.api.common.access;

public interface IAnvilScreenAccessor {

  void player_item_descriptions$init();

  void player_item_descriptions$removed();

  boolean player_item_descriptions$isPageVisible();

  boolean player_item_descriptions$isOverPanel(double mouseX, double mouseY);

  boolean player_item_descriptions$mouseClicked(double mouseX, double mouseY, int button);

  void player_item_descriptions$restoreFocus();
}
