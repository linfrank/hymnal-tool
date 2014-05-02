package com.soypig.hymnal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Hymn {

  public String id;
  public String writer;
  public String composer;
  public String meter;
  public Locale language;

  public List<Stanza> stanzas=new ArrayList<Stanza>();

  public Map<Locale,String> translations=new HashMap<Locale,String>();

  public Map<String,String> properties=new HashMap<String,String>();

  public static class Stanza {

    public List<Section> sections=new ArrayList<Section>();

    public static class Section {

      public boolean chorus;
      public List<String> lines=new ArrayList<String>();

    }

  }

  @Override
  public String toString(){

    StringBuilder b=new StringBuilder();

    if(id!=null)b.append("Id: ").append(id).append("\n");
    if(writer!=null)b.append("Writer: ").append(writer).append("\n");
    if(composer!=null)b.append("Composer: ").append(composer).append("\n");
    if(meter!=null)b.append("Meter: ").append(meter).append("\n");
    if(language!=null)b.append("Language: ").append(language.getDisplayLanguage()).append("\n");

    for(Stanza stanza:stanzas){
      for(Stanza.Section section:stanza.sections){
        b.append("\n");
        for(String line:section.lines){
          if(section.chorus)b.append(" ");
          b.append(line).append("\n");
        }
      }
    }

    if(translations.size()>0){
      b.append("\n").append("Translations:");
      for(Map.Entry<Locale,String> trans:translations.entrySet()){
        b.append(" ").append(trans.getKey()).append(":").append(trans.getValue());
      }
    }

    if(properties.size()>0){
      b.append("\n").append("Properties: ").append(properties);
    }

    b.append("\n");

    return b.toString();

  }

}
