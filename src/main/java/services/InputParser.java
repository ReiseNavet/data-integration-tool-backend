package services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import services.interfaces.SchemaParser;
import services.parsers.schema.GBFSParser;
import services.parsers.schema.IXSIParser;
import services.parsers.schema.NeTExParser;
import services.parsers.schema.SpreadsheetParser;
import services.utils.OntologyConcept;
import services.utils.Unzipper;


public class InputParser {

  public File parseInput(String filepathLoad, String filepathSaveIfParsed) throws Exception {
    String filetype = FilenameUtils.getExtension(filepathLoad);
    if (filetype.equals("owl") || filetype.equals("rdf")){
      return new File(filepathLoad);
    } 
    SchemaParser parser = null;
    if (filetype.equals("zip")){
      filepathLoad = Unzipper.unzip(filepathLoad);
      parser = determineZipParser(filepathLoad); //Either GBFS or NeTEx
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



  private SchemaParser determineZipParser(String path) {
    File folder = new File(path);
    
    if(!folder.isDirectory()) {
      return null;
    }
    
    List<String> folderItems = List.of(folder.list());
    if(
      folderItems.contains("netex_part_1") &&
      folderItems.contains("netex_part_2") &&
      folderItems.contains("netex_part_3") &&
      folderItems.contains("netex_framework")
    ) {
      return new NeTExParser();
    }
    // oversimplified approach, but does the job. 
    // if the user inputs an invalid zip it will be
    // read as a GBFS, and then crash. 
    return new GBFSParser();
  }
}
