
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import services.parsers.schema.NeTExParser;
import services.utils.Unzipper;

public class NeTExParserTest {
    private NeTExParser parser = new NeTExParser();

    @Test
    public void testParse() throws Exception {
        assertNotNull(new NeTExParser().parse(Unzipper.unzip("files/parse_files/NeTEx_xsd.zip", "temp/parse_files/xsd")));
    }
    
}
