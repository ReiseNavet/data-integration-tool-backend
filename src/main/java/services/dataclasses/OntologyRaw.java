package services.dataclasses;

public class OntologyRaw {
    public String name; // rdfs:label
    public String[] properties;
    public String description; // rdfs:comment
    public String subClassof; // F.eks. a AboutPage is a part of a WebPage, a brother is a part of a family (Merk: is a part of)
    public String range; // F.eks. a car is a thing, a nose is a thing (Merk: is a)
    public String domain; // F.eks. a truck is a type of car (Merk: is a type of)
    public String inverseOf;
}
