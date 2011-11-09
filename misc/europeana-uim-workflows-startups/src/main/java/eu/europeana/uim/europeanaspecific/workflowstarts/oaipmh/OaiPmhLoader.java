package eu.europeana.uim.europeanaspecific.workflowstarts.oaipmh;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;

import org.theeuropeanlibrary.model.common.qualifier.Language;
import org.theeuropeanlibrary.model.common.qualifier.LanguageRelation;
import org.theeuropeanlibrary.model.common.qualifier.Status;
import org.theeuropeanlibrary.model.common.qualifier.TemporalRelation;
import org.theeuropeanlibrary.model.common.time.Instant;
import org.theeuropeanlibrary.model.common.time.InstantGranularity;
import org.theeuropeanlibrary.model.tel.Metadata;
import org.theeuropeanlibrary.model.tel.ObjectModelRegistry;
import org.theeuropeanlibrary.model.tel.qualifier.FieldSource;
import org.theeuropeanlibrary.model.common.Identifier;
import org.theeuropeanlibrary.model.common.qualifier.IdentifierType;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;
import eu.europeana.uim.edmcore.definitions.RDF;
import eu.europeana.uim.model.europeanaspecific.utils.DefUtils;


import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import eu.europeana.uim.api.LoggingEngine;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.Request;

/**
 * OAI-PMH loader. This class provides methods to load all records from a specific repository-set
 * into the storage engine.
 * 
 */
public class OaiPmhLoader {
    /**
     * hard coded prefix that is used as namespace in our repox system, it is stripped away
     * automatically to get the initial identifier from the provider
     */
    private static final String          REPOX_PREFIX  = "urn:theeuropeanlibrary.org:";

    private final static Logger          log           = Logger.getLogger(OaiPmhLoader.class.getName());

    private final static DateFormat      harvestedDate = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressWarnings("rawtypes")
    private final StorageEngine          storage;
    @SuppressWarnings("unused")
    private LoggingEngine<?>             loggingEngine;
    @SuppressWarnings("rawtypes")
    private final Request                request;
    private final ProgressMonitor        monitor;

    @SuppressWarnings("rawtypes")
    //private final FieldProcessor         processor;
    private final Iterator<OaiPmhRecord> harvest;

    private int                          maximum       = 0;
    private int                          totalProgress = 0;

    private static DocumentBuilder       builder;

    /** 
     * An option to allow the unescaping of html encoded characters inside records 	
     */
    private boolean unescapeHtmlCharactersInRecordElements=false;
    
