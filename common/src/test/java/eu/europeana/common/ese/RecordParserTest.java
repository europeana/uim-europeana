package eu.europeana.common.ese;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import eu.europeana.uim.common.parse.RecordField;
import eu.europeana.uim.common.parse.RecordMap;
import eu.europeana.uim.common.parse.RecordParser;
import eu.europeana.uim.common.parse.XMLStreamParserException;


public class RecordParserTest {


	@Test
	public void testParseReadingEurope() throws XMLStreamParserException {
		InputStream stream = RecordParserTest.class.getResourceAsStream("/readingeurope.xml");
		RecordParser parser = new RecordParser();
		List<RecordMap> xml = parser.parse(stream, "europeana:record");
		assertEquals(999, xml.size());
	}

	
	@Test
	public void testParseReadingEuropeOAI() throws XMLStreamParserException {
		InputStream stream = RecordParserTest.class.getResourceAsStream("/readingeurope.oai.xml");
		RecordParser parser = new RecordParser();
		List<RecordMap> xml = parser.parse(stream, "europeana:record");
		assertEquals(250, xml.size());
	}

	
	@Test
	public void testParseReadingEuropePathESE() throws XMLStreamParserException {
		InputStream stream = RecordParserTest.class.getResourceAsStream("/readingeurope.oai.xml");
		RecordParser parser = new RecordParser();
		List<RecordMap> xml = parser.parse(stream, "OAI-PMH|ListRecords|record|metadata|europeana:record");
		assertEquals(250, xml.size());
	}

	@Test
	public void testParseReadingEuropeTEL() throws XMLStreamParserException {
		InputStream stream = RecordParserTest.class.getResourceAsStream("/readingeurope.tel.xml");
		RecordParser parser = new RecordParser();
		List<RecordMap> xml = parser.parse(stream, "tel:dcx");
		assertEquals(250, xml.size());
	}

	
	@Test
	public void testParseReadingEuropePathTEL() throws XMLStreamParserException {
		InputStream stream = RecordParserTest.class.getResourceAsStream("/readingeurope.tel.xml");
		RecordParser parser = new RecordParser();
		List<RecordMap> xml = parser.parse(stream, "OAI-PMH|ListRecords|record|metadata|record|tel:dcx");
		assertEquals(250, xml.size());
	}

	

	
	@Test
	public void testParseReadingEuropeDetail() throws XMLStreamParserException {
		InputStream stream = RecordParserTest.class.getResourceAsStream("/readingeurope.oai.xml");
		RecordParser parser = new RecordParser();
		List<RecordMap> xml = parser.parse(stream, "europeana:record");
		for (RecordMap record : xml) {
			String title = (String) record.get(new RecordField("dc", "title", "eng"));
			if (title != null) {
				List<String> local = record.getValueByLocal("title");
				boolean matched = false;
				for (String ltitle : local) {
					if (ltitle.equals(title)) {
						matched = true;
					}
				}
				assertTrue(matched);
			}
		}
	}
}
