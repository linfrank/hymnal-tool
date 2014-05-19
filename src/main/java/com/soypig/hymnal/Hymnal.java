package com.soypig.hymnal;

import java.util.*;

public class Hymnal {

  public String id;

  public String title;

  // Key -> value
  public Map<String, String> properties = new HashMap<String, String>();

  // Page order
  private SortedSet<String> order;

  // Hymn number -> hymn Id
  public Map<String, String> binding = new HashMap<String, String>();

  public Hymnal(Comparator<String> comparator){
    if(comparator!=null){
      order = new TreeSet<String>(comparator);
    }
    else{
      // insertion order
      order = new TreeSet<String>(new Comparator<String>(){
        Map<String, Integer> orderMap = new HashMap<String, Integer>();
        @Override
        public int compare(String s1, String s2){
          if(!orderMap.containsKey(s1)){
            orderMap.put(s1, orderMap.size());
          }
          if(!orderMap.containsKey(s2)){
            orderMap.put(s2, orderMap.size());
          }
          return orderMap.get(s1) - orderMap.get(s2);
        }
      });
    }
  }

  public Hymnal(){
    this(null);
  }

  public void addHymn(String number, String id){
    binding.put(number, id);
    order.add(number);
  }

  public SortedSet<String> getOrder(){
    return order;
  }

  @Override
  public String toString() {

    StringBuilder b = new StringBuilder();

    if (id != null) b.append("Id: ").append(id).append("\n");
    if (title != null) b.append("Title: ").append(title).append("\n");

    if (properties.size() > 0) {
      b.append("\n").append("Properties: ").append(properties);
    }

    if (order.size() > 0) {
      b.append("\n");
      for (String number : order) {
        b.append("\n").append(number);
      }
    }

    if (binding.size() > 0) {
      b.append("\n");
      for (String number : binding.keySet()) {
        b.append("\n").append(number).append(" : ").append(binding.get(number));
      }
    }

    b.append("\n");

    return b.toString();

  }

}