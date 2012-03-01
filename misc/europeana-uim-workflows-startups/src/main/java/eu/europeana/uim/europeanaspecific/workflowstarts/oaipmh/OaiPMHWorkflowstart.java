/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.oaipmh;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.workflow.AbstractWorkflowStart;
import eu.europeana.uim.workflow.Task;
import eu.europeana.uim.workflow.TaskCreator;
import eu.europeana.uim.workflow.WorkflowStartFailedException;
import eu.europeana.uim.store.UimDataSet;

/**
 * 
 * @author Georgios Markakis
 */
public class OaiPMHWorkflowstart extends AbstractWorkflowStart{

	
    /** Property which allows to overwrite base url from collection/provider */
    public static final String                     HARVEST_BASEURL                     = "oaipmh.overwrite.baseUrl";

    /** Property which allows to overwrite metadata prefix from collection/provider */
    public static final String                     HARVEST_METADATAPREFIX              = "oaipmh.overwrite.metadata";

    /** Property which allows to overwrite set from collection */
    public static final String                     HARVEST_SET                         = "oaipmh.overwrite.set";

    /** property name to retrieve maximum number of records to load */
    public static final String                     HARVEST_MAXIMUM_RECORDS             = "oaipmh.maximum.records";

    /** property name to retrieve maximum number of records to load */
    public static final String                     HARVEST_ENFORCE_FULLSET             = "oaipmh.enforce.fullset";

    /** */
    public static final String                     HARVEST_RESUMPTION_TOKEN            = "oaipmh.resumption.token";

    /** property name to retrieve maximum number of records to load */
    public static final String                     HARVEST_ENFORCE_FROM                = "oaipmh.enforce.from";

    /** property name to retrieve maximum number of records to load */
    public static final String                     HARVEST_ENFORCE_UNTIL               = "oaipmh.enforce.until";

    /** property name to define MARC variant (marc21, unimarc) */
    public static final String                     HARVEST_EXPECTED                    = "oaipmh.expected.records";

    /** property name to define MARC variant (marc21, unimarc) */
    public static final String                     PROCESSOR_TYPE                      = "processor.type";

    /** property name to define MARC variant (marc21, unimarc) */
    public static final String                     PROCESSOR_ADDON_CONTROLFIELDS       = "processor.marc.addon.controlfields";

    /** property name to define MARC variant (marc21, unimarc) */
    public static final String                     PROCESSOR_ADDON_DATAFIELDS          = "processor.marc.addon.datafields";

    /** property name to define MARC variant (marc21, unimarc) */
    public static final String                     PROCESSOR_ADDON_XSL                 = "processor.marc.addon.xsl.file";

    /** property name to define if html characters should be unescaped */
    public static final String                     PROCESSOR_ADDON_UNESCAPE_HTML_CHARS = "processor.xml.addon.unescapehtmlchars";

    /**
     * parameters to be set for oai pmh workflow start
     */
    private static final List<String>              PARAMETER                           = new ArrayList<String>() {
                                                                                           {
                                                                                               add(HARVEST_MAXIMUM_RECORDS);
                                                                                               add(HARVEST_BASEURL);
                                                                                               add(HARVEST_METADATAPREFIX);
                                                                                               add(HARVEST_SET);
                                                                                               add(HARVEST_ENFORCE_FULLSET);
                                                                                               add(HARVEST_ENFORCE_FROM);
                                                                                               add(HARVEST_METADATAPREFIX);
                                                                                               add(HARVEST_RESUMPTION_TOKEN);
                                                                                               add(HARVEST_ENFORCE_UNTIL);
                                                                                               add(HARVEST_EXPECTED);
                                                                                               add(PROCESSOR_TYPE);

                                                                                               add(PROCESSOR_ADDON_CONTROLFIELDS);
                                                                                               add(PROCESSOR_ADDON_DATAFIELDS);
                                                                                               add(PROCESSOR_ADDON_XSL);
                                                                                               add(PROCESSOR_ADDON_UNESCAPE_HTML_CHARS);
                                                                                           }
                                                                                       };
	
	
    private static TKey<OaiPMHWorkflowstart, Data> DATA_KEY                            = TKey.register(
    		OaiPMHWorkflowstart.class,
            "data",
            Data.class);
	
    
    /**
     * default batch size
     */
    public static int                              BATCH_SIZE                          = 250;

