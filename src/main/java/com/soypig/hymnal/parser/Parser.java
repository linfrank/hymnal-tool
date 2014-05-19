package com.soypig.hymnal.parser;

import com.soypig.hymnal.Hymn;
import com.soypig.hymnal.Hymnal;

import java.io.InputStream;
import java.util.List;

public interface Parser {

  public void parse(InputStream is) throws Exception;

  public List<Hymn> getHymns();

  public Hymnal getHymnal();

}
