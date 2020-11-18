package services.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import services.HashGenerator;

/**
 * OntologyConcepts are the raw informations we extract with schema parser, which are then each converted to
 * either owl:Class, owl:DatatypeProperty, or owl:ObjectProperty (depending on its fields).
 * We can then create a OWLOntology from a List<OntologyConcepts>.
 */
public class OntologyConcept {
  // All fields except "name" are optional. Look at schema-org.owl for examples.
  public String name = ""; // rdfs:label
  public String description = ""; // rdfs:comment
  public String domain = ""; // Similiar to subClassOf, but for a property not a class, which means it wont be used
                             // for the matching.
  public String range = ""; // F.eks. address is a string, phone_num is a integer (Merk: is a)

  // Ask if you need to use this.
  public String subClassof = ""; // F.eks. a brother is a part of a family (Merk: is a part of)

  public static File toOWLFile(List<OntologyConcept> ontologyConcepts, String filepathToStore)
      throws Exception {
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLOntology o = toOWLOntology(ontologyConcepts, m);
    FileUtil.createDirectory(filepathToStore);
    File file = OWLOntologyToFile.convert(o, filepathToStore, m);
    FixOntology(filepathToStore);
    return file;
  }

  private static OWLOntology toOWLOntology(List<OntologyConcept> ontologyConcepts, OWLOntologyManager ontologyManager) throws Exception {
    IRI base_iri = IRI.create("http://www.semanticweb.org/ontologies/ont" + HashGenerator.generateHash() +".owl");
    OWLOntology ontology = ontologyManager.createOntology(base_iri);
    OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();

    Map<String, OWLClass> nameToOWLClass = new HashMap<String, OWLClass>();
    Map<String, Set<String>> domains = new HashMap<String, Set<String>>();
    Map<String, Set<String>> ranges = new HashMap<String, Set<String>>();
    List<String> objectProperties = new ArrayList<String>();

    //Create OWLClasses
    for (OntologyConcept concept : ontologyConcepts) {

      OWLClass owlclass = dataFactory.getOWLClass(IRI.create(base_iri + "#" + concept.name));

      AddLabel(concept.name, owlclass, dataFactory, ontologyManager, ontology);
      //Add description
      if (!concept.description.equals("")){
        if (!nameToOWLClass.containsKey(concept.name)) { // It only uses one of the comments if there are duplicate entries
          AddDescription(concept.description, owlclass, dataFactory, ontologyManager, ontology);
        }
      }
      //Add range
      if (!concept.range.equals("")) {
        if (!ranges.containsKey(concept.name)){
          ranges.put(concept.name, new HashSet<String>());
        }
        ranges.get(concept.name).add(concept.range);
      }
      //Add domains to map
      if (!concept.domain.equals("")){
        if (!domains.containsKey(concept.name)){
          domains.put(concept.name, new HashSet<String>());
        }
        domains.get(concept.name).add(concept.domain);
      }

      nameToOWLClass.put(concept.name, owlclass);
    }
    //Add ranges from map
    for (String key : ranges.keySet()){
      OWLClass owlclass = nameToOWLClass.get(key);
      //If all ranges references to classes, then its a objectProperty
      if (ranges.get(key).stream().allMatch(a -> nameToOWLClass.containsKey(a))){
        objectProperties.add(key);
      }
      if (objectProperties.contains(key)){
        Set<OWLClass> rangeclasses = ranges.get(key).stream().map(a -> nameToOWLClass.get(a)).collect(Collectors.toSet());
        AddRangeObject(owlclass, rangeclasses, dataFactory, ontologyManager, ontology);
      } else {
        //Can't use unionOf with data type ranges. So there better be no duplicate entries
        String rangedata = ranges.get(key).iterator().next();
        AddRangeData(owlclass, rangedata, dataFactory, ontologyManager, ontology);
      }
    }
    //Add domains from map
    for (String key : domains.keySet()){
      OWLClass owlclass = nameToOWLClass.get(key);
      Set<OWLClass> domainclasses = domains.get(key).stream().map(a -> nameToOWLClass.get(a)).collect(Collectors.toSet());
      if (objectProperties.contains(key)){
        AddDomainObject(owlclass, domainclasses, dataFactory, ontologyManager, ontology);
      } else {
        AddDomainData(owlclass, domainclasses, dataFactory, ontologyManager, ontology);
      }
    }
    // Unused for now: Add subClassOf
    for (OntologyConcept concept : ontologyConcepts) {
      if (concept.subClassof.equals("")) {
        continue;
      }
      OWLClass owlclass = nameToOWLClass.get(concept.name);
      OWLClass superclass = nameToOWLClass.get(concept.subClassof);
      AddSubClassOf(owlclass, superclass, dataFactory, ontologyManager, ontology);
    }
    return ontology;
  }

