package io.github.jason13official.player_item_descriptions.api.common.access;

import org.jetbrains.annotations.Nullable;

public interface IAnvilMenuAccessor {

  String player_item_descriptions$getItemName();

  void player_item_descriptions$setItemName(String name);

  String player_item_descriptions$getItemDescription();

  boolean player_item_descriptions$setItemDescription(String description);

  @Nullable Boolean player_item_descriptions$getItemLock();

  boolean player_item_descriptions$setItemLock(boolean locked);
}
