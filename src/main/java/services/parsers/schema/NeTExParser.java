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
        List<OntologyConcept> ontoConcepts = new ArrayList<OntologyConcept>();

        System.setProperty("javax.xml.transform.TransformerFactory","com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        System.out.println(filePath + "/NeTEx_publication.xsd");
        XsdParser parserInstance = null;
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/NeTEx_publication.xsd");
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/netex_service/netex_filter_object-v1.0.xsd");
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/netex_framework/netex_utility/netex_utility_types-v1.0.xsd");
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/netex_framework/netex_utility/netex_utility_time-v1.0.xsd");
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/netex_framework/netex_utility/netex_location_types-v1.0.xsd");
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/netex_framework/netex_utility/netex_dataSource_version-v1.0.xsd");
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/netex_framework/netex_utility/netex_layer_support-v1.0.xsd");
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/netex_framework/netex_utility/netex_responsibilitySet_version-v1.0.xsd");
        parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/netex_service/netex_dataObjectRequest_service-v1.0.xsd");
        //parserInstance = new XsdParser("files/parse_files/NeTEx_xsd/xml/xml.xsd");
        
        //Stream<XsdElement> elementsStream = parserInstance.getResultXsdElements();
        Stream<XsdSchema> schemasStream = parserInstance.getResultXsdSchemas();
        for (XsdSchema schema : (Iterable<XsdSchema>) schemasStream::iterator) {
          parseSchema(schema);
        }
    
        
        //File NeTExFolder = new File(filePath);
        //String[] paths = NeTExFolder.list();


        return ontoConcepts;
    }

    public void parseSchema(XsdSchema schema) throws Exception {
        List<XsdSimpleType> simpleTypes = schema.getChildrenSimpleTypes().collect(Collectors.toList());
        if (simpleTypes.size() > 0) System.out.println("hi1");
    
        List<XsdComplexType> complexTypes = schema.getChildrenComplexTypes().collect(Collectors.toList());
        if (complexTypes.size() > 0) System.out.println("hi2");
    
        List<XsdElement> xsdElements = schema.getChildrenElements().collect(Collectors.toList());
        if (xsdElements.size() > 0) System.out.println("hi3");
    
      }    
}
