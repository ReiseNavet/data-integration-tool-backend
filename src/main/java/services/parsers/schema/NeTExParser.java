package services.parsers.schema;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import services.dataclasses.OntologyConcept;
import services.interfaces.SchemaParser;

public class NeTExParser implements SchemaParser {

    @Override
    public List<OntologyConcept> parse(String filePath) throws Exception {
        throw new NotImplementedException();
    }
    
}