    @SuppressWarnings("unused")
    private static final SimpleDateFormat          ISO8601TIMEFORMAT                   = new SimpleDateFormat(
                                                                                               "yyyy-MM-dd'T'HH:mm:ssZ");
    private static final SimpleDateFormat          ISO8601DATEFORMAT                   = new SimpleDateFormat(
                                                                                               "yyyy-MM-dd");
    private static final SimpleDateFormat          SIMPLEDATEFORMAT                    = new SimpleDateFormat(
                                                                                               "yyyy.MM.dd");
    
    
    
	public OaiPMHWorkflowstart(String name, String description) {
		super(name, description);
	}

	
	private final static class Data implements Serializable {

	       public OaiPmhLoader loader;
		        public Request<?>   request;
		
	            public int          maxrecords = 0;
		        public int          expected   = 0;
		    }
	
	
	
	@Override
	public List<String> getParameters() {
        return PARAMETER;
	}

	@Override
	public int getPreferredThreadCount() {
		return 5;
	}

	@Override
	public int getMaximumThreadCount() {
		return 10;
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <I> TaskCreator<I> createLoader(final ExecutionContext<I> context,
            final StorageEngine<I> storage) {
        final Data value = context.getValue(DATA_KEY);
        if (!value.loader.isFinished()) { return new TaskCreator() {
            @Override
            public void run() {
                try {
                    List<MetaDataRecord> list = value.loader.doNext(100, false);

                    for (MetaDataRecord mdr : list) {
                        Task task = new Task(mdr, storage, context);
                        synchronized (getQueue()) {
                            getQueue().offer(task);
                        }
                    }

                } catch (Throwable t) {
                    throw new RuntimeException("Failed to retrieve MDRs from storage. " +
                                               context.getExecution().toString(), t);
                } finally {
                    setDone(true);
                }
            }
        }; }
        return null;
    }

    
    
    @Override
    public <I> boolean isFinished(ExecutionContext<I> context, StorageEngine<I> storage) {
        Data value = context.getValue(DATA_KEY);
        boolean finished = value.loader.isFinished();
        return finished;
    }

    
    
    @Override
    public <I> void completed(ExecutionContext<I> context, StorageEngine<I> storage) {
        Data value = context.getValue(DATA_KEY);
        value.loader.close();

        if (context.getExecution().isCanceled()) {
            value.request.setFailed(true);
        }
    }

    @Override
    public <I> int getTotalSize(ExecutionContext<I> context) {
        Data value = context.getValue(DATA_KEY);
        if (value == null) return Integer.MAX_VALUE;

        if (value.expected > 0) { return value.expected; }

        if (value.loader.getExpectedRecordCount() > 0) { return value.loader.getExpectedRecordCount(); }
        return Integer.MAX_VALUE;
    }
    
    
    
	@Override
	public <I> void initialize(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {
	       Data value = new Data();
	        UimDataSet dataSet = context.getDataSet();
	        if (dataSet instanceof Collection) {
	            Collection collection = (Collection)dataSet;
	            Date thisFrom = null;
	            Date thisTill = null;

	            try {
	                List<Request<I>> requests = storage.getRequests(collection);
	                for (Request<I> request : requests) {
	                    if (!request.isFailed()) {
	                        if (thisFrom == null || thisFrom.before(request.getDataTill())) {
	                            thisFrom = request.getDataTill();
	                        }
	                    }
	                }

	                String fullload = context.getProperties().getProperty(HARVEST_ENFORCE_FULLSET);
	                if (fullload != null && Boolean.parseBoolean(fullload)) {
	                    thisFrom = null;
	                }

	                String propFrom = context.getProperties().getProperty(HARVEST_ENFORCE_FROM);
	                if (propFrom != null) {
	                    try {
	                        thisFrom = ISO8601DATEFORMAT.parse(propFrom);
	                    } catch (ParseException e) {
	                        thisFrom = SIMPLEDATEFORMAT.parse(propFrom);
	                    }
	                }

	                String propTill = context.getProperties().getProperty(HARVEST_ENFORCE_UNTIL);
	                if (propTill != null) {
	                    try {
	                        thisTill = ISO8601DATEFORMAT.parse(propTill);
	                    } catch (ParseException e) {
	                        thisTill = SIMPLEDATEFORMAT.parse(propTill);
	                    }
	                }

	            } catch (StorageEngineException e1) {
	                throw new WorkflowStartFailedException("Caused by StorageEngineException", e1);
	            } catch (ParseException e) {
	                throw new WorkflowStartFailedException("Caused by parse exception", e);
	            }

	            try {
	                Request request = storage.createRequest(collection, new Date());
	                storage.updateRequest(request);
	                value.request = request;

	                Properties properties = context.getProperties();
	                String property = properties.getProperty(HARVEST_MAXIMUM_RECORDS, "0");
	                value.maxrecords = Integer.parseInt(property);

	                property = properties.getProperty(HARVEST_EXPECTED, "0"); //
	                value.expected = Integer.parseInt(property);

	                String metadataprefix = "edm";

	                String controlFields = properties.getProperty(PROCESSOR_ADDON_CONTROLFIELDS); //
	                String dataFields = properties.getProperty(PROCESSOR_ADDON_DATAFIELDS); //


	                String baseURL = properties.getProperty(HARVEST_BASEURL);
	                if (baseURL == null) {
	                	
	                	//TODO: Hardwired for the time being, decide on whether this is going to be a Karaf property set by RepoxPlugin
	                    baseURL = "http://bd2.inesc-id.pt:8080/repox2/OAIHandler?";
	                }

	                String setSpec = properties.getProperty(HARVEST_SET);
	                if (setSpec == null || setSpec.trim().isEmpty()) {
	                        setSpec = request.getCollection().getMnemonic();
	                }

	                OaipmhHarvest harvest;
	                try {
	                    if (thisFrom == null && thisTill == null) {
	                        String resume = properties.getProperty(HARVEST_RESUMPTION_TOKEN);
	                        if (resume == null || resume.isEmpty()) {
	                            harvest = new OaipmhHarvest(baseURL, metadataprefix, setSpec, context.getLoggingEngine());
	                        } else {
	                            harvest = new OaipmhHarvest(baseURL, resume, context.getLoggingEngine());
	                        }

	                    } else {
	                        if (thisFrom != null) {
	                            if (thisTill != null) {
	                                harvest = new OaipmhHarvest(baseURL,
	                                        ISO8601DATEFORMAT.format(thisFrom),
	                                        ISO8601DATEFORMAT.format(thisTill), metadataprefix, setSpec, context.getLoggingEngine());
	                            } else {
	                                harvest = new OaipmhHarvest(baseURL,
	                                        ISO8601DATEFORMAT.format(thisFrom), null, metadataprefix,
	                                        setSpec, context.getLoggingEngine());
	                            }
	                        } else {
	                            harvest = new OaipmhHarvest(baseURL, null,
	                                    ISO8601DATEFORMAT.format(thisTill), metadataprefix, setSpec, context.getLoggingEngine());
	                        }
	                    }
	                } catch (HarvestException e) {
	                    context.getLoggingEngine().logFailed(Level.SEVERE, "OaiPmhWorkflowStart", e,
	                            "Failed to start harvester.");

	                    throw new WorkflowStartFailedException("Could not load data '" +
	                                                           collection.getId() + "'!", e);

	                }

	                @SuppressWarnings("unchecked")
					OaiPmhLoader loader = new OaiPmhLoader(harvest,storage, request,
	                        context.getMonitor(), context.getLoggingEngine());

	                String unescapeHtmlChars = properties.getProperty(PROCESSOR_ADDON_UNESCAPE_HTML_CHARS);
	                if (unescapeHtmlChars != null && !unescapeHtmlChars.isEmpty()) {
	                    if (Boolean.parseBoolean(unescapeHtmlChars))
	                        loader.setUnescapeHtmlCharactersInRecordElements(true);
	                }

	                value.loader = loader;
	                loader.setMaximum(value.maxrecords);

	                context.putValue(DATA_KEY, value);

	            } catch (StorageEngineException e) {
	                context.getLoggingEngine().logFailed(Level.SEVERE, "OaiPmhWorkflowStart", e,
	                        "Failed to load");

	                throw new WorkflowStartFailedException("Could not create request '" +
	                                                       collection.getId() + "'!", e);
	            }

	        } else if (dataSet instanceof Request) {
	            throw new IllegalArgumentException("A request cannot be the basis for a new harvest.");
	        } else {
	            throw new IllegalStateException("Unsupported dataset <" + context.getDataSet() + ">");
	        }

		
	}



}
