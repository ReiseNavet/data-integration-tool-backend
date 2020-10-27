package services.parsers.schema;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import algorithms.utilities.StringUtilities;
import net.lingala.zip4j.core.ZipFile;
import rita.json.JSONObject;
import services.dataclasses.OntologyConcept;
import services.interfaces.SchemaParser;
import services.parsers.ZipParser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GBFSParser implements SchemaParser {

    public static void main(String[] args) throws Exception {
        String filepathLoad = "files/temp/GBFS.zip";
        new GBFSParser().parse(ZipParser.Unzip(filepathLoad));   
    }
    @Override
    public List<OntologyConcept> parse(String filePath) throws Exception {
        List<OntologyConcept> ontoConcepts = new ArrayList<OntologyConcept>();

        File GBFSFolder = new File(filePath);
        String[] jsonPaths = GBFSFolder.list();
        for (String jsonPath : jsonPaths){
            String jsonRaw = Files.readString(Paths.get(filePath + "\\" + jsonPath));
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
