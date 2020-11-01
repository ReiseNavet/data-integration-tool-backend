package services.parsers.schema;

import java.util.ArrayList;
import java.util.List;

import algorithms.utilities.StringUtilities;
import rita.json.JSONObject;
import services.interfaces.SchemaParser;
import services.utils.FileUtil;
import services.utils.OntologyConcept;
import services.utils.Unzipper;

import java.io.File;

public class GBFSParser implements SchemaParser {

  public static void main(String[] args) throws Exception {
    List<OntologyConcept> concepts = new GBFSParser().parse(Unzipper.unzip("files/parse_files/GBFS.zip", "temp/parse_files/GBFS"));
    String filepath = "temp/parse_files/ParseGBFS.owl";
    OntologyConcept.toOWLFile(concepts, filepath);
  }

  @Override
  public List<OntologyConcept> parse(String filePath) throws Exception {
    List<OntologyConcept> ontoConcepts = new ArrayList<OntologyConcept>();

    File GBFSFolder = new File(filePath);
    String[] jsonPaths = GBFSFolder.list();
    for (String jsonPath : jsonPaths){
      String jsonRaw = FileUtil.readFile(filePath + "\\" + jsonPath);
      JSONObject json = new JSONObject(jsonRaw);
      JSONObject definitions = json.getJSONObject("definitions");
      for(Object domain_key : definitions.keySet()){
        OntologyConcept ontoConcept = new OntologyConcept();
        ontoConcept.name = domain_key.toString();
        ontoConcepts.add(ontoConcept);
        JSONObject concept = definitions.getJSONObject(domain_key.toString());
        JSONObject properties =  concept.getJSONObject("properties");
        for (Object data_key : properties.keySet()){
          OntologyConcept ontoConcept_sub = new OntologyConcept();
          ontoConcept_sub.name = StringUtilities.RemoveDomain(domain_key.toString(), data_key.toString());
          ontoConcept_sub.domain = domain_key.toString();
          ontoConcepts.add(ontoConcept_sub);
        }
      }
    }

    return ontoConcepts;
  }
    
}
