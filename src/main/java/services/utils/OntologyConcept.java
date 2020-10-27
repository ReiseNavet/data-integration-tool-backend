package services.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import kotlin.NotImplementedError;
import services.HashGenerator;

public class OntologyConcept {
  // All fields except "name" are optional. Look at schema-org.owl for examples.
  public String name = ""; // rdfs:label
  public String description = ""; // rdfs:comment
  public String domain = ""; // Same as subClassOf, but for a header not a class, which means it wont be used
                             // for the matching.

  // Ask if you need to use these.
  public String subClassof = ""; // F.eks. a brother is a part of a family (Merk: is a part of)
  public String range = ""; // F.eks. a brother is a person (Merk: is a)

  public static File toOWLFile(List<OntologyConcept> ontologyConcepts, String filepathToStore)
      throws Exception {
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLOntology o = toOWLOntology(ontologyConcepts, m);
    //OWLOntology o = OntologyConcept.test(m);
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

    //Create OWLClasses
    for (OntologyConcept ontologyConcept : ontologyConcepts) {

      OWLClass owlClass = dataFactory.getOWLClass(IRI.create(base_iri + "#" + ontologyConcept.name));

      AddLabel(ontologyConcept.name, owlClass, dataFactory, ontologyManager, ontology);
      //Add description
      if (!ontologyConcept.description.equals("")){
        if (!nameToOWLClass.containsKey(ontologyConcept.name)) { // It only uses one of the comments if there are duplicate entries
          AddDescription(ontologyConcept.description, owlClass, dataFactory, ontologyManager, ontology);
        }
      }
      //Add domains to map
      if (!ontologyConcept.domain.equals("")){
        if (!domains.containsKey(ontologyConcept.name)){
          domains.put(ontologyConcept.name, new HashSet<String>());
        }
        domains.get(ontologyConcept.name).add(ontologyConcept.domain);
      }
      //Add range
      if (!ontologyConcept.range.equals("")) {
        throw new NotImplementedError("range are currently not supported");
      }

      nameToOWLClass.put(ontologyConcept.name, owlClass);
    }
    // Add domains from map
    for (String key : domains.keySet()){
      OWLClass owlClass = nameToOWLClass.get(key);
      Set<OWLClass> owlClassSet = domains.get(key).stream().map(str -> nameToOWLClass.get(str)).collect(Collectors.toSet());
      AddDomain(owlClass, owlClassSet, dataFactory, ontologyManager, ontology);
    }
    // Add subClassOf (duplicate subclass entries are not supported as of now)
    for (OntologyConcept concept : ontologyConcepts) {
      if (concept.subClassof.equals("")) {
        continue;
      }
      OWLClass owlClass = nameToOWLClass.get(concept.name);
      OWLClass owlClass2 = nameToOWLClass.get(concept.subClassof);
      AddSubClassOf(owlClass, owlClass2, dataFactory, ontologyManager, ontology);
    }
    return ontology;
  }

  private static void AddLabel(String name, OWLClass owlClass, OWLDataFactory dataFactory, OWLOntologyManager ontologyManager, OWLOntology ontology) {
    OWLAnnotation labelAnno = dataFactory.getOWLAnnotation(dataFactory.getRDFSLabel(), dataFactory.getOWLLiteral(name));
    OWLAxiom label_ax = dataFactory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), labelAnno);
    ontologyManager.addAxiom(ontology, label_ax);
  }

  private static void AddDescription(String description, OWLClass owlClass, OWLDataFactory dataFactory, OWLOntologyManager ontologyManager, OWLOntology ontology) {
    OWLAnnotation commentAnno = dataFactory.getOWLAnnotation(dataFactory.getRDFSComment(), dataFactory.getOWLLiteral(description, ""));
    OWLAxiom comment_ax = dataFactory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), commentAnno);
    ontologyManager.addAxiom(ontology, comment_ax);
  }

  private static void AddDomain(OWLClass c, Set<OWLClass> c2, OWLDataFactory df, OWLOntologyManager m, OWLOntology o) {
    OWLDataPropertyExpression c_as_property = df.getOWLDataProperty(c.getIRI());
    OWLDataPropertyDomainAxiom domain_ax = null;
    if (c2.size() == 1){
      domain_ax = df.getOWLDataPropertyDomainAxiom(c_as_property, c2.iterator().next());
    } else {
      domain_ax = df.getOWLDataPropertyDomainAxiom(c_as_property, df.getOWLObjectUnionOf(c2));
    }
    m.addAxiom(o, domain_ax);
  }

  private static void AddSubClassOf(OWLClass childClass, OWLClass parentClass, OWLDataFactory dataFactory, OWLOntologyManager ontologyManager, OWLOntology ontology){
    if (parentClass == null){
      System.out.println("Warning: SubClassOf could not find parent class");
      return;
    }
    OWLSubClassOfAxiom sub_axiom = dataFactory.getOWLSubClassOfAxiom(childClass, parentClass);
    ontologyManager.addAxiom(ontology, sub_axiom);
  }


  /**
   * OWLClasses got turned into Descriptions for some reason, so I need to replace it in the end
   */
  private static void FixOntology(String filepathToStore) throws Exception {
    String text = Files.readString(Paths.get(filepathToStore));
    String[] textSplit = text.split("owl:unionOf");
    for (int n = 0; n < textSplit.length; n += 2){
      textSplit[n] = textSplit[n].replace("rdf:Description", "owl:Class");
    }
    Files.writeString(Paths.get(filepathToStore), String.join("owl:unionOf", textSplit));
  }

}