    static {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (DOMException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Creates a new instance of this class.
     * 
     * @param <I>
     * 
     * @param storage
     * @param request
     * @param monitor
     * @param harvest
     * @param loggingEngine
    */
    
    
    @SuppressWarnings("rawtypes")
    public <I> OaiPmhLoader(Iterator<OaiPmhRecord> harvest, StorageEngine<I> storage, Request<I> request, 
    		ProgressMonitor monitor, LoggingEngine<I> loggingEngine) {
        super();
        this.harvest = harvest;
        this.storage = storage;
        this.request = request;
        this.monitor = monitor;
        this.loggingEngine = loggingEngine;
    }

    /**
     * @return maximum number of loaded
     */
    public int getMaximum() {
        return maximum;
    }

    /**
     * @param maximum
     *            maximum number of loaded
     */
    public void setMaximum(int maximum) {
        this.maximum = maximum;
        if (harvest instanceof OaipmhHarvest) {
            ((OaipmhHarvest)harvest).setMaxRecordsToHarvest(maximum);
        }
    }

    /**
     * @param batchSize
     *            number of loaded records
     * @param save
     *            Should they be saved to the index?
     * @return list of loaded MARC records
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public synchronized List<MetaDataRecord> doNext(int batchSize, boolean save) {
        List<MetaDataRecord> result = new ArrayList<MetaDataRecord>();

        int progress = 0;

        while (harvest.hasNext() && !monitor.isCancelled()) {
            if (progress >= batchSize || (totalProgress >= maximum && maximum > 0)) {
                break;
            }

            OaiPmhRecord record = harvest.next();
            String identifier = record.getIdentifier();
            if (identifier.startsWith(REPOX_PREFIX)) {
                int prefix = identifier.indexOf(':', REPOX_PREFIX.length()) + 1;
                identifier = identifier.substring(prefix, identifier.length());
            }

            if(unescapeHtmlCharactersInRecordElements) 
            	unescapeHtmlCharacters(record.getMetadata());
            
            try {
                MetaDataRecord mdr = storage.createMetaDataRecord(request.getCollection(),
                        identifier);
                
                
                /*

                mdr.addValue(ObjectModelRegistry.IDENTIFIER, new Identifier(identifier),
                        IdentifierType.LOCAL_IDENTIFIER);
                mdr.addValue(ObjectModelRegistry.INSTANT, new Instant(new Date(),
                        InstantGranularity.SECOND), TemporalRelation.OP_LOADED);

                if (record.getProvenance() != null) {
                    Node originDescription = record.getProvenance().getFirstChild();
                    if (originDescription != null &&
                        "originDescription".equals(originDescription.getLocalName())) {
                        Node node = originDescription.getAttributes().getNamedItem("harvestDate");
                        if (node != null) {
                            String harvested = node.getTextContent();
                            try {
                                Date parse = harvestedDate.parse(harvested);
                                
                                mdr.addValue(ObjectModelRegistry.INSTANT, new Instant(parse,
                                        InstantGranularity.DAY), TemporalRelation.OP_HARVESTED);
                            } catch (Throwable t) {
                            }
                        }
                    }
                }

                if (record.isDeleted()) {
                    mdr.addValue(ObjectModelRegistry.STATUS, Status.DELETED);

                    MetaDataRecord<?> activeMdr = storage.getMetaDataRecord(mdr.getId());
                    if (activeMdr == null) {
                        // the record was not in TEL before, so the record should be skipped
                        continue;
                    }
                    Metadata raw = activeMdr.getFirstValue(ObjectModelRegistry.METADATA,
                            FieldSource.PROVIDER);
                    if (raw != null) {
                        Document doc = builder.parse(new InputSource(new StringReader(
                                raw.getRecordInXml())));
                        record.setMetadata((Element)doc.getChildNodes().item(0));
                    }
                } else {
                    mdr.addValue(ObjectModelRegistry.STATUS, Status.ACTIVE);

                    String oaiidentifier = record.getIdentifier();

                    if (oaiidentifier != null && oaiidentifier.trim().length() > 0) {
                        mdr.addValue(ObjectModelRegistry.IDENTIFIER,
                                new Identifier(record.getIdentifier()), IdentifierType.OAIPMH);
                    } else {
                        log.warning("No identifier (" + mdr.getId() + "):\n");   //+ XmlUtil.writeDomToString(record.getMetadata()));
                                    
                    }

                }
                    /*
                    */
                if (request != null) {
                    String language = request.getCollection().getLanguage();
                    
                    /*
                    
                    if (language != null && Language.lookupLanguage(language) != null) {
                        mdr.addValue(ObjectModelRegistry.LANGUAGE,
                                Language.lookupLanguage(language),
                                LanguageRelation.LANGUAGE_OF_COLLECTION);
                    }
                    mdr.addValue(ObjectModelRegistry.COLLECTION,
                            request.getCollection().getMnemonic());
                    */        

                    RDF validedmrecord = DefUtils.unmarshallObjectFromElement(record.getMetadata(), new RDF());
                    mdr.addValue(EuropeanaModelRegistry.EDMRECORD,validedmrecord);
                    
                }

                if (save) {
                    storage.updateMetaDataRecord(mdr);
                }
                storage.addRequestRecord(request, mdr);

                result.add(mdr);
                progress++;
                totalProgress++;
            } catch (Throwable t) {
                log.log(Level.WARNING, "Failed to process record:" + identifier, t);
            }
        }

        return result;
    }

    /**
     * @return true iff no new records can be read from files
     */
    public synchronized boolean isFinished() {
        return !harvest.hasNext();
    }

    /**
     * Close loader.
     */
    public void close() {
        // nothing to do
    }

    /**
     * @return The total records as reported by the OAI-PMH server on the first ListRecords response
     */
    public int getExpectedRecordCount() {
        if (harvest instanceof OaipmhHarvest) {
            if (maximum > 0) { return Math.min(maximum,
                    ((OaipmhHarvest)harvest).getCompleteListSize()); }
            return ((OaipmhHarvest)harvest).getCompleteListSize();
        }
        return -1;
    }

	/**
	 * Sets the unescapeHtmlCharactersInRecordElements to the given value.
	 * @param unescapeHtmlCharactersInRecordElements the unescapeHtmlCharactersInRecordElements to set
	 */
	public void setUnescapeHtmlCharactersInRecordElements(
			boolean unescapeHtmlCharactersInRecordElements) {
		this.unescapeHtmlCharactersInRecordElements = unescapeHtmlCharactersInRecordElements;
	}
	
	
	private static void unescapeHtmlCharacters(Element domElement) {
		if (!domElement.getTextContent().isEmpty())
			domElement.setTextContent(StringEscapeUtils.unescapeHtml(domElement.getTextContent()));
		/*
		for(Element el: XmlUtil.elements(domElement)) {
			unescapeHtmlCharacters(el);
		}
		*/
	}
}

