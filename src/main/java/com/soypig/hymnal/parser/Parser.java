package com.soypig.hymnal.parser;

import com.soypig.hymnal.Hymn;

import java.io.InputStream;
import java.util.List;

public interface Parser {

  public List<Hymn> parse(InputStream is)throws Exception;

}
