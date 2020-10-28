package services.interfaces;

import java.util.List;

import services.utils.OntologyConcept;

public interface SchemaParser {
  public List<OntologyConcept> parse(String filePath) throws Exception;
}
