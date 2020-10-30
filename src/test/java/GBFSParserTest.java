import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import services.parsers.schema.GBFSParser;
import services.utils.Unzipper;

public class GBFSParserTest {
    private GBFSParser parser = new GBFSParser();

    @Test
    public void testParse() throws Exception {
        assertNotNull(parser.parse("files/test/GBFS"));
    }
    
}