  private static void AddLabel(String name, OWLClass owlclass, OWLDataFactory df, OWLOntologyManager m, OWLOntology o) {
    OWLAnnotation labelAnno = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(name));
    OWLAxiom label_ax = df.getOWLAnnotationAssertionAxiom(owlclass.getIRI(), labelAnno);
    m.addAxiom(o, label_ax);
  }

  private static void AddDescription(String description, OWLClass owlclass, OWLDataFactory df, OWLOntologyManager m,
      OWLOntology o) {
    OWLAnnotation commentAnno = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral(description, ""));
    OWLAxiom comment_ax = df.getOWLAnnotationAssertionAxiom(owlclass.getIRI(), commentAnno);
    m.addAxiom(o, comment_ax);
  }

  private static void AddDomainData(OWLClass owlclass, Set<OWLClass> domainclasses, OWLDataFactory df, OWLOntologyManager m, OWLOntology o) {
    OWLDataPropertyExpression c_as_property = df.getOWLDataProperty(owlclass.getIRI());
    OWLDataPropertyDomainAxiom domain_ax = null;
    if (domainclasses.size() == 1){
      domain_ax = df.getOWLDataPropertyDomainAxiom(c_as_property, domainclasses.iterator().next());
    } else {
      domain_ax = df.getOWLDataPropertyDomainAxiom(c_as_property, df.getOWLObjectUnionOf(domainclasses));
    }
    m.addAxiom(o, domain_ax);
  }
  private static void AddDomainObject(OWLClass owlclass, Set<OWLClass> domainclasses, OWLDataFactory df, OWLOntologyManager m, OWLOntology o) {
    OWLObjectPropertyExpression c_as_property = df.getOWLObjectProperty(owlclass.getIRI());
    OWLObjectPropertyDomainAxiom domain_ax = null;
    if (domainclasses.size() == 1){
      domain_ax = df.getOWLObjectPropertyDomainAxiom(c_as_property, domainclasses.iterator().next());
    } else {
      domain_ax = df.getOWLObjectPropertyDomainAxiom(c_as_property, df.getOWLObjectUnionOf(domainclasses));
    }
    m.addAxiom(o, domain_ax);
  }

  private static void AddRangeData(OWLClass owlclass, String rangedata, OWLDataFactory df, OWLOntologyManager m, OWLOntology o) {
    OWLDataProperty c_as_property  = df.getOWLDataProperty(owlclass.getIRI());
    PrefixManager pm = new DefaultPrefixManager(
                "http://www.w3.org/2001/XMLSchema#");
    OWLDataPropertyRangeAxiom range_ax = df.getOWLDataPropertyRangeAxiom(c_as_property, df.getOWLDatatype(rangedata, pm));
    m.addAxiom(o, range_ax);
  }

  private static void AddRangeObject(OWLClass owlclass, Set<OWLClass> rangeclasses, OWLDataFactory df, OWLOntologyManager m, OWLOntology o) {
    OWLObjectProperty c_as_property  = df.getOWLObjectProperty(owlclass.getIRI());
    OWLObjectPropertyRangeAxiom range_ax = null;
    if (rangeclasses.size() == 1){
      range_ax = df.getOWLObjectPropertyRangeAxiom(c_as_property, rangeclasses.iterator().next());
    } else {
      range_ax = df.getOWLObjectPropertyRangeAxiom(c_as_property, df.getOWLObjectUnionOf(rangeclasses));
    }
    m.addAxiom(o, range_ax);
  }

  private static void AddSubClassOf(OWLClass owlclass, OWLClass superclass, OWLDataFactory df, OWLOntologyManager m, OWLOntology o){
    if (superclass == null){
      System.out.println("Warning: SubClassOf could not find superclass");
      return;
    }
    OWLSubClassOfAxiom sub_ax = df.getOWLSubClassOfAxiom(owlclass, superclass);
    m.addAxiom(o, sub_ax);
  }


  /**
   * Replace all rdf:Description with owl:Class outside of unionOf braces
   * (Since OWLClasses gets turned into Descriptions for some reason)
   */
  private static void FixOntology(String filepathToStore) throws Exception {
    String text = FileUtil.readFile(filepathToStore);
    String[] textSplit = text.split("owl:unionOf");
    for (int n = 0; n < textSplit.length; n += 2){
      textSplit[n] = textSplit[n].replace("rdf:Description", "owl:Class");
    }
    FileUtil.writeFile(filepathToStore, String.join("owl:unionOf", textSplit));
  }
}
