package io.github.jsnimda.inventoryprofiles.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IndentParser
 */
public class IndentParser {
  private static final Pattern INDENT_REGEX = Pattern.compile("^[ \\\t]*");
  public boolean parsed = false;
  public Section root = null;
  public List<ErrorMessage> errorMsgs = new ArrayList<>();
  public final List<String> lines;
  public final int maxLevel;
  public IndentParser(List<String> lines, int maxLevel) {
    this.lines = lines;
    this.maxLevel = maxLevel;
  }
  public IndentParser parse() {
    if (parsed) return this;
    root = new Section(-1, "", -1, -1, null);

    int lineNumber = 0;
    Section last = null;
    outer:
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      lineNumber = i + 1;
      if (line.trim().isEmpty()) continue;
      int indent = getIndent(line);
      if (last == null && indent != 0) {
        error(lineNumber, "Unexpected indent. Indent should be zero.");
        continue;
      }
      if (indent == 0) {
        last = new Section(lineNumber, line, 0, 0, root);
      } else {
        if (indent < last.indent) {
          while (indent < last.indent) {
            last = last.parent;
          }
          if (indent > last.indent) {
            error(lineNumber, "Unexpected indent. Indent not aligned.");
            // stop loading children of last
            for (i = i+1; i < lines.size(); i++) {
              line = lines.get(i);
              lineNumber = i + 1;
              if (line.trim().isEmpty()) continue;
              if (getIndent(line) > last.indent) {
                error(lineNumber, "Skipped. Due to last un-aligned line.");
              } else {
                --i; // treat as peek
                continue outer;
              }
            }
            continue;
          }
          // now indent == last.indent
        }
        if ((maxLevel >= 0 && last.level >= maxLevel) || indent == last.indent) {
          last = new Section(lineNumber, line, last.level, last.indent, last.parent);
        } else { // indent > last.indent
          last = new Section(lineNumber, line, last.level + 1, indent, last);
        }
      }
    }

    parsed = true;
    return this;
  }
  private void error(int lineNumber, String message) {
    errorMsgs.add(new ErrorMessage(lineNumber, message));
  }
  public static class Section {
    public int lineNumber;
    public String text;
    public int level;
    public int indent;
    public List<Section> children = new ArrayList<>();
    public Section parent;
    public Section(int lineNumber, String text, int level, int indent, Section parent) {
      this.lineNumber = lineNumber;
      this.text = text;
      this.level = level;
      this.indent = indent;
      this.parent = parent;
      if (parent != null) {
        parent.children.add(this);
      }
    }
    @Override
    public String toString() {
      return "L" + level + ":" + lineNumber + ": " + text.trim();
    }
  }

  public static class ErrorMessage {
    public int lineNumber;
    public String message;

    public ErrorMessage(int lineNumber, String message) {
      this.lineNumber = lineNumber;
      this.message = message;
    }
    @Override
    public String toString() {
      return lineNumber + ": " + message;
    }
    
  }
  
  public static int getIndent(String line) {
    Matcher m = INDENT_REGEX.matcher(line);
    return m.find() ? m.group(0).length() : 0;
  }
  public static IndentParser parse(List<String> lines, int maxLevel) {
    return new IndentParser(lines, maxLevel).parse();
  }
}