package services.dataclasses;

import java.util.List;

import com.hp.hpl.jena.ontology.DatatypeProperty;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import services.IO.OWLOntologyToFile;

public class OntologyConcept {
    // All fields except "name" are optional. Look at schema-org.owl for examples.
    public String name; // rdfs:label
    public String description; // rdfs:comment
    public String subClassof; // F.eks. a AboutPage is a part of a WebPage, a brother is a part of a family
                              // (Merk: is a part of)
    public String domain; // F.eks. a truck is a type of car (Merk: is a type of)
    public String range; // F.eks. a car is a thing, a nose is a thing (Merk: is a)

    public static void main(String[] args) throws Exception {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = OntologyConcept.toOwlOntology(null, m);
        OWLOntologyToFile.Convert(o, "files/temp/onto", m);
    }

    public static OWLOntology toOwlOntology(List<OntologyConcept> ontologyConcepts, OWLOntologyManager m)
            throws Exception {
        IRI example_iri = IRI.create("http://www.semanticweb.org/ontologies/ont.owl");
        OWLOntology o = m.createOntology(example_iri);
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        OWLClass baby = df.getOWLClass(IRI.create(example_iri + "#Baby"));
        OWLClass child = df.getOWLClass(IRI.create(example_iri + "#Child"));
        OWLClass person = df.getOWLClass(IRI.create(example_iri + "#Person"));
        OWLClass thing = df.getOWLClass(IRI.create(example_iri + "#Thing"));

        OWLAnnotation labelAnno = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral("Baby"));
        OWLAxiom label_ax = df.getOWLAnnotationAssertionAxiom(baby.getIRI(), labelAnno);

        OWLAnnotation commentAnno = df.getOWLAnnotation(df.getRDFSComment(),
                df.getOWLLiteral("A very young child", ""));
        OWLAxiom comment_ax = df.getOWLAnnotationAssertionAxiom(baby.getIRI(), commentAnno);

        OWLSubClassOfAxiom sub_ax = df.getOWLSubClassOfAxiom(baby, child);
        
        //PrefixManager pm= new DefaultPrefixManager("http://www.co-ode.org/ontologies/ont.owl#");
        OWLObjectPropertyExpression man= df.getOWLObjectProperty(baby.getIRI());
        //OWLDatatype dt = df.getOWLDatatype("xsd:string",pm);
        OWLObjectPropertyDomainAxiom domain=df.getOWLObjectPropertyDomainAxiom(man, person);
        //OWLObjectPropertyRangeAxiom range= df.getOWLObjectPropertyRangeAxiom(man, (OWLClassExpression) dt);

        m.applyChange(new AddAxiom(o, label_ax));
        m.applyChange(new AddAxiom(o, comment_ax));
        m.applyChange(new AddAxiom(o, sub_ax));
        m.applyChange(new AddAxiom(o, domain));
        //m.applyChange(new AddAxiom(o, range));
        //m.applyChange(new AddAxiom(o, range_ax));
        return o;
    }
}
