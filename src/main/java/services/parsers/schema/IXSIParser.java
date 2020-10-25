package services.parsers.schema;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import services.dataclasses.OntologyConcept;
import services.interfaces.SchemaParser;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.*;


public class IXSIParser implements SchemaParser {

    @Override
    public List<OntologyConcept> parse(String filePath) throws Exception {
        throw new NotImplementedException();
    }
    
}
