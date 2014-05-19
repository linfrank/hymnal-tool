package com.soypig.hymnal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soypig.hymnal.util.CatTree;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Extras {

  private List<Hymn> hymns;
  private Hymnal hymnal;
  private CatTree<String> byCategory;

  public Extras(List<Hymn> hymns, Hymnal hymnal) {
    this.hymns = hymns;
    this.hymnal = hymnal;
  }

  private void orderByCategory() {

    Map<String, Hymn> idMap = new HashMap<String, Hymn>(hymns.size());
    for (Hymn hymn : hymns) {
      idMap.put(hymn.id, hymn);
    }

    byCategory = new CatTree<String>(Pattern.compile("\\s+-\\s+"));
    for (String number : hymnal.getOrder()) {
      String category = idMap.get(hymnal.binding.get(number)).properties.get("Category");
      if (category != null) {
        byCategory.add(category, number);
      }
    }
  }

  public void writeCatTree(OutputStream os) throws Exception {

    if (byCategory == null) {
      orderByCategory();
    }

    Gson gson = new GsonBuilder()
     .setPrettyPrinting()
     .create();

    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "utf8"));
    writer.print(gson.toJson(byCategory.root));
    writer.close();

  }

  public void writeCatTree(File outputPath, Converter.InputFormat inputFormat) throws Exception {

    if (outputPath.exists() && outputPath.isDirectory()) {
      switch (inputFormat) {
        case LSM_ENGLISH_HYMNAL_2014:
          writeCatTree(new FileOutputStream(outputPath + "/LSM.English.topics.json"));
          break;
      }
    }
    else {
      throw new IOException("Bad output path: " + outputPath);
    }

  }

  public static void main(String[] args) throws Exception {

    if (args.length != 3) {
      System.err.println("Usage: <Input Format> <Input Path> <Output Path>");
      System.err.println();
      System.err.println("Input options and descriptions:");
      for (int i = 0; i < Converter.InputFormat.values().length; i++) {
        Converter.InputFormat f = Converter.InputFormat.values()[i];
        System.err.println("  " + i + " : " + f + " (" + f.desc + ")");
        System.err.println("      <Input Path> is " + f.path);
      }
      System.err.println();
      System.err.println("Example: 0 /Users/me/input/hymnal_html/ /Users/me/output/");
      return;
    }

    Converter.InputFormat inputFormat = Converter.InputFormat.values()[Integer.parseInt(args[0])];
    String inputPath = args[1];
    String outputPath = args[2];

    Converter converter = new Converter(inputFormat);

    converter.extract(inputPath);
    System.out.println("Extracted " + converter.getHymns().size() + " hymns from " + inputPath);

    Extras extras = new Extras(converter.getHymns(), converter.getHymnal());

    extras.writeCatTree(new File(outputPath), inputFormat);
    System.out.println("Wrote to " + outputPath);

  }

}
