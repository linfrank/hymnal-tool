package com.soypig.hymnal.parser;

import com.soypig.hymnal.Hymn;
import com.sun.tools.javac.util.List;

import java.io.InputStream;

public interface Parser {

  public List<Hymn> parse(InputStream is)throws Exception;

}
