package com.soypig.hymnal.parser;

import com.soypig.hymnal.Hymn;
import com.soypig.hymnal.Hymnal;
import com.soypig.hymnal.util.AlphanumComparator;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LSMEnglishHymnalHTMLParser implements Parser {

  public final static String PUB_PREFIX = "LSM.";
  public final static Locale[] LOCALES = {
   Locale.KOREAN,
   Locale.CHINESE,
   new Locale("es"),
   new Locale("ru"),
   new Locale("tl"),
   Locale.FRENCH,
   new Locale("pt")
  };

  private DOMParser parser;

  private List<Hymn> hymns;
  private Hymnal hymnal;

  public LSMEnglishHymnalHTMLParser() throws Exception {

    parser = new DOMParser();
    //parser.setProperty("http://cyberneko.org/html/properties/default-encoding","" );

    hymns = new ArrayList<Hymn>(2000);
    hymnal = new Hymnal(new AlphanumComparator());

    hymnal.id = PUB_PREFIX + "English";
    hymnal.title = "Hymns";

  }

  @Override
  public List<Hymn> getHymns() {
    return hymns;
  }

  @Override
  public Hymnal getHymnal() {
    return hymnal;
  }

  @Override
  public void parse(InputStream is) throws Exception {

    parser.parse(new InputSource(is));
    Element d = parser.getDocument().getDocumentElement();

    Hymn h = new Hymn();

    String title = findElement(d, "title").getTextContent().trim();
    String number = title.substring(title.indexOf('#') + 1);
    h.id = PUB_PREFIX + Locale.ENGLISH.getDisplayLanguage() + "." + number;

    List<Element> meta = findElements(findElement(d, "div", "class", "metabox"), "tr");
    for (Element e : findElements(meta.get(0), "a")) {
      h.writers.add(e.getFirstChild().getTextContent().trim());
    }
    for (Element e : findElements(meta.get(1), "a")) {
      h.composers.add(e.getFirstChild().getTextContent().trim());
    }
    h.meter = findElement(meta.get(2), "a").getFirstChild().getTextContent().trim();

    h.language = Locale.ENGLISH;

    Element note = findElement(d, "p", "class", "note");
    h.note = note == null ? null : note.getTextContent().trim();

    String nums = findElement(d, "p", "class", "nums").getTextContent();
    String[] transTokens = nums.split("[\\p{Z}\\s]+");
    for (int i = 0; i < transTokens.length; i++) {
      String num = transTokens[i].substring(transTokens[i].indexOf(':') + 1);
      if (num.indexOf('[') > -1) num = num.substring(0, num.indexOf('['));
      if (num.length() > 0 && !num.equals("-")) {
        h.translations.put(LOCALES[i], PUB_PREFIX + LOCALES[i].getDisplayLanguage() + "." + num);
      }
    }

    h.properties.put("Category", findElements(findElement(d, "div", "class", "titlebox"), "span").get(1).getTextContent().trim());
    //h.properties.put("Book", "Hymns");
    //h.properties.put("BookNumber", number);
    hymnal.addHymn(number, h.id);

    List<Element> divs = findElements(d, "div");
    int stanza = 0;
    for (Element div : divs) {
      String clazz = div.getAttribute("class");
      if (clazz != null &&
       (clazz.equals("verse") || clazz.equals("chorus") || clazz.equals("singleverse"))) {
        Hymn.Section s = new Hymn.Section();
        if (clazz.equals("verse")) s.stanza = ++stanza;
        else if (clazz.equals("chorus")) s.chorus = true;
        List<Element> lines = findElements(div, "p");
        for (Element line : lines) {
          s.lines.add(line.getTextContent().trim());
        }
        h.song.add(s);
      }
    }

    hymns.add(h);

  }

  private static List<Element> findElements(Element me, String tag, String att, String val) {
    tag = tag.toUpperCase();
    List<Element> l = new ArrayList<Element>();
    if (me.getTagName().equals(tag) && (att == null || (me.hasAttribute(att) && me.getAttribute(att).equals(val)))) {
      l.add(me);
    }
    for (int i = 0; i < me.getChildNodes().getLength(); i++) {
      Node child = me.getChildNodes().item(i);
      if (child instanceof Element) {
        l.addAll(findElements((Element) child, tag, att, val));
      }
    }
    return l;
  }

  private static List<Element> findElements(Element me, String tag) {
    return findElements(me, tag, null, null);
  }

  private static Element findElement(Element me, String tag, String att, String val) {
    tag = tag.toUpperCase();
    if (me.getTagName().equals(tag) && (att == null || (me.hasAttribute(att) && me.getAttribute(att).equals(val)))) {
      return me;
    }
    else {
      for (int i = 0; i < me.getChildNodes().getLength(); i++) {
        Node child = me.getChildNodes().item(i);
        if (child instanceof Element) {
          Element e = findElement((Element) child, tag, att, val);
          if (e != null) return e;
        }
      }
      return null;
    }
  }

  private static Element findElement(Element me, String tag) {
    return findElement(me, tag, null, null);
  }

}
