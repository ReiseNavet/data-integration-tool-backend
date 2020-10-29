package services.parsers.schema;

import java.util.List;

import services.interfaces.SchemaParser;
import services.utils.OntologyConcept;
import services.utils.Unzipper;

public class NeTExParser implements SchemaParser {

    public static void main(String[] args) throws Exception{
        List<OntologyConcept> concepts = new NeTExParser().parse(Unzipper.unzip("files/parse_files/NeTEx_xsd.zip", "temp/parse_files/xsd"));
        String filepath = "temp/parse_files/ParseNeTEx.owl";
        OntologyConcept.toOWLFile(concepts, filepath);
      }

    @Override
    public List<OntologyConcept> parse(String filePath) throws Exception {
        return new IXSIParser().parse(filePath + "/NeTEx_publication.xsd");
    }
}
