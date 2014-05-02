package com.soypig.hymnal.parser;

import com.soypig.hymnal.Hymn;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.List;

public class LSMEnglishHymnalHTMLParser implements Parser{

  SAXParserFactory spf;

  public LSMEnglishHymnalHTMLParser(){
    spf = SAXParserFactory.newInstance();
  }


  @Override
  public List<Hymn> parse(InputStream is)throws Exception{

    SAXParser saxParser = spf.newSAXParser();
    XMLReader xmlReader = saxParser.getXMLReader();
    LsmEnglishHymnalHtml handler=new LsmEnglishHymnalHtml();
    xmlReader.setContentHandler(handler);
    xmlReader.parse(new InputSource(is));

    return null;

  }

  private class LsmEnglishHymnalHtml extends DefaultHandler {

    public Hymn h=new Hymn();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes){
      text.reset
      if(localName.equalsIgnoreCase("a")){
        String href=attributes.getValue("href");
        if(href!=null){
          if(href.startsWith("../categories/")){
            String cat=href.substring(href.lastIndexOf('/')+1,href.lastIndexOf('.')).replace('_',' ');
            String sub=href.substring(href.lastIndexOf('#')+1).replace('_', ' ');
            h.properties.put("Category",cat);
            h.properties.put("Subcategory",sub);
          }
        }
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName){

    }

    @Override
    public void characters(char[] ch, int start, int length){
      71
      content = String.copyValueOf(ch, start, length).trim();
      72
    }


  }

}
