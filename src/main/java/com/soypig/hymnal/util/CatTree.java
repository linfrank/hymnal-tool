package com.soypig.hymnal.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public class CatTree<T> {

  public static class Node<T>{

    public LinkedHashSet<T> items=new LinkedHashSet<T>();
    public LinkedHashMap<String,Node<T>> children=new LinkedHashMap<String, Node<T>>();

    public void add(String[] cat,T item){
      if(cat!=null&&cat.length>0){
        Node<T> child=children.get(cat[0]);
        if(child==null){
          child=new Node<T>();
          children.put(cat[0],child);
        }
        if(cat.length==1){
          child.items.add(item);
        }
        else{
          add(Arrays.copyOfRange(cat, 1, cat.length),item);
        }
      }
    }

    public LinkedHashSet<T> get(String[] cat){
      if(cat==null||cat.length<1){
        return null;
      }
      else{
        Node<T> child=children.get(cat[0]);
        if(child==null){
          return null;
        }
        else {
          if (cat.length == 1) {
            return child.items;
          }
          else {
            return get(Arrays.copyOfRange(cat, 1, cat.length));
          }
        }
      }
    }

  }

  public Node<T> root;

  private Pattern subRegex;

  public CatTree(Pattern subRegex){
    this.subRegex=subRegex;
    root=new Node<T>();
  }

  public CatTree(){
    this(null);
  }

  public void add(String[] cat,T item){
    root.add(cat,item);
  }

  public void add(String cat,T item){
    if(subRegex!=null){
      add(subRegex.split(cat), item);
    }
    else{
      add(new String[]{cat},item);
    }
  }

  public LinkedHashSet<T> get(String[] cat){
    return root.get(cat);
  }

  public LinkedHashSet<T> get(String cat){
    if(subRegex!=null){
      return get(subRegex.split(cat));
    }
    else{
      return get(new String[]{cat});
    }
  }

}
