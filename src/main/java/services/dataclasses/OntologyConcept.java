package services.dataclasses;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.semanticweb.owlapi.model.OWLOntology;

public class OntologyConcept {
    // All fields except "name" are optional
    public String name; // rdfs:label
    public String[] properties;
    public String description; // rdfs:comment
    public String subClassof; // F.eks. a AboutPage is a part of a WebPage, a brother is a part of a family (Merk: is a part of)
    public String range; // F.eks. a car is a thing, a nose is a thing (Merk: is a)
    public String domain; // F.eks. a truck is a type of car (Merk: is a type of)
    public String inverseOf;

    public static OWLOntology toOwlOntology(List<OntologyConcept> ontologyConcepts){
        throw new NotImplementedException();
    }
}
