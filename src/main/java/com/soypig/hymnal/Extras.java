package com.soypig.hymnal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soypig.hymnal.util.AlphanumComparator;
import com.soypig.hymnal.util.CatTree;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Extras {

  private List<Hymn> hymns;
  private SortedMap<String, Hymn> byNumber;
  private CatTree<String> byCategory;

  public Extras(List<Hymn> hymns) {
    this.hymns = hymns;
  }

  private void orderByNumber() {
    byNumber = new TreeMap<String, Hymn>(new AlphanumComparator());
    for (Hymn hymn : hymns) {
      String number = hymn.properties.get("BookNumber");
      if (number != null) {
        byNumber.put(number, hymn);
      }
    }
  }

  private void orderByCategory() {
    if (byNumber == null) {
      orderByNumber();
    }
    byCategory = new CatTree<String>(Pattern.compile("\\s+-\\s+"));
    for (String number : byNumber.keySet()) {
      String category = byNumber.get(number).properties.get("Category");
      if (category != null) {
        byCategory.add(category, number);
      }
    }
  }

  public void addPrevNext() {
    if (byNumber == null) {
      orderByNumber();
    }
    List<String> order = new ArrayList<String>(byNumber.keySet());
    for (int i = 0; i < order.size(); i++) {
      Hymn curr = byNumber.get(order.get(i));
      if (i > 0) {
        curr.properties.put("PrevNumber", order.get(i - 1));
        curr.properties.put("PrevId", byNumber.get(order.get(i - 1)).id);
      }
      if (i < order.size() - 1) {
        curr.properties.put("NextNumber", order.get(i + 1));
        curr.properties.put("NextId", byNumber.get(order.get(i + 1)).id);
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

  public void writeCatTree(File outputPath) throws Exception {

    if (outputPath.getParentFile().exists() && !outputPath.isDirectory()) {
      writeCatTree(new FileOutputStream(outputPath));
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
      System.err.println("Example: 0 /Users/me/Desktop/hymnal_html/ /Users/me/Desktop/hymnal/json/");
      return;
    }

    Converter.InputFormat inputFormat = Converter.InputFormat.values()[Integer.parseInt(args[0])];
    String inputPath = args[1];
    String outputPath = args[2];

    Converter converter = new Converter(inputFormat);

    converter.extract(inputPath);
    System.out.println("Extracted " + converter.getHymns().size() + " hymns from " + inputPath);

    Extras extras = new Extras(converter.getHymns());

    extras.writeCatTree(new File(outputPath + "/category.json"));
    System.out.println("Wrote to " + outputPath);

  }

}
