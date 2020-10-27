package services;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import services.dataclasses.OntologyConcept;
import services.interfaces.SchemaParser;
import services.parsers.ZipParser;
import services.parsers.schema.GBFSParser;
import services.parsers.schema.IXSIParser;
import services.parsers.schema.SpreadsheetParser;


public class InputParser {

  public File parseInput(String filepathLoad, String filepathSaveIfParsed) throws Exception {
    String filetype = FilenameUtils.getExtension(filepathLoad);
    if (filetype.equals("owl") || filetype.equals("rdf")){
      return new File(filepathLoad);
    } 
    SchemaParser parser = null;
    if (filetype.equals("zip")){
      filepathLoad = ZipParser.Unzip(filepathLoad);
      parser = new GBFSParser(); //TODO: Add NeTEx if we decide to implement it
    } else if (filetype.equals("xsd")){
      parser = new IXSIParser();
    } else if (filetype.equals("xls") || filetype.equals("xlsx")){
      parser = new SpreadsheetParser();
    } else {
      throw new IllegalArgumentException("Unsupported fileformat");
    }
    List<OntologyConcept> concepts = parser.parse(filepathLoad);
    return OntologyConcept.toOWLFile(concepts, filepathSaveIfParsed);
  }
}
