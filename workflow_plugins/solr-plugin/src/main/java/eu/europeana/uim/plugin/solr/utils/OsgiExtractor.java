package eu.europeana.uim.plugin.solr.utils;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.ctc.wstx.stax.WstxInputFactory;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

import eu.europeana.corelib.definitions.model.EdmLabel;
import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.corelib.dereference.impl.EntityImpl;
import eu.europeana.corelib.dereference.impl.Extractor;

public class OsgiExtractor extends Extractor {

	public OsgiExtractor() {

	}
	
	public String getEdmLabel(String field) {

		return vocabulary != null ? vocabulary.getElements().get(field)
				.toString() : EdmLabel.NULL.toString();
	}

	private ControlledVocabularyImpl vocabulary;
	private Datastore datastore;
	private final static long UPDATETIMESTAMP = 5184000000l;
	public Map<String, List<String>> denormalize(String resource,
			ControlledVocabularyImpl controlledVocabulary) {
		Map<String,List<String>> denormalizedValues = new HashMap<String, List<String>>();
		if(controlledVocabulary!=null){
		String suffix = controlledVocabulary.getSuffix() != null ? controlledVocabulary
				.getSuffix() : "";
		String xmlString = retrieveValueFromResource(resource + suffix != null ? resource
				+ suffix
				: "");
		XMLInputFactory inFactory = new WstxInputFactory();
		Source source;
		if (xmlString.length() > 0) {
			vocabulary = controlledVocabulary;
			try {
				source = new StreamSource(new ByteArrayInputStream(
						xmlString.getBytes()), "UTF-8");
				XMLStreamReader xml = inFactory.createXMLStreamReader(source);
				String element = "";
				while (xml.hasNext()) {
					List<String> tempList = new ArrayList<String>();
					switch (xml.getEventType()) {
					case XMLStreamConstants.START_DOCUMENT:
						
						break;
					case XMLStreamConstants.START_ELEMENT:
						element = (xml.getPrefix() != null ? xml.getPrefix()
								+ ":" : "")
								+ xml.getLocalName();

						if (isMapped(element)) {
							if (xml.getAttributeCount() > 0) {
								String attribute = xml.getAttributePrefix(0)
										+ ":" + xml.getAttributeLocalName(0);
								if (isMapped(element + "_" + attribute)) {
									if(!denormalizedValues.containsKey(element+"_"+attribute)){
										tempList.add(xml.getAttributeValue(0));
										denormalizedValues.put(getEdmLabel(element + "_" + attribute),tempList);
										tempList = new ArrayList<String>();
									}
									else{
										tempList = denormalizedValues.get(getEdmLabel(element + "_" + attribute));
										String val = xml.getAttributeValue(0);
										if(!tempList.contains(val)){
											tempList.add(val);
										}
										denormalizedValues.put(getEdmLabel(element + "_" + attribute),tempList);
										tempList = new ArrayList<String>();
									}
									
								
							} else {
								if(!denormalizedValues.containsKey(getEdmLabel(element))){
									tempList.add(xml.getElementText());
									denormalizedValues.put(getEdmLabel(element),tempList);
									tempList = new ArrayList<String>();
								}
								else{
									tempList = denormalizedValues.get(getEdmLabel(element));
									String val = xml.getElementText();
									if(!tempList.contains(val)){
										tempList.add(val);
									}
									denormalizedValues.put(getEdmLabel(element),tempList);
									tempList = new ArrayList<String>();
								}
								}
							}
						}
						break;
					
					default:
						break;
					}
					xml.next();
				}
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		}
		return denormalizedValues;
	}

	public void setDatastore(Datastore datastore){
		this.datastore=datastore;
	}

	public boolean isMapped(String field) {

		if (vocabulary != null) {
			for (Entry<String, EdmLabel> entry : vocabulary.getElements()
					.entrySet()) {
				if (StringUtils.contains(entry.getKey(), field)
						&& !entry.getValue().equals(EdmLabel.NULL)) {
					return true;
				}
			}
		}
		return false;
	}
	private String retrieveValueFromResource(String resource) {

		EntityImpl entity = datastore.find(EntityImpl.class)
				.filter("uri", resource).get();
		if (entity == null) {
				String val = retrieveValue(resource);
				if (val.length()>0){
				EntityImpl newEntity = new EntityImpl();
				newEntity.setUri(resource);
				newEntity.setTimestamp(new Date().getTime());
				newEntity.setContent(val);
				datastore.save(newEntity);
				}
				return val;
		}
		else {
			if(new Date().getTime()-entity.getTimestamp()<UPDATETIMESTAMP){
				return entity.getContent();
			}
			else {
				String val = retrieveValue(resource);
				Query<EntityImpl> updateQuery = datastore
						.createQuery(EntityImpl.class).field("uri").equal(resource);
				UpdateOperations<EntityImpl> ops =datastore.createUpdateOperations(EntityImpl.class)
						.set("content", val);
				datastore.update(updateQuery, ops);
				return val;
			}
		}
	}

	private String retrieveValue(String resource) {
		URLConnection urlConnection;
		try {

			urlConnection = new URL(resource).openConnection();

			InputStream inputStream = urlConnection.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, "UTF-8");
			return writer.toString();
		} catch (MalformedURLException e) {
			return "";
		} catch (IOException e) {
			return "";
		}
	}
}
