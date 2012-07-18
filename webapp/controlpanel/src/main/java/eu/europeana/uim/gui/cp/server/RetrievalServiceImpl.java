/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.gui.cp.server;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.theeuropeanlibrary.model.common.Link;
import org.theeuropeanlibrary.model.tel.qualifier.FieldSource;
import eu.europeana.corelib.definitions.jibx.ProvidedCHOType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.RDF.Choice;
import eu.europeana.uim.gui.cp.client.services.RetrievalService;
import eu.europeana.uim.gui.cp.shared.validation.LinkDTO;
import eu.europeana.uim.gui.cp.shared.validation.LinksResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.MetaDataRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.MetaDataResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.NGramResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.SearchResultDTO;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.gui.cp.server.AbstractOSGIRemoteServiceServlet;
import eu.europeana.uim.model.europeana.EuropeanaLink;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.MetaDataRecord.QualifiedValue;
import eu.europeana.uim.store.bean.MetaDataRecordBean;

/**
 * Implementation of the service for retrieval functionality (records from repository, record
 * content, search functionality).
 * 
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @since May 11, 2011
 */
@SuppressWarnings("unchecked")
public class RetrievalServiceImpl extends AbstractOSGIRemoteServiceServlet implements
        RetrievalService {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger log = Logger.getLogger(RetrievalServiceImpl.class.getName());

    /**
     * Creates a new instance of this class.
     */
    public RetrievalServiceImpl() {
        super();
    }

    
    /* (non-Javadoc)
     * @see eu.europeana.uim.gui.cp.client.services.RetrievalService#getRecordsForCollection(java.lang.String, int, int, java.lang.String)
     */
    @Override
    public MetaDataResultDTO getRecordsForCollection(String collectionId, int offset, int maxSize,
            String constraint) {
        StorageEngine<String> storage = (StorageEngine<String>)getEngine().getRegistry().getStorageEngine();
        if (storage == null) {
            log.log(Level.SEVERE, "Storage connection is null!");
            return null;
        }

        String recordId = null;
        if (!constraint.equals("")) {
            recordId = constraint;
        }

        int maxNumber = 0;
        List<MetaDataRecord<String>> metaDataRecords = new ArrayList<MetaDataRecord<String>>();
        if (recordId == null) {
            Collection<String> collection = null;
            try {
                collection = storage.getCollection(collectionId);
            } catch (Throwable t) {
                log.log(Level.WARNING, "Could not retrieve collection for id '" + collectionId +
                                       "'!", t);
            }

            if (collection != null) {
            	String[] recordIds = null;
                try {
                    recordIds = storage.getByCollection(collection);
                    maxNumber = recordIds.length;
                } catch (Throwable t) {
                    log.log(Level.WARNING, "Could not retrieve records for collection '" +
                                           collectionId + "'!", t);
                    maxNumber = -1;
                }

                if (recordIds != null && recordIds.length > 0) {
                    Arrays.sort(recordIds);
                    String[] subset = Arrays.copyOfRange(recordIds,
                            Math.min(offset, recordIds.length - 1),
                            Math.min(offset + maxSize, recordIds.length));
                    
                    try {
                        metaDataRecords = storage.getMetaDataRecords(Arrays.asList(subset));
                    } catch (Throwable t) {
                        log.log(Level.WARNING,
                                "Could not retrieve records for Ids '" + Arrays.toString(subset) +
                                        "'!", t);
                        maxNumber = -1;
                    }
                }
            }
        } else {
            metaDataRecords = new ArrayList<MetaDataRecord<String>>();
            try {
                metaDataRecords.add(storage.getMetaDataRecord(recordId));
                maxNumber = 1;
            } catch (Throwable t) {
                log.log(Level.WARNING, "Could not retrieve record with Id '" + recordId + "'!", t);
            }
        }

        List<MetaDataRecordDTO> results = new ArrayList<MetaDataRecordDTO>();
        for (MetaDataRecord<String> metaDataRecord : metaDataRecords) {
            if (metaDataRecord != null && metaDataRecord.getId() != null) {
                MetaDataRecordDTO record = new MetaDataRecordDTO();
                record.setId(metaDataRecord.getId());

                
                IBindingFactory bfact;
				try {
					bfact = BindingDirectory.getFactory(RDF.class);

    			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
               
                String edmxml = metaDataRecord.getFirstValue(EuropeanaModelRegistry.EDMRECORD);
                
                if (edmxml != null) {
                    RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(edmxml));
                    
                    List<Choice> rdfchoicelist = rdf.getChoiceList();
                    
                    for(Choice choice: rdfchoicelist){
                    	
                    	if(choice.ifProvidedCHO()){
                    		
                    		ProvidedCHOType cho = choice.getProvidedCHO();
                    		
                    		List<eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice> dctypelist = cho.getChoiceList();
                    		
                    		for(eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice dcchoice : dctypelist){
                    			
                    			if(dcchoice.ifTitle()){
                    				record.setTitle(dcchoice.getTitle().getString());
                    			}
                    			
                    			if(dcchoice.ifLanguage()){
                    				record.setWorkLanguage(dcchoice.getDate().getString());
                    			}
                    			
                    			if(dcchoice.ifCreator()){
                    				record.setCreator(dcchoice.getCreator().getString());
                    			}
                    			
                    			if(dcchoice.ifPublisher()){
                    				record.setPublicationPlace(dcchoice.getPublisher().getString());
                    			}
                    			
                    			if(dcchoice.ifDate()){
                    				record.setPublicationYear(dcchoice.getDate().getString());
                    			}
                    			
                    		}
                    		
                    	}
                    }

                }
                
				} catch (JiBXException e) {

					e.printStackTrace();
				}

                results.add(record);
            }
        }
        return new MetaDataResultDTO(results, maxNumber);
    }

    @Override
    public String getRawRecord(String recordId) {
        String res = "";

        StorageEngine<String> storage = (StorageEngine<String>)getEngine().getRegistry().getStorageEngine();
        if (storage == null) {
            log.log(Level.SEVERE, "Storage connection is null!");
        } else {
            MetaDataRecord<String> metaDataRecord = null;
            try {
                metaDataRecord = storage.getMetaDataRecord(recordId);
            } catch (Throwable t) {
                log.log(Level.WARNING, "Could not retrieve record for id '" + recordId + "'!", t);
            }

            if (metaDataRecord == null) {
                log.log(Level.WARNING, "Could not find record with id '" + recordId + "'!");
            } else {

              
            }
        }

        return res;
    }

    @Override
    public String getXmlRecord(String recordId) {
        String res = "";

        StorageEngine<String> storage = (StorageEngine<String>)getEngine().getRegistry().getStorageEngine();
        if (storage == null) {
            log.log(Level.SEVERE, "Storage connection is null!");
        } else {
            MetaDataRecord<String> metaDataRecord = null;
            try {
                metaDataRecord = storage.getMetaDataRecord(recordId);
            } catch (Throwable t) {
                log.log(Level.WARNING, "Could not retrieve record for id '" + recordId + "'!", t);
            }

            if (metaDataRecord == null) {
                log.log(Level.WARNING, "Could not find record with id '" + recordId + "'!");
            } else {

            	res = metaDataRecord.getFirstValue(EuropeanaModelRegistry.EDMRECORD);
            	
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource is = new InputSource(new StringReader(res));
                    Document doc = db.parse(is);
                    res = getFormattedXml(doc);
                } catch (Throwable t) {
                    log.log(Level.WARNING, "Could not format xml for '" + recordId + "'!", t);

                }

            }
        }

        return res;

        
    }

    private String getFormattedXml(Document doc) throws Exception {
        String xml = "";
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(doc);

        transformer.transform(source, result);

        xml = writer.toString();
        return xml;
    }

    @Override
    public SearchResultDTO searchIndex(String searchQuery, int offset, int maxSize, String[] facets) {
      
    	throw new UnsupportedOperationException("Not Implemented Yet");
          
    }

    @Override
    public String getSearchRecord(String recordId) {
        throw new UnsupportedOperationException("Not Implemented Yet");
    }

    @Override
    public NGramResultDTO searchSuggestions(String searchQuery, int offset, int maxSize) {
        throw new UnsupportedOperationException("Not Implemented Yet");
    }

    @Override
    public LinksResultDTO getLinks(String recordId) {
        ArrayList<LinkDTO> links = new ArrayList<LinkDTO>();


        StorageEngine<String> storage = (StorageEngine<String>)getEngine().getRegistry().getStorageEngine();
        if (storage == null) {
            log.log(Level.SEVERE, "Storage connection is null!");
        } else {
            MetaDataRecord<String> metaDataRecord = null;
            try {
                metaDataRecord = storage.getMetaDataRecord(recordId);
            } catch (Throwable t) {
                log.log(Level.WARNING, "Could not retrieve record for id '" + recordId + "'!", t);
            }

            if (metaDataRecord == null) {
                log.log(Level.WARNING, "Could not find record with id '" + recordId + "'!");
            } else {
                List<QualifiedValue<EuropeanaLink>> qualifiedValues = metaDataRecord.getQualifiedValues(EuropeanaModelRegistry.EUROPEANALINK);
                if (qualifiedValues == null) { return new LinksResultDTO(links); }
                for (QualifiedValue<EuropeanaLink> qv : qualifiedValues) {

                    Set<Enum<?>> qualifiers = qv.getQualifiers();
                    String description = "Link in record - Type ";
                    for (Enum<?> q : qualifiers) {
                        description = description + q + " ";
                    }
                    EuropeanaLink link = qv.getValue();
                    if (link != null) links.add(new LinkDTO(link.getUrl(), description));
                }
            }
        }

        LinksResultDTO linksResult = new LinksResultDTO(links);
        return linksResult;
    }
}
