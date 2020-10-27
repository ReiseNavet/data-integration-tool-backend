package services;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import services.dataclasses.OntologyConcept;
import services.interfaces.SchemaParser;
import services.parsers.schema.GBFSParser;
import services.parsers.schema.IXSIParser;
import services.parsers.schema.SpreadsheetParser;


public class InputParser {

  public OWLOntology parseInput(String filepath, OWLOntologyManager manager) throws Exception {
    String filetype = FilenameUtils.getExtension(filepath);
    if (filetype.equals("owl") || filetype.equals("rdf")){
      OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(filepath));
      return ontology;
    } 
    SchemaParser parser = null;
    if (filetype.equals("zip")){
      parser = new GBFSParser(); //TODO: Add NeTEx if we decide to implement it
    } else if (filetype.equals("xsd")){
      parser = new IXSIParser();
    } else if (filetype.equals("xls") || filetype.equals("xlsx")){
      parser = new SpreadsheetParser();
    } else {
      throw new IllegalArgumentException("Unsupported fileformat");
    }
    List<OntologyConcept> concepts = parser.parse(filepath);
    return OntologyConcept.toOwlOntology(concepts);
  }
}
