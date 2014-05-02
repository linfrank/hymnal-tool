package com.soypig.hymnal;

import java.util.*;

public class Hymn {

  public String id;
  public String title;
  public List<String> writers = new ArrayList<String>();
  public List<String> composers = new ArrayList<String>();
  public String meter;
  public Locale language;
  public String note;

  public List<Section> song = new ArrayList<Section>();

  public Map<Locale, String> translations = new HashMap<Locale, String>();
  public Map<String, String> properties = new HashMap<String, String>();

  public static class Section {

    public int stanza;
    public boolean chorus;
    public List<String> lines = new ArrayList<String>();

  }

  @Override
  public String toString() {

    StringBuilder b = new StringBuilder();

    if (id != null) b.append("Id: ").append(id).append("\n");
    if (title != null) b.append("Title: ").append(title).append("\n");
    if (writers.size() > 0) b.append("Writer(s): ").append(writers).append("\n");
    if (composers.size() > 0) b.append("Composer(s): ").append(composers).append("\n");
    if (meter != null) b.append("Meter: ").append(meter).append("\n");
    if (language != null) b.append("Language: ").append(language.getDisplayLanguage()).append("\n");
    if (note != null) b.append("Note: ").append(note).append("\n");

    for (Section section : song) {
      b.append("\n");
      for (int i = 0; i < section.lines.size(); i++) {
        if (i > 0 || section.chorus || section.stanza < 1) {
          b.append("   ");
        }
        else {
          if (section.stanza < 10) {
            b.append(" ");
          }
          b.append(section.stanza).append(" ");
        }
        b.append(section.lines.get(i)).append("\n");
      }
    }

    if (translations.size() > 0) {
      b.append("\n").append("Translations: ").append(translations.values());
    }

    if (properties.size() > 0) {
      b.append("\n").append("Properties: ").append(properties);
    }

    b.append("\n");

    return b.toString();

  }

}
