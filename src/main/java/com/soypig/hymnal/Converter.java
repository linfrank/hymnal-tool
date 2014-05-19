package com.soypig.hymnal;

import com.google.gson.Gson;
import com.soypig.hymnal.parser.LSMEnglishHymnalHTMLParser;
import com.soypig.hymnal.parser.Parser;

import java.io.*;
import java.util.List;

public class Converter {

  public enum InputFormat {

    LSM_ENGLISH_HYMNAL_2014(
     "e.g., 'LSM English Hymnal HTML [2014-02-01].7z'",
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

    TXT_SINGLE("one plain text hymns file", "output folder path"),
    TXT_MULTI("multiple plain text files, one per hymn", "output folder path"),
    JSON_SINGLE("one JSON hymns file", "output folder path"),
    JSON_MULTI("multiple JSON files, one per hymn", "output folder path");

    public final String desc;
    public final String path;

    OutputFormat(String desc, String path) {
      this.desc = desc;
      this.path = path;
    }

  }

  private InputFormat inputFormat;

  private Parser parser;

  private Gson gson;

  public Converter(InputFormat inputFormat) throws Exception {

    this.inputFormat = inputFormat;
    switch (this.inputFormat) {
      case LSM_ENGLISH_HYMNAL_2014:
        parser = new LSMEnglishHymnalHTMLParser();
        break;
    }

    gson = new Gson();

  }

  public List<Hymn> getHymns() {
    return parser.getHymns();
  }

  public Hymnal getHymnal() {
    return parser.getHymnal();
  }

  public void extract(InputStream is) throws Exception {
    parser.parse(is);
  }

  public void extract(File inputPath) throws Exception {
    switch (inputFormat) {
      case LSM_ENGLISH_HYMNAL_2014:
        File[] files = inputPath.listFiles();
        if (files != null) {
          for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".html")) {
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
        extract(new File(inputPath + "/html/hymns"));
        break;
    }
  }

  private void write(OutputStream os, Object hymnData, OutputFormat outputFormat) throws Exception {
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "utf8"));
    switch (outputFormat) {
      case TXT_SINGLE:
      case TXT_MULTI:
        writer.println(hymnData);
        break;
      case JSON_SINGLE:
      case JSON_MULTI:
        writer.print(gson.toJson(hymnData));
        break;
    }
    writer.flush();
  }

  public void write(File outputPath, OutputFormat outputFormat) throws Exception {

    if (!outputPath.exists() || !outputPath.isDirectory()) {
      throw new IOException("Bad output path for " + outputFormat + ": " + outputPath);
    }

    FileOutputStream fos;

    switch (outputFormat) {
      case TXT_SINGLE:
      case TXT_MULTI:
        fos = new FileOutputStream(outputPath + "/" + getHymnal().id + ".hymnal.txt");
        break;
      case JSON_SINGLE:
      case JSON_MULTI:
        fos = new FileOutputStream(outputPath + "/" + getHymnal().id + ".hymnal.json");
        break;
      default:
        fos = null;
    }
    write(fos, getHymnal(), outputFormat);
    fos.close();

    switch (outputFormat) {
      case TXT_SINGLE:
        fos = new FileOutputStream(outputPath + "/" + getHymnal().id + ".hymns.txt");
        for (Hymn hymn : getHymns()) {
          write(fos, hymn, outputFormat);
        }
        fos.close();
        break;
      case JSON_SINGLE:
        fos = new FileOutputStream(outputPath + "/" + getHymnal().id + ".hymns.json");
        write(fos, getHymns(), outputFormat);
        fos.close();
        break;
      case TXT_MULTI:
      case JSON_MULTI:
        String ext = outputFormat == OutputFormat.JSON_MULTI ? ".json" : ".txt";
        for (Hymn hymn : getHymns()) {
          fos = new FileOutputStream(outputPath + "/" + hymn.id + ext);
          write(fos, hymn, outputFormat);
          fos.close();
        }
        break;
    }

  }

  public void write(String inputPath, OutputFormat outputFormat) throws Exception {
    switch (outputFormat) {
      case TXT_SINGLE:
      case TXT_MULTI:
      case JSON_SINGLE:
      case JSON_MULTI:
        write(new File(inputPath), outputFormat);
        break;
    }
  }

  public static void main(String[] args) throws Exception {

    if (args.length != 4) {
      System.err.println("Usage: <Input Format> <Input Path> <Output Format> <Output Path>");
      System.err.println();
      System.err.println("Input options and descriptions:");
      for (int i = 0; i < InputFormat.values().length; i++) {
        InputFormat f = InputFormat.values()[i];
        System.err.println("  " + i + " : " + f + " (" + f.desc + ")");
        System.err.println("      <Input Path> is " + f.path);
      }
      System.err.println();
      System.err.println("Output options and descriptions:");
      for (int i = 0; i < OutputFormat.values().length; i++) {
        OutputFormat f = OutputFormat.values()[i];
        System.err.println("  " + i + " : " + f + " (" + f.desc + ")");
        System.err.println("      <Input Path> is " + f.path);
      }
      System.err.println();
      System.err.println("Example: 0 /Users/me/input/hymnal_html/ 2 /Users/me/Desktop/output/");
      return;
    }

    InputFormat inputFormat = InputFormat.values()[Integer.parseInt(args[0])];
    String inputPath = args[1];
    OutputFormat outputFormat = OutputFormat.values()[Integer.parseInt(args[2])];
    String outputPath = args[3];

    Converter converter = new Converter(inputFormat);

    converter.extract(inputPath);
    System.out.println("Extracted " + converter.getHymns().size() + " hymns from " + inputPath);

    converter.write(outputPath, outputFormat);
    System.out.println("Wrote " + converter.getHymns().size() + " hymns to " + outputPath);

  }

}
