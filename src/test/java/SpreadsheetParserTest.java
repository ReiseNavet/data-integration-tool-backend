import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import services.parsers.schema.SpreadsheetParser;

public class SpreadsheetParserTest {
    private SpreadsheetParser parser = new SpreadsheetParser();

    @Test
    public void testParse() throws Exception {
        assertNotNull(parser.parse("files/test/GTFS-Flex.xlsx"));
    }
    
}
