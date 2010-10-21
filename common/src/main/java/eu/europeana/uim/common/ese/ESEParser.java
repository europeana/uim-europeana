package eu.europeana.uim.common.ese;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.time.DurationFormatUtils;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * XML parser for documents compliant with Europeana Semantic Elements.
 *
 * @author Andreas Juffinger
 */
public class ESEParser {

    private static final Logger log = Logger.getLogger(ESEParser.class.getName());


    public List<HashMap<String, Object>> importXml(InputStream inputStream) throws TransformerException, XMLStreamException, IOException {
    	XMLInputFactory inFactory = new WstxInputFactory();
    	
        XMLStreamReader xml = inFactory.createXMLStreamReader(inputStream);

        int recordCount = 0;
        long startTime = System.currentTimeMillis();

        List<HashMap<String, Object>> recordList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> record = null;


        while (xml.hasNext()) {
            xml.next();

            switch (xml.getEventType()) {
                case XMLStreamConstants.START_DOCUMENT:
                    log.info("Document started");
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    if (isRecordElement(xml)) {
                        record = new HashMap<String, Object>();
                    } else if (record != null) {
                        String local = xml.getLocalName();
                        String prefix = xml.getPrefix();
                        if ("europeana".equals(prefix)) {
                            local = prefix + ":" + local;
                        }

                        if (xml.getAttributeCount() > 0) {
                            for (int i = 0; i < xml.getAttributeCount(); i++) {
                                String l = xml.getAttributeLocalName(i);
                                if ("lang".equals(l)) {
                                    local = local + "@" + xml.getAttributeValue(i);
                                }
                            }
                        }

                        String text = xml.getElementText();
                        record.put(local, text);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (isRecordElement(xml)) {
                        if (record != null) {
                            if (++recordCount % 25 == 0) {
                                log.info(String.format("imported %d records in %s", recordCount, DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startTime)));
                            }

                            recordList.add(record);
                            record = null;
                        }
                    }
                    break;

                case XMLStreamConstants.END_DOCUMENT:
                    log.info(String.format("Document ended, imported %d records", recordCount));
                    break;
            }
        }
        inputStream.close();
        return recordList;
    }

    private boolean isRecordElement(XMLStreamReader xml) {
        return "record".equals(xml.getName().getLocalPart());
    }

}