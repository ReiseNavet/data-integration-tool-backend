import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import services.parsers.schema.IXSIParser;

public class IXSIParserTest {
    private IXSIParser parser = new IXSIParser();

    @Test
    public void testParse() throws Exception {
        assertNotNull(parser.parse("files/test/IXSI.xsd"));
    }
    
}
