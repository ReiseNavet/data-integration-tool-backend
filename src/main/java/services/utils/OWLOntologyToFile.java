package services.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OWLOntologyToFile {
  public static File convert(OWLOntology owlOntology, String saveLocation, OWLOntologyManager manager) throws Exception{
    OutputStream outputStream = new FileOutputStream(saveLocation);
    manager.saveOntology(owlOntology, outputStream);
    outputStream.close();
    return new File(saveLocation);
  }
}
