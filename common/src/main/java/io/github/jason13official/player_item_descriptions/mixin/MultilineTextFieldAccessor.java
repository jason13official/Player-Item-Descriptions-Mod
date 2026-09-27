package io.github.jason13official.player_item_descriptions.mixin;

import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultilineTextField.class)
public interface MultilineTextFieldAccessor {

  @Accessor("selectCursor")
  int player_item_descriptions$getSelectCursor();
}
