package io.github.jsnimda.inventoryprofiles.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import io.github.jsnimda.inventoryprofiles.Log;
import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.IndentParser.ErrorMessage;
import io.github.jsnimda.inventoryprofiles.config.IndentParser.Section;
import io.github.jsnimda.inventoryprofiles.config.ProfileSet.Profile;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.util.Identifier;

/**
 * ProfilesConfigHandler
 */
public class ProfilesConfigHandler {

  public static final String FILE_VERSION_STRING = "PROFILES/0.1.0";
  public static final String CONFIG_FILE_NAME = "Profiles.custom.txt";
  public static final String CONFIG_DEFAULT_FILE_NAME = "Profiles.default.0.1.0.txt";
  public static final String CLASSPATH_FILE_NAME = "configs/profiles.default.txt";
  private static Identifier identifier;
  private static String defaultContent;
  private static Map<String, ProfileSet> defaulProfileSets = null;

  private static File dir;

  public static void init() {
    identifier = new Identifier(ModInfo.MOD_ID, CLASSPATH_FILE_NAME);
    InputStream inputStream;
    try {
      inputStream = Current.resourceManager().getResource(identifier).getInputStream();
      defaultContent = IOUtils.toString(inputStream, "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Map<String, ProfileSet> getDefauProfileSets() {
    if (defaulProfileSets == null) defaulProfileSets = Parser.parse(defaultContent);
    return defaulProfileSets;
  }

  public static class Parser {
    private static final Pattern PROFILE_LINE_REGEX = Pattern.compile("^([\\w.-]+)(?:\\s*\\(\\s*(.*?)\\s*\\)\\s*|\\s.*)$");
    private static final Pattern VALID_PROFILE_OPTIONS_REGEX = Pattern.compile("^(?:\\s*?[\\w.-]+\\s*=\\s*[\\w.-]+\\s*?|\\s*?)(?:,(?:\\s*?[\\w.-]+\\s*=\\s*[\\w.-]+\\s*?|\\s*?))*$"); // matches ex. "option1 = value1, option2 = value2"
    private static final Pattern OPTION_KEY_VALUE_PAIR_REGEX = Pattern.compile("([\\w.-]+)\\s*=\\s*([\\w.-]+)");
    private static final Pattern PREF_REGEX = Pattern.compile("^\\S+");

    private HashMap<String, ProfileSet> profileSets = new HashMap<>();
    private final List<String> lines;
    private static void err(String msg) {
      Log.warn("[inventoryprofiles]["+CONFIG_FILE_NAME+"] " + msg);
    }
    public Map<String, ProfileSet> parse() {
      if (lines.isEmpty() || lines.get(0).trim().isEmpty()) {
        err("File not loaded. Empty file.");
        return Collections.emptyMap();
      }
      if (!lines.get(0).trim().equals(FILE_VERSION_STRING)) {
        err("File not loaded. Version mismatch (first line not equal to \"" + FILE_VERSION_STRING + "\").");
        return Collections.emptyMap();
      }
      List<String> profileSetsContent = lines.subList(1, lines.size());
      IndentParser indentResult = IndentParser.parse(profileSetsContent, 2);
      for (ErrorMessage msg : indentResult.errorMsgs) {
        err("Line " + (msg.lineNumber+1) + " was skipped. " + msg.message);
      }
      // start parsing IndentParser sections
      // profile sets
      for (Section section : indentResult.root.children) {
        parseSectionAsLevel0(section);
      }
      return profileSets;
    }

    private void parseOptions(String profileName, String optionsString) {
      ProfileSet ps = createOrGetProfileSet(profileName);
      Matcher m = OPTION_KEY_VALUE_PAIR_REGEX.matcher(optionsString);
      while (m.find()) {
        ps.addOption(m.group(1), m.group(2));
      }
    }
    private void addProfile(String profileName, List<Section> children) {
      ProfileSet ps = createOrGetProfileSet(profileName);
      Profile p = ps.addProfile();
      for (Section pref : children) {
        String s = pref.text.trim();
        for (Section subpref : pref.children) {
          s += " " + subpref.text.trim();
        }
        Matcher m = PREF_REGEX.matcher(s);
        if (m.find()) {
          String slotName = m.group(0);
          if (ProfileSet.isValidSlotName(slotName)) {
            p.addPreference(slotName, s.substring(slotName.length()).trim());
          } else {
            err("Line " + (pref.lineNumber+1) + " was skipped. Invalid slot name.");
          }
        } else {
          err("Line " + (pref.lineNumber+1) + " was skipped. Invalid syntax.");
        }
      }
    }
    private void parseSectionAsLevel0(Section section) {
      Matcher profileLineValidator = PROFILE_LINE_REGEX.matcher(section.text);
      if (profileLineValidator.matches()) {
        String profileName = profileLineValidator.group(1);
        String profileOptions = profileLineValidator.group(2);
        if (profileOptions != null) {
          Matcher optionsValidator = VALID_PROFILE_OPTIONS_REGEX.matcher(profileOptions);
          if (optionsValidator.matches()) {
            parseOptions(profileName, optionsValidator.group(0));
          } else {
            err("Profile options at line " + (section.lineNumber+1) + "was skipped. Invalid syntax.");
          }
        } else {
          addProfile(profileName, section.children);
        }
      } else {
        err("Line " + (section.lineNumber+1) + " was skipped. Invalid profile name.");
      }
    }
    

    private ProfileSet createOrGetProfileSet(String name) {
      if (!profileSets.containsKey(name)) {
        profileSets.put(name, new ProfileSet(name));
      }
      return profileSets.get(name);
    }
    public Parser(List<String> lines) {
      this.lines = lines;
    }
    public static Map<String, ProfileSet> parse(List<String> lines) {
      return new Parser(lines).parse();
    }
    public static Map<String, ProfileSet> parse(String defaultContent) {
      return parse(Arrays.asList(defaultContent.split("(\r\n|\r|\n)")));
    }
  }



}