package services.dataclasses;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitor;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import kotlin.NotImplementedError;
import services.IO.OWLOntologyToFile;
import services.parsers.schema.SpreadsheetParser;

public class OntologyConcept {
    // All fields except "name" are optional. Look at schema-org.owl for examples.
    public String name = ""; // rdfs:label
    public String description = ""; // rdfs:comment
    public String domain = ""; // Same as subClassOf, but for a header not a class, which means it wont be used
                               // for the matching.

    // Ask if you need to use these.
    public String subClassof = ""; // F.eks. a brother is a part of a family (Merk: is a part of)
    public String range = ""; // F.eks. a brother is a person (Merk: is a)

    public static void main(String[] args) throws Exception {
        List<OntologyConcept> concepts = new SpreadsheetParser().parse("files/temp/GTFS-Flex.xlsx");
        String filepath = "files/temp/onto";
        OntologyConcept.toOWLFile(concepts, filepath);
    }

    public static File toOWLFile(List<OntologyConcept> ontologyConcepts, String filepathToStore)
            throws Exception {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = toOWLOntology(ontologyConcepts, m);
        //OWLOntology o = OntologyConcept.test(m);
        File file = OWLOntologyToFile.Convert(o, filepathToStore, m);
        FixOntology(filepathToStore);
        return file;
    }

    private static OWLOntology toOWLOntology(List<OntologyConcept> ontologyConcepts, OWLOntologyManager m)
            throws Exception {
        IRI base_iri = IRI.create("http://www.semanticweb.org/ontologies/ont.owl");
        OWLOntology o = m.createOntology(base_iri);
        OWLDataFactory df = OWLManager.getOWLDataFactory();

        Map<String, OWLClass> nameToOWLClass = new HashMap<String, OWLClass>();
        Map<String, Set<String>> domains = new HashMap<String, Set<String>>();

        //Create OWLClasses
        for (OntologyConcept concept : ontologyConcepts) {

            OWLClass c = df.getOWLClass(IRI.create(base_iri + "#" + concept.name));

            AddLabel(concept.name, c, df, m, o);
            //Add description
            if (!concept.description.equals("")){
                if (!nameToOWLClass.containsKey(concept.name)) { // It only uses one of the comments if there are duplicate entries
                    AddDescription(concept.description, c, df, m, o);
                }
            }
            //Add domains to map
            if (!concept.domain.equals("")){
                if (!domains.containsKey(concept.name)){
                    domains.put(concept.name, new HashSet<String>());
                }
                domains.get(concept.name).add(concept.domain);
            }
            //Add range
            if (!concept.range.equals("")) {
                throw new NotImplementedError("range are currently not supported");
            }

            nameToOWLClass.put(concept.name, c);
        }
        // Add domains from map
        for (String key : domains.keySet()){
            OWLClass c = nameToOWLClass.get(key);
            Set<OWLClass> c2 = domains.get(key).stream().map(a -> nameToOWLClass.get(a)).collect(Collectors.toSet());
            AddDomain(c, c2, df, m, o);
        }
        // Add subClassOf (duplicate subclass entries are not supported as of now)
        for (OntologyConcept concept : ontologyConcepts) {
            if (concept.subClassof.equals("")) {
                continue;
            }
            OWLClass c = nameToOWLClass.get(concept.name);
            OWLClass c2 = nameToOWLClass.get(concept.subClassof);
            AddSubClassOf(c, c2, df, m, o);
        }
        return o;
    }

    private static void AddLabel(String name, OWLClass c, OWLDataFactory df, OWLOntologyManager m, OWLOntology o) {
        OWLAnnotation labelAnno = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(name));
        OWLAxiom label_ax = df.getOWLAnnotationAssertionAxiom(c.getIRI(), labelAnno);
        m.addAxiom(o, label_ax);
    }

    private static void AddDescription(String description, OWLClass c, OWLDataFactory df, OWLOntologyManager m,
            OWLOntology o) {
        OWLAnnotation commentAnno = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral(description, ""));
        OWLAxiom comment_ax = df.getOWLAnnotationAssertionAxiom(c.getIRI(), commentAnno);
        m.addAxiom(o, comment_ax);
    }

    private static void AddDomain(OWLClass c, Set<OWLClass> c2, OWLDataFactory df, OWLOntologyManager m, OWLOntology o) {
        OWLObjectPropertyExpression c_as_property = df.getOWLObjectProperty(c.getIRI());
        OWLObjectPropertyDomainAxiom domain_ax = null;
        if (c2.size() == 1){
            domain_ax = df.getOWLObjectPropertyDomainAxiom(c_as_property, c2.iterator().next());
        } else {
            domain_ax = df.getOWLObjectPropertyDomainAxiom(c_as_property, df.getOWLObjectUnionOf(c2));
        }
        m.addAxiom(o, domain_ax);
    }

    private static void AddSubClassOf(OWLClass child, OWLClass parent, OWLDataFactory df, OWLOntologyManager m, OWLOntology o){
        if (parent == null){
            System.out.println("Warning: SubClassOf could not find parent class");
            return;
        }
        OWLSubClassOfAxiom sub_ax = df.getOWLSubClassOfAxiom(child, parent);
        m.addAxiom(o, sub_ax);
    }


    /**
     * OWLClasses got turned into Descriptions for some reason, so I need to replace it in the end
     */
    private static void FixOntology(String filepathToStore) throws Exception {
        String text = Files.readString(Paths.get(filepathToStore));
        text = text.replace("rdf:Description", "owl:Class");
        Files.writeString(Paths.get(filepathToStore), text);
    }


    /**
     * Test function for implementing
     */
    private static OWLOntology test(OWLOntologyManager m) throws Exception{
        IRI example_iri = IRI.create("http://www.semanticweb.org/ontologies/ont.owl");
        OWLOntology o = m.createOntology(example_iri);
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        OWLClass baby = df.getOWLClass(IRI.create(example_iri + "#Baby"));
        IRI personIRI = IRI.create(example_iri + "#Person");
        OWLClass person = df.getOWLClass(personIRI);
        OWLClass child = df.getOWLClass(IRI.create(example_iri + "#Child"));

        OWLClassExpression firstRuleSet = df.getOWLObjectUnionOf(person, child);
        OWLObjectPropertyExpression c_as_property = df.getOWLObjectProperty(baby.getIRI());
        m.addAxiom(o, df.getOWLObjectPropertyDomainAxiom(c_as_property, firstRuleSet));

        //OWLAnnotation labelAnno = df.getOWLAnnotationProperty(df.getRDFSLabel(), df.getOWLLiteral("Person"));
        OWLAnnotation labelAnno = df.getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create(example_iri + "#rdfs:label")), df.getOWLLiteral("Person"));
        OWLAnnotationAssertionAxiom label_ax = df.getOWLAnnotationAssertionAxiom(personIRI, labelAnno);
        m.addAxiom(o, label_ax);
        return o;
    }
}
