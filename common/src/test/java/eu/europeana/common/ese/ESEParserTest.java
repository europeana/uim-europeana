package eu.europeana.common.ese;

import eu.europeana.uim.common.ese.ESEParser;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ESEParserTest {

	
	@Test
	public void testParseReadingEurope() throws TransformerException, XMLStreamException, IOException {
		InputStream stream = ESEParserTest.class.getResourceAsStream("/readingeurope.xml");
		ESEParser parser = new ESEParser();
		List<HashMap<String,Object>> xml = parser.importXml(stream);
		assertEquals(xml.size(), 999);
	}
}
