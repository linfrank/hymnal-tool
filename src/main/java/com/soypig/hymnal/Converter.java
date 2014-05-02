package com.soypig.hymnal;

import com.google.gson.Gson;
import com.soypig.hymnal.parser.LSMEnglishHymnalHTMLParser;
import com.soypig.hymnal.parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Converter {

  public enum InputFormat {

    LSM_ENGLISH_HYMNAL_2014(
     "e.g., 'LSM English Hymnal HTML [2014-02-01].7z' from https://sites.google.com/site/hymnalfeedback/home",
     "root folder after unzipping"
    );

    public final String desc;
    public final String path;

    InputFormat(String desc, String path) {
      this.desc = desc;
      this.path = path;
    }

  }

  public enum OutputFormat {

    TXT_SINGLE("one large plain text file", "output file path"),
    TXT_MULTI("multiple plain text files, one per hymn", "output folder path"),
    JSON_SINGLE("one large JSON file", "output file path"),
    JSON_MULTI("multiple JSON files, one per hymn", "output folder path");

    public final String desc;
    public final String path;

    OutputFormat(String desc, String path) {
      this.desc = desc;
      this.path = path;
    }

  }

  private InputFormat inputFormat;
  private OutputFormat outputFormat;

  private List<Hymn> hymns;

  private Parser parser;
  private Gson gson;

  public Converter(InputFormat inputFormat, OutputFormat outputFormat) throws Exception {
    this.inputFormat = inputFormat;
    this.outputFormat = outputFormat;
    hymns = new ArrayList<Hymn>(2000);
    switch (inputFormat) {
      case LSM_ENGLISH_HYMNAL_2014:
        parser = new LSMEnglishHymnalHTMLParser();
        break;
    }
    switch (outputFormat) {
      case JSON_SINGLE:
      case JSON_MULTI:
        gson = new Gson();
        break;
    }
  }

  public List<Hymn> getHymns(){return hymns;}

  public void extract(InputStream is) throws Exception {
    hymns.addAll(parser.parse(is));
  }

  public void extract(File inputPath) throws Exception {
    switch (inputFormat) {
      case LSM_ENGLISH_HYMNAL_2014:
        if (inputPath.exists() && inputPath.isDirectory()) {
          for (File file : inputPath.listFiles()) {
            if(file.getName().toLowerCase().endsWith(".html")) {
              extract(new FileInputStream(file));
            }
          }
        }
        else {
          throw new IOException("Bad input path for " + inputFormat + ": " + inputPath);
        }
        break;
    }
  }

  public void extract(String inputPath) throws Exception {
    switch (inputFormat) {
      case LSM_ENGLISH_HYMNAL_2014:
        extract(new File(inputPath+"/html/hymns"));
        break;
    }
  }

  public void write(OutputStream os, Hymn hymn) throws Exception {
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "utf8"));
    switch (outputFormat) {
      case TXT_SINGLE:
      case TXT_MULTI:
        writer.println(hymn);
        break;
      case JSON_SINGLE:
      case JSON_MULTI:
        writer.println(gson.toJson(hymn));
        break;
    }
    writer.flush();
  }

  public void write(File outputPath) throws Exception {
    switch (outputFormat) {
      case TXT_SINGLE:
      case JSON_SINGLE:
        if (outputPath.getParentFile().exists() && !outputPath.isDirectory()) {
          FileOutputStream fos = new FileOutputStream(outputPath);
          for (Hymn hymn : hymns) {
            write(fos, hymn);
          }
          fos.close();
        }
        else {
          throw new IOException("Bad output path for " + outputFormat + ": " + outputPath);
        }
        break;
      case TXT_MULTI:
      case JSON_MULTI:
        if (outputPath.exists() && outputPath.isDirectory()) {
          String ext = "";
          switch (outputFormat) {
            case TXT_MULTI:
              ext = ".txt";
              break;
            case JSON_MULTI:
              ext = ".json";
              break;
          }
          for (Hymn hymn : hymns) {
            FileOutputStream fos = new FileOutputStream(outputPath + "/" + hymn.id + ext);
            write(fos, hymn);
            fos.close();
          }
        }
        else {
          throw new IOException("Bad output path for " + outputFormat + ": " + outputPath);
        }
        break;
    }
  }

  public void write(String inputPath) throws Exception {
    switch (outputFormat) {
      case TXT_SINGLE:
      case TXT_MULTI:
      case JSON_SINGLE:
      case JSON_MULTI:
        write(new File(inputPath));
        break;
    }
  }

  public static void main(String[] args) throws Exception{

    if (args.length != 4) {
      System.err.println("Usage: <Input Format> <Input Path> <Output Format> <Output Path>");
      System.err.println("Input options and descriptions:");
      for (int i = 0; i < InputFormat.values().length; i++) {
        InputFormat f = InputFormat.values()[i];
        System.err.println(" " + i + ":" + f + " (" + f.desc + ")" + " input path is " + f.path);
      }
      System.err.println("Output options and descriptions:");
      for (int i = 0; i < OutputFormat.values().length; i++) {
        OutputFormat f = OutputFormat.values()[i];
        System.err.println(" " + i + ":" + f + " (" + f.desc + ")" + " output path is " + f.path);
      }
      System.err.println("Example: 0 /Users/me/Desktop/hymnal_html/ 2 /Users/me/Desktop/hymnal.json");
    }

    String inputFormat=args[0];
    String inputPath=args[1];
    String outputFormat=args[2];
    String outputPath=args[3];

    Converter converter=new Converter(
     InputFormat.values()[Integer.parseInt(inputFormat)],
     OutputFormat.values()[Integer.parseInt(outputFormat)]);

    converter.extract(inputPath);
    System.out.println("Extracted "+converter.getHymns().size()+" hymns from "+inputPath);
    converter.write(outputPath);
    System.out.println("Wrote "+converter.getHymns().size()+" hymns to "+outputPath);

  }

}