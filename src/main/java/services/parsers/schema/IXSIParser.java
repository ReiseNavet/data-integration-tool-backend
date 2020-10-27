package services.parsers.schema;

import java.util.List;

import services.interfaces.SchemaParser;
import services.utils.OntologyConcept;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IXSIParser implements SchemaParser {
  private List<OntologyConcept> concepts = new ArrayList<>();
  private XsdParser parserInstance;

  public IXSIParser(){
  }

  public static void main(String[] args) throws Exception{
    List<OntologyConcept> concepts = new IXSIParser().parse("files/parse_files/IXSI.xsd");
    String filepath = "temp/parse_files/ParseIXSI.owl";
    OntologyConcept.toOWLFile(concepts, filepath);
  }

  @Override
  public List<OntologyConcept> parse(String filePath) throws Exception {
    System.setProperty("javax.xml.transform.TransformerFactory","com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
    parserInstance = new XsdParser(filePath);
    //Stream<XsdElement> elementsStream = parserInstance.getResultXsdElements();
    Stream<XsdSchema> schemasStream = parserInstance.getResultXsdSchemas();
    for (XsdSchema schema : (Iterable<XsdSchema>) schemasStream::iterator) {
      parseSchema(schema);
    }

    return this.concepts;
      
  }

  public void parseSchema(XsdSchema schema) throws Exception {
    List<XsdSimpleType> simpleTypes = schema.getChildrenSimpleTypes().collect(Collectors.toList());
    if (simpleTypes.size() > 0) this.parseSimpleTypes(simpleTypes);

    List<XsdComplexType> complexTypes = schema.getChildrenComplexTypes().collect(Collectors.toList());
    if (complexTypes.size() > 0) this.parseComplexTypes(complexTypes);

    // List<XsdElement> xsdElements = schema.getChildrenElements().collect(Collectors.toList());
    // if (xsdElements.size() > 0) this.parseElements(xsdElements, "");

  }

  public void parseElements(List<XsdElement> xsdElements, String parent) {
    for (XsdElement elem : xsdElements) {
      OntologyConcept concept = new OntologyConcept();
      concept.name = elem.getName();
      if (elem.getAnnotation() != null){
        concept.description = elem.getAnnotation().getDocumentations().get(0).getContent();
      }
      concept.domain = parent;
      //concept.range = elem.getType();
      this.concepts.add(concept);
    }
  }

  // A simple element can only contain text
  public void parseSimpleTypes(List<XsdSimpleType> simpleTypes) {
    for (XsdSimpleType st: simpleTypes) {
      OntologyConcept concept = new OntologyConcept();
      concept.name = st.getName();
      concept.description = st.getAnnotation().getDocumentations().get(0).getContent();
      //concept.range = st.getRestriction().getBase(); // Is restriction range?
      this.concepts.add(concept);
    }
  }

  public void parseComplexTypes(List<XsdComplexType> complexTypes) throws Exception  {
    for (XsdComplexType ct : complexTypes) {
      OntologyConcept concept = new OntologyConcept();
      concept.name = ct.getName();
      concept.description = ct.getAnnotation().getDocumentations().get(0).getContent();
      this.concepts.add(concept);

      if (ct.getElements() != null){
        // Adds elements from sequence
        if (ct.getChildAsSequence() != null){
          List<XsdElement> xsdElementsSequence  = ct.getChildAsSequence().getChildrenElements().collect(Collectors.toList());
          this.parseElements(xsdElementsSequence, concept.name);    
        }
        
        // Adds elements from groups () 
        if (ct.getChildAsGroup() != null){
          List<XsdElement> xsdElementsGroups = ct.getChildAsGroup().getChildElement().getChildrenElements().collect(Collectors.toList());
          this.parseElements(xsdElementsGroups, concept.name);
        }

        // Adds elements from choices
        if (ct.getChildAsChoice() != null){
          List<XsdElement> xsdElementsChoices = ct.getChildAsChoice().getChildrenElements().collect(Collectors.toList());
          this.parseElements(xsdElementsChoices, concept.name);
        }
      }
    }
  }
}
