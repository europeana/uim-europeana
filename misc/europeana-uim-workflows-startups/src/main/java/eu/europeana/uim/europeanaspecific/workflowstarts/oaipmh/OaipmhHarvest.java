package eu.europeana.uim.europeanaspecific.workflowstarts.oaipmh;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

import javax.xml.transform.TransformerException;


import ORG.oclc.oai.harvester2.verb.ListRecords;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import eu.europeana.uim.api.LoggingEngine;

/**
 * Provides an Iterator interface to an OAI-PMH harvest
 * 
 */
public class OaipmhHarvest implements Iterator<OaiPmhRecord> {

    private final String              baseURL;
    private final String              metadataPrefix;
    private final String              setSpec;
    private final String              from;
    private final String              until;

    private int                       maxRecordsToRetrieve  = -1;

    private String                    nextResumptionToken   = null;
    private int                       totalRetrievedRecords = 0;

    private LoggingEngine<?>          loggingEngine;

    /**
     * The total records as reported by the OAI-PMH server on the first ListRecords response
     */
    private int                       completeListSize      = -1;

    private final Queue<OaiPmhRecord> records               = new LinkedList<OaiPmhRecord>();

    /**
     * Creates a new instance of this class.
     * 
     * @param baseURL
     *            Oai server base url
     * @param metadataPrefix
     *            oai metadata prefix to harvest
     * @param setSpec
     *            oai set to harvest
     * @param loggingEngine
     * @throws HarvestException
     */
    public OaipmhHarvest(String baseURL, String metadataPrefix, String setSpec,
                         LoggingEngine<?> loggingEngine) throws HarvestException {
        this(baseURL, null, null, metadataPrefix, setSpec, loggingEngine);
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param baseURL
     *            Oai server base url
     * @param from
     * @param until
     * @param metadataPrefix
     *            oai metadata prefix to harvest
     * @param setSpec
     *            oai set to harvest
     * @param loggingEngine
     * @throws HarvestException
     */
    public OaipmhHarvest(String baseURL, String from, String until, String metadataPrefix,
                         String setSpec, LoggingEngine<?> loggingEngine) throws HarvestException {
        super();
        this.baseURL = baseURL;
        this.from = from;
        this.until = until;
        this.metadataPrefix = metadataPrefix;
        this.setSpec = setSpec;
        this.loggingEngine = loggingEngine;

        fetchFirstRecords();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param baseURL
     *            Oai server base url
     * @param resumptionToken
     * @param loggingEngine
     */
    public OaipmhHarvest(String baseURL, String resumptionToken, LoggingEngine<?> loggingEngine) {
        super();
        this.baseURL = baseURL;
        this.setSpec = null;
        this.metadataPrefix = null;
        this.from = null;
        this.until = null;
        this.loggingEngine = loggingEngine;

        this.nextResumptionToken = resumptionToken;
        try {
            fetchNextIfEmpty();
        } catch (HarvestException e) {
            throw new RuntimeException("Could not get the first records!", e);
        }
    }

    @Override
    public boolean hasNext() {
        if (maxRecordsToRetrieve > 0 && totalRetrievedRecords >= maxRecordsToRetrieve)
            return false;

        try {
            fetchNextIfEmpty();
            return !records.isEmpty();
        } catch (HarvestException e) {
            throw new RuntimeException("Could not get the next records!", e);
        }
    }

    @Override
    public OaiPmhRecord next() {
        try {
            fetchNextIfEmpty();
            totalRetrievedRecords++;
            return records.poll();
        } catch (HarvestException e) {
            throw new RuntimeException("Could not get the next records!", e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Sorry, not implemented.");
    }

    private synchronized void fetchFirstRecords() throws HarvestException {
        try {
            long start = System.currentTimeMillis();
            ListRecords listRecords = new ListRecords(baseURL, from, until, setSpec, metadataPrefix);
            processListRecordsResponse(listRecords);

            if (loggingEngine != null)
                loggingEngine.log(Level.INFO, "OaipmhHarvest", String.format(
                        "Load:" + listRecords.getRequestURL() + " in %.3f sec.",
                        (System.currentTimeMillis() - start) / 1000.0));

        } catch (Throwable e) {
            throw new HarvestException(
                    getRequestURL(baseURL, from, until, setSpec, metadataPrefix), e);
        }
    }

    private synchronized void fetchNextIfEmpty() throws HarvestException {
        try {
            if (records.isEmpty()) {
                if (nextResumptionToken == null || "".equals(nextResumptionToken)) { return; }
                processListRecordsResponse(resumeListRecordsWithRetry());
            }
        } catch (HarvestException e) {
            throw e;
        } catch (Throwable e) {
            throw new HarvestException(getRequestURL(baseURL, nextResumptionToken), e);
        }
    }

    private void processListRecordsResponse(ListRecords listRecords) throws HarvestException,
            TransformerException, NoSuchFieldException {
        NodeList errors = listRecords.getErrors();
        if (errors != null && errors.getLength() > 0) {
            int length = errors.getLength();
            for (int i = 0; i < length; ++i) {
                Node item = errors.item(i);
                Node code = item.getAttributes().getNamedItem("code");
                if (code != null && "noRecordsMatch".equals(code.getNodeValue())) {
                    // only in this case we do not treat it as an error.
                } else {
                    throw new HarvestException(listRecords.getRequestURL() + " returned error:" +
                                               item.getTextContent());
                }
            }
        } else {
            String xpath = "//ListRecords/record";
            String uri0 = listRecords.getDocument().getDocumentElement().getNamespaceURI();
            if ("http://www.openarchives.org/OAI/2.0/".equals(uri0)) {
                xpath = "//oai20:ListRecords/oai20:record";
            }

            NodeList nodeList = listRecords.getNodeList(xpath);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                OaiPmhRecord record = new OaiPmhRecord();

                NodeList nodes = node.getChildNodes();
                for (int j = 0; j < nodes.getLength(); j++) {
                    Node candidate = nodes.item(j);
                    if ("header".equals(candidate.getLocalName())) {
                        record.setIdentifier(candidate.getFirstChild().getTextContent());
                        Node statusNode = candidate.getAttributes().getNamedItem("status");
                        if (statusNode != null) {
                            record.setDeleted(statusNode.getTextContent().equals("deleted"));
                        } else {
                            record.setDeleted(false);
                        }
                    } else if ("metadata".equals(candidate.getLocalName())) {
                        NodeList childNodes = candidate.getChildNodes();
                        for (int idx = 0; idx < childNodes.getLength(); idx++) {
                            Node childNode = childNodes.item(idx);
                            if (childNode instanceof Element) {
                                record.setMetadata((Element)childNode);
                                break;
                            }
                        }
                    } else if ("about".equals(candidate.getLocalName())) {
                        record.setProvenance((Element)candidate.getFirstChild());

                    }
                }
                records.offer(record);
            }

            nextResumptionToken = listRecords.getResumptionToken();

            if (this.completeListSize == -1) {
                String listSizeXpath = "/oai20:OAI-PMH/oai20:ListRecords/oai20:resumptionToken/@completeListSize";
                if ("http://www.openarchives.org/OAI/2.0/".equals(uri0))
                    listSizeXpath = "//oai20:ListRecords/oai20:resumptionToken/@completeListSize";
                String sizeStr = listRecords.getSingleString(listSizeXpath);
                if (sizeStr != null && !sizeStr.isEmpty()) try {
                    completeListSize = Integer.parseInt(sizeStr);
                } catch (NumberFormatException e) {
                    // just ignore
                }
            }

        }
    }

    /**
     * Executes a list records request and in case of failure, it waits for some seconds and tries
     * again.
     * 
     * @return a ListRecords
     * @throws Exception
     *             if the maximum number of retries was reached
     */
    protected ListRecords resumeListRecordsWithRetry() throws Exception {
        int tries = 0;
        while (true) {
            tries++;
            try {
                long start = System.currentTimeMillis();
                ListRecords listRecords = new ListRecords(baseURL, nextResumptionToken);
                if (loggingEngine != null)
                    loggingEngine.log(Level.INFO, "OaipmhHarvest", 
                            "Load:" + listRecords.getRequestURL() + String.format(" in %.3f sec.",
                            (System.currentTimeMillis() - start) / 1000.0));

                return listRecords;

            } catch (SAXParseException e) {
                // cannot continue, because we can't find
                // the next token => next token is null
                nextResumptionToken = null;
                throw e;
            } catch (TransformerException e) {
                // cannot continue, because we can't find
                // the next token => next token is null

                nextResumptionToken = null;
                throw e;

            } catch (IOException e) {
                if (tries > 3) throw e;
                try {
                    long time = 15000 * (tries * tries * tries);
                    if (loggingEngine != null)
                        loggingEngine.logFailed(Level.INFO, "OaipmhHarvest", null,
                                "Failed <" + getRequestURL(baseURL, nextResumptionToken) +
                                        "> going to retry in: " + time / 1000 + " sec.");

                    Thread.sleep(time);
                } catch (InterruptedException e1) {
                    // ignore
                }
            }
        }
    }

    /**
     * @param maxRecordsToRetrieve
     */
    public void setMaxRecordsToHarvest(int maxRecordsToRetrieve) {
        this.maxRecordsToRetrieve = maxRecordsToRetrieve;
    }

    /**
     * @return The total records as reported by the OAI-PMH server on the first ListRecords response
     */
    public int getCompleteListSize() {
        return completeListSize;
    }

    /**
     * Construct the query portion of the http request
     * 
     * @return a String containing the query portion of the http request
     */
    private static String getRequestURL(String baseURL, String from, String until, String set,
            String metadataPrefix) {
        StringBuffer requestURL = new StringBuffer(baseURL);
        requestURL.append("?verb=ListRecords");
        if (from != null) requestURL.append("&from=").append(from);
        if (until != null) requestURL.append("&until=").append(until);
        if (set != null) requestURL.append("&set=").append(set);
        requestURL.append("&metadataPrefix=").append(metadataPrefix);
        return requestURL.toString();
    }

    /**
     * Construct the query portion of the http request (resumptionToken version)
     * 
     * @param baseURL
     * @param resumptionToken
     * @return
     */
    private static String getRequestURL(String baseURL, String resumptionToken) {
        StringBuffer requestURL = new StringBuffer(baseURL);
        requestURL.append("?verb=ListRecords");
        requestURL.append("&resumptionToken=").append(URLEncoder.encode(resumptionToken));
        return requestURL.toString();
    }

}
