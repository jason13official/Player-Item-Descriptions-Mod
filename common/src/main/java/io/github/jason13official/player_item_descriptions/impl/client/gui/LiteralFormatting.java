package io.github.jason13official.player_item_descriptions.impl.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

public final class LiteralFormatting {

  private static int depth;

  public static boolean isActive() {
    return depth > 0;
  }

  public static void begin() {
    depth++;
  }

  public static void end() {
    depth = Math.max(0, depth - 1);
  }

  public static List<int[]> splitLines(Font font, String value, int width) {

    List<int[]> lines = new ArrayList<>();

    if (value.isEmpty()) {
      lines.add(new int[]{0, 0});
      return lines;
    }

    begin();

    try {
      font.getSplitter().splitLines(value, width, Style.EMPTY, false, (style, begin, end) -> lines.add(new int[]{begin, end}));
    } finally {
      end();
    }

    if (value.charAt(value.length() - 1) == '\n') {
      lines.add(new int[]{value.length(), value.length()});
    }

    return lines;
  }

  public static FormattedCharSequence sequence(String value, int begin, int end) {

    String text = value.substring(0, end);

    return sink -> iterate(text, 0, Style.EMPTY, Style.EMPTY,
        (position, style, codepoint) -> position < begin || sink.accept(position - begin, style, codepoint));
  }

  public static int width(Font font, String value, int begin, int end) {
    return font.width(sequence(value, begin, end));
  }

  public static boolean iterate(String text, int offset, Style currentStyle, Style resetStyle, FormattedCharSink sink) {

    Style style = currentStyle;
    boolean code = offset > 0 && offset <= text.length() && text.charAt(offset - 1) == '§';

    for (int position = offset; position < text.length(); position++) {

      char character = text.charAt(position);

      if (code) {
        code = false;

        if (!sink.accept(position, resetStyle, Character.isSurrogate(character) ? 65533 : character)) {
          return false;
        }

        continue;
      }

      if (character == '§') {

        if (position + 1 < text.length()) {

          ChatFormatting formatting = ChatFormatting.getByCode(text.charAt(position + 1));

          if (formatting != null) {
            style = formatting == ChatFormatting.RESET ? resetStyle : style.applyLegacyFormat(formatting);
          }
        }

        code = true;

        if (!sink.accept(position, resetStyle, character)) {
          return false;
        }

        continue;
      }

      if (Character.isHighSurrogate(character) && position + 1 < text.length() && Character.isLowSurrogate(text.charAt(position + 1))) {

        if (!sink.accept(position, style, Character.toCodePoint(character, text.charAt(position + 1)))) {
          return false;
        }

        position++;
      } else if (!sink.accept(position, style, Character.isSurrogate(character) ? 65533 : character)) {
        return false;
      }
    }

    return true;
  }
}
