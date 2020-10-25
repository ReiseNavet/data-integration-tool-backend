package services.parsers.schema;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import services.dataclasses.OntologyConcept;
import services.interfaces.SchemaParser;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IXSIParser implements SchemaParser {
    private List<OntologyConcept> concepts = new ArrayList<>();
    private XsdParser parserInstance;

    public IXSIParser(String filePath){
        this.parserInstance = new XsdParser(filePath);
    }

    @Override
    public List<OntologyConcept> parse(String filePath) throws Exception {
        //Stream<XsdElement> elementsStream = parserInstance.getResultXsdElements();
        Stream<XsdSchema> schemasStream = parserInstance.getResultXsdSchemas();
        schemasStream.forEach(this::parseSchema);

        // debug
        System.out.println("IXSIParser count: " + concepts.stream().count());

        return this.concepts;
        
    }

    public void parseSchema(XsdSchema schema) {
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
            concept.description = elem.getAnnotation().getDocumentations().get(0).getContent();
            concept.domain = elem.getType();
            concept.subClassof = parent;
            this.concepts.add(concept);
        }
    }

    // A simple element can only contain text
    public void parseSimpleTypes(List<XsdSimpleType> simpleTypes) {
        for (XsdSimpleType st: simpleTypes) {
            OntologyConcept concept = new OntologyConcept();
            concept.name = st.getName();
            concept.description = st.getAnnotation().getDocumentations().get(0).getContent();
            concept.range = st.getRestriction().getBase(); // Is restriction range?
            this.concepts.add(concept);
        }
    }

    public void parseComplexTypes(List<XsdComplexType> complexTypes) {
        for (XsdComplexType ct : complexTypes) {
            OntologyConcept concept = new OntologyConcept();
            concept.name = ct.getName();
            concept.description = ct.getAnnotation().getDocumentations().get(0).getContent();
            this.concepts.add(concept);

            // Adds elements from sequence

            try {
                List<XsdElement> xsdElements  = ct.getChildAsSequence().getChildrenElements().collect(Collectors.toList());
                this.parseElements(xsdElements, concept.name);
            } catch (Exception e) {

            }

            // Adds elements from groups ()

            try {
                List<XsdElement> xsdElements = ct.getChildAsGroup().getChildElement().getChildrenElements().collect(Collectors.toList());
                this.parseElements(xsdElements, concept.name);
            } catch (Exception e) {

            }

            // Adds elements from choices

            try {
                List<XsdElement> xsdElements = ct.getChildAsChoice().getChildrenElements().collect(Collectors.toList());
                this.parseElements(xsdElements, concept.name);
            } catch (Exception e) {

            }
        }
    }
}
