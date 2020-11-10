package services.interfaces;

import java.io.File;

import fr.inrialpes.exmo.align.impl.URIAlignment;

public interface Algorithm {
  public URIAlignment run(File onto1, File onto2) throws Exception;
}
