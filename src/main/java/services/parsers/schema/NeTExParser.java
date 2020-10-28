package services.parsers.schema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;

import services.interfaces.SchemaParser;
import services.utils.OntologyConcept;

public class NeTExParser implements SchemaParser {

    public static void main(String[] args) throws Exception{
        List<OntologyConcept> concepts = new NeTExParser().parse("files/parse_files/NeTEx_xsd");
        String filepath = "temp/parse_files/ParseNeTEx.owl";
        OntologyConcept.toOWLFile(concepts, filepath);
      }

    @Override
    public List<OntologyConcept> parse(String filePath) throws Exception {
        return new IXSIParser().parse("files/parse_files/NeTEx_xsd/NeTEx_publication.xsd");
    }
}
