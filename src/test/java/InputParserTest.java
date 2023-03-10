import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import services.InputParser;

public class InputParserTest {
    private InputParser parser = new InputParser();

    @Test
    public void testParse() throws Exception {
        assertNotNull(parser.parseInput("files/test/IXSI.xsd", "files/test/output.owl"));
    }
    
}
