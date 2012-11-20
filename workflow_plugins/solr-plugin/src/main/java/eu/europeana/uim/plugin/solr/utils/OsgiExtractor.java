package eu.europeana.uim.plugin.solr.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.ctc.wstx.stax.WstxInputFactory;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.mongodb.MongoException;

import eu.europeana.corelib.definitions.jibx.AgentType;
import eu.europeana.corelib.definitions.jibx.Concept;
import eu.europeana.corelib.definitions.jibx.LiteralType;
import eu.europeana.corelib.definitions.jibx.PlaceType;
import eu.europeana.corelib.definitions.jibx.ResourceOrLiteralType;
import eu.europeana.corelib.definitions.jibx.ResourceOrLiteralType.Lang;
import eu.europeana.corelib.definitions.jibx.ResourceType;
import eu.europeana.corelib.definitions.jibx.TimeSpanType;
import eu.europeana.corelib.definitions.model.EdmLabel;
import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.corelib.dereference.impl.EntityImpl;
import eu.europeana.corelib.dereference.impl.Extractor;

public class OsgiExtractor extends Extractor {

	public OsgiExtractor() {

	}

	public List<EdmLabel> getEdmLabel(String field) {

		return vocabulary != null ? vocabulary.getElements().get(field)
				 : new ArrayList<EdmLabel>();
	}

	private ControlledVocabularyImpl vocabulary;
	private Datastore datastore;
	private final static long UPDATETIMESTAMP = 5184000000l;

	@SuppressWarnings("rawtypes")
	public Map<String, List> denormalize(String resource,
			ControlledVocabularyImpl controlledVocabulary, int iterations, boolean iterFromVocabulary)
			throws SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {

		
		if (controlledVocabulary != null) {
			vocabulary = controlledVocabulary;
			String suffix = controlledVocabulary.getSuffix() != null ? controlledVocabulary
					.getSuffix() : "";
			int iters = iterFromVocabulary?controlledVocabulary.getIterations():iterations;
			String xmlString = retrieveValueFromResource(resource + suffix != null ? resource
					+ suffix
					: "");
			

			if (xmlString.length() > 0) {
				
				return createDereferencingMap(xmlString,iterations);
			}
			
		}
		return new HashMap<String, List>();
	}

	private Map<String, List> createDereferencingMap(String xmlString, int iterations) throws SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		XMLInputFactory inFactory = new WstxInputFactory();
		Map<String, List> denormalizedValues = new HashMap<String, List>();
		List<Concept> concepts = new ArrayList<Concept>();
		List<AgentType> agents = new ArrayList<AgentType>();
		List<TimeSpanType> timespans = new ArrayList<TimeSpanType>();
		List<PlaceType> places = new ArrayList<PlaceType>();
		
		Concept lastConcept = null;
		AgentType lastAgent = null;
		TimeSpanType lastTimespan = null;
		PlaceType lastPlace = null;
		try {
			Source source = new StreamSource(new ByteArrayInputStream(
					xmlString.getBytes()), "UTF-8");
			XMLEventReader xml = inFactory.createXMLEventReader(source);

			String element = "";
			while (xml.hasNext()) {
				XMLEvent evt = xml.nextEvent();
				if (evt.isStartElement()) {
					StartElement sElem = evt.asStartElement();
					element = (sElem.getName().getPrefix() != null ? sElem
							.getName().getPrefix() + ":"
							: "")
							+ sElem.getName().getLocalPart();
					// If it is mapped then
					if (isMapped(element)) {
						for(EdmLabel edmLabel: getEdmLabel(element)){
						if (sElem.getAttributes().hasNext()) {
							Attribute attr = (Attribute) sElem
									.getAttributes().next();
							String attribute = attr.getName()
									.getPrefix()

							+ ":" + attr.getName().getLocalPart();
							// Is the attribute mapped?
							if (isMapped(element + "_" + attribute)) {
								for(EdmLabel label: getEdmLabel(element + "_" + attribute)){
								String attrVal = attr.getValue();
								String elem = null;
								if (xml.peek().isCharacters()) {
									elem = xml.nextEvent()
											.asCharacters().getData();
								}

								if (StringUtils.equals(
										label.toString(),
										"skos_concept")) {
									if (lastConcept != null) {
										concepts.add(lastConcept);
									}

									lastConcept = createNewEntity(
											Concept.class, attrVal);
								} else

								if (StringUtils.equals(
										label.toString(),
										"edm_agent")) {
									if (lastAgent != null) {
										agents.add(lastAgent);
									}

									lastAgent = createNewEntity(
											AgentType.class, attrVal);
								} else

								if (StringUtils.equals(
										label.toString(),
										"edm_timespan")) {
									if (lastTimespan != null) {
										timespans.add(lastTimespan);
									}

									lastTimespan = createNewEntity(
											TimeSpanType.class, attrVal);
								} else

								if (StringUtils.equals(
										label.toString(),
										"edm_place")) {
									if (lastPlace != null) {
										places.add(lastPlace);
									}
									lastPlace = createNewEntity(
											PlaceType.class, attrVal);
								} else {
									if (StringUtils.startsWith(
											label.toString(), "cc")) {

										appendConceptValue(lastConcept,
												edmLabel.toString(),
												elem,
												label.toString(),
												attrVal,iterations);
									}

									else if (StringUtils.startsWith(
											label.toString(), "ts")) {

										appendValue(TimeSpanType.class,
												lastTimespan,
												edmLabel.toString(),
												elem,
												label.toString(),
												attrVal,iterations);
									} else if (StringUtils.startsWith(
											label.toString(), "ag")) {

										appendValue(AgentType.class,
												lastAgent,
												edmLabel.toString(),
												elem,
												label.toString(),
												attrVal,iterations);
									} else if (StringUtils.startsWith(
											label.toString(), "pl")) {

										appendValue(PlaceType.class,
												lastPlace,
												edmLabel.toString(),
												elem,
												label.toString(),
												attrVal,iterations);
									}
								}
								}
							}
							// Since the attribute is not mapped
							else {

								if (StringUtils.equals(
										edmLabel.toString(),
										"skos_concept")) {
									if (lastConcept != null) {
										concepts.add(lastConcept);
									}
									lastConcept = createNewEntity(
											Concept.class,
											xml.getElementText());

								} else if (StringUtils.equals(
										edmLabel.toString(),
										"edm_agent")) {
									if (lastAgent != null) {
										agents.add(lastAgent);
									}
									lastAgent = createNewEntity(
											AgentType.class,
											xml.getElementText());

								} else if (StringUtils.equals(
										edmLabel.toString(),
										"edm_timespan")) {
									if (lastTimespan != null) {
										timespans.add(lastTimespan);
									}
									lastTimespan = createNewEntity(
											TimeSpanType.class,
											xml.getElementText());

								} else if (StringUtils.equals(
										edmLabel.toString(),
										"edm_place")) {
									if (lastPlace != null) {
										places.add(lastPlace);
									}
									lastPlace = createNewEntity(
											PlaceType.class,
											xml.getElementText());

								}
								if (StringUtils.startsWith(
										edmLabel.toString(), "cc")) {
									appendConceptValue(lastConcept,
											edmLabel.toString(),
											xml.getElementText(), null,
											null,iterations);
								}

								else if (StringUtils.startsWith(
										edmLabel.toString(), "ts")) {
									appendValue(TimeSpanType.class,
											lastTimespan,
											edmLabel.toString(),
											xml.getElementText(), null,
											null,iterations);
								} else if (StringUtils.startsWith(
										edmLabel.toString(), "ag")) {
									appendValue(AgentType.class,
											lastAgent,
											edmLabel.toString(),
											xml.getElementText(), null,
											null,iterations);
								} else if (StringUtils.startsWith(
										edmLabel.toString(), "pl")) {
									appendValue(PlaceType.class,
											lastPlace,
											edmLabel.toString(),
											xml.getElementText(), null,
											null,iterations);

								}
							}

						}
						// Since it does not have attributes
						else {
							XMLEvent evt2 = xml.nextEvent();
							if (evt2.isCharacters()) {
								if (StringUtils.equals(
										edmLabel.toString(),
										"skos_concept")) {
									if (lastConcept != null) {
										concepts.add(lastConcept);
									}
									lastConcept = createNewEntity(
											Concept.class, evt2
													.asCharacters()
													.getData());

								} else if (StringUtils.equals(
										edmLabel.toString(),
										"edm_agent")) {
									if (lastAgent != null) {
										agents.add(lastAgent);
									}
									lastAgent = createNewEntity(
											AgentType.class, evt2
													.asCharacters()
													.getData());

								} else if (StringUtils.equals(
										edmLabel.toString(),
										"edm_timespan")) {
									if (lastTimespan != null) {
										timespans.add(lastTimespan);
									}
									lastTimespan = createNewEntity(
											TimeSpanType.class, evt2
													.asCharacters()
													.getData());

								} else if (StringUtils.equals(
										edmLabel.toString(),
										"edm_place")) {
									if (lastPlace != null) {
										places.add(lastPlace);
									}
									lastPlace = createNewEntity(
											PlaceType.class, evt2
													.asCharacters()
													.getData());

								}

								if (StringUtils.startsWith(
										edmLabel.toString(), "cc")) {
									appendConceptValue(lastConcept,
											edmLabel.toString(), evt2
													.asCharacters()
													.getData(), null,
											null,iterations);
								}

								else if (StringUtils.startsWith(
										edmLabel.toString(), "ts")) {
									appendValue(TimeSpanType.class,
											lastTimespan,

											edmLabel.toString(), evt2
													.asCharacters()
													.getData(), null,
											null,iterations);
								} else if (StringUtils.startsWith(
										edmLabel.toString(), "ag")) {
									appendValue(AgentType.class,
											lastAgent,
											edmLabel.toString(), evt2
													.asCharacters()
													.getData(), null,
											null,iterations);
								} else if (StringUtils.startsWith(
										edmLabel.toString(), "pl")) {
									appendValue(PlaceType.class,
											lastPlace,
											edmLabel.toString(), evt2
													.asCharacters()
													.getData(), null,
											null,iterations);
								}
							}
						}
						}
					}
					// The element is not mapped, but does it have any
					// mapped attributes?
					else {
						if (sElem.getAttributes().hasNext()) {
							Attribute attr = (Attribute) sElem
									.getAttributes().next();
							String attribute = attr.getName()
									.getPrefix()

							+ ":" + attr.getName().getLocalPart();
							// Is the attribute mapped?
							xml.next();

							// Is the attribute mapped?
							if (isMapped(element + "_" + attribute)) {
								for(EdmLabel label:getEdmLabel(element + "_" + attribute)){
								if (StringUtils.equals(
										label.toString(),
										"skos_concept")) {
									if (lastConcept != null) {
										concepts.add(lastConcept);
									}

									lastConcept = createNewEntity(
											Concept.class,
											attr.getValue());
								} else

								if (StringUtils.equals(
										label.toString(),
										"edm_agent")) {
									if (lastAgent != null) {
										agents.add(lastAgent);
									}

									lastAgent = createNewEntity(
											AgentType.class,
											attr.getValue());
								} else

								if (StringUtils.equals(
										label.toString(),
										"edm_timespan")) {
									if (lastTimespan != null) {
										timespans.add(lastTimespan);
									}

									lastTimespan = createNewEntity(
											TimeSpanType.class,
											attr.getValue());
								} else

								if (StringUtils.equals(
										label.toString(),
										"edm_place")) {
									if (lastPlace != null) {
										places.add(lastPlace);
									}
									lastPlace = createNewEntity(
											PlaceType.class,
											attr.getValue());
								} else {
									if (StringUtils.startsWith(
											label.toString(), "cc")) {
										String elem = null;
										if (xml.peek().isCharacters()) {
											elem = xml.nextEvent()
													.asCharacters()
													.getData();
										}
										String attrVal = attr
												.getValue();

										appendConceptValue(lastConcept,
												null,
												elem,
												label.toString(),
												attrVal,iterations);
									}

									else if (StringUtils.startsWith(
											label.toString(), "ts")) {
										String elem = null;
										if (xml.peek().isCharacters()) {
											elem = xml.nextEvent()
													.asCharacters()
													.getData();
										}
										String attrVal = attr
												.getValue();

										appendValue(TimeSpanType.class,
												lastTimespan,
												null,
												elem,
												label.toString(),
												attrVal,iterations);
									} else if (StringUtils.startsWith(
											label.toString(), "ag")) {
										String elem = null;
										if (xml.peek().isCharacters()) {
											elem = xml.nextEvent()
													.asCharacters()
													.getData();
										}
										String attrVal = attr
												.getValue();

										appendValue(AgentType.class,
												lastAgent,
												null,
												elem,
												label.toString(),
												attrVal,iterations);
									} else if (StringUtils.startsWith(
											label.toString(), "pl")) {
										String elem = null;
										if (xml.peek().isCharacters()) {
											elem = xml.nextEvent()
													.asCharacters()
													.getData();
										}
										String attrVal = attr
												.getValue();

										appendValue(PlaceType.class,
												lastPlace,
												null,
												elem,
												label.toString(),
												attrVal,iterations);
									}
								}
							}
							}
						}
					}
				} else if (evt.isEndDocument()) {
					if (lastConcept != null)
						concepts.add(lastConcept);
					if (lastAgent != null)
						agents.add(lastAgent);
					if (lastTimespan != null)
						timespans.add(lastTimespan);
					if (lastPlace != null)
						places.add(lastPlace);
				}

			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		denormalizedValues.put("concepts", concepts);
		denormalizedValues.put("agents", agents);
		denormalizedValues.put("timespans", timespans);
		denormalizedValues.put("places", places);
		return denormalizedValues;
	}

	public void setDatastore(Datastore datastore) {
		this.datastore = datastore;
	}

	public boolean isMapped(String field) {

		if (vocabulary != null) {
			for (Entry<String, List<EdmLabel>> entry : vocabulary.getElements()
					.entrySet()) {
				if (StringUtils.contains(entry.getKey(), field)
						&& entry.getValue()!=null) {
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
			if (val.length() > 0) {
				EntityImpl newEntity = new EntityImpl();
				newEntity.setUri(resource);
				newEntity.setTimestamp(new Date().getTime());
				newEntity.setContent(val);
				datastore.save(newEntity);
			}
			return val;
		} else {
			if (new Date().getTime() - entity.getTimestamp() < UPDATETIMESTAMP) {
				return entity.getContent();
			} else {
				String val = retrieveValue(resource);
				Query<EntityImpl> updateQuery = datastore
						.createQuery(EntityImpl.class).field("uri")
						.equal(resource);
				UpdateOperations<EntityImpl> ops = datastore
						.createUpdateOperations(EntityImpl.class).set(
								"content", val);
				datastore.update(updateQuery, ops);
				return val;
			}
		}
	}

	private String retrieveValue(String resource) {
		URLConnection urlConnection;
		if(resource!=null&&vocabulary!=null){
		try {
			
			if (StringUtils.isNotBlank(vocabulary.getReplaceUrl())){
				resource = StringUtils.replace(resource, vocabulary.getURI(), vocabulary.getReplaceUrl());
			}
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
		return "";
	}

	private <T> T createNewEntity(Class<T> clazz, String val)
			throws InstantiationException, IllegalAccessException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {
		T obj = clazz.newInstance();
		Class<?>[] cls = new Class<?>[1];
		cls[0] = (String.class);
		Method method = clazz.getMethod("setAbout", cls);
		method.invoke(obj, val);
		return obj;
	}

	@SuppressWarnings("unchecked")
	private <T> T appendValue(Class<T> clazz, T obj, String edmLabel,
			String val, String edmAttr, String valAttr, int iterations)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		RdfMethod RDF = null;
		for (RdfMethod rdfMethod : RdfMethod.values()) {
			if (StringUtils.equals(rdfMethod.getSolrField(), edmLabel)) {
				RDF = rdfMethod;
			}
		}

		//
		if (RDF.getMethodName().endsWith("List")) {

			Method mthd = clazz.getMethod(RDF.getMethodName());

			@SuppressWarnings("rawtypes")
			List lst = mthd.invoke(obj) != null ? (ArrayList) mthd.invoke(obj)
					: new ArrayList();
			if (RDF.getClazz().getSuperclass()
					.isAssignableFrom(ResourceType.class)) {

				ResourceType rs = new ResourceType();
				rs.setResource(val != null ? val : valAttr);
				if(isURI(rs.getResource())){
					denormalize(rs.getResource(),iterations-1);
				}
				lst.add(RDF.returnObject(RDF.getClazz(), rs));

			} else if (RDF.getClazz().getSuperclass()
					.isAssignableFrom(ResourceOrLiteralType.class)) {
				ResourceOrLiteralType rs = new ResourceOrLiteralType();
				if (isURI(val)) {
					rs.setResource(val);
					denormalize(val,iterations-1);
				} else {
					rs.setString(val);
				}
				if (edmAttr != null
						&& StringUtils.equals(
								StringUtils.split(edmAttr, "@")[1], "xml:lang")) {
					Lang lang = new Lang();
					lang.setLang(valAttr);
					rs.setLang(lang);
				}
				lst.add(RDF.returnObject(RDF.getClazz(), rs));
			} else if (RDF.getClazz().getSuperclass()
					.isAssignableFrom(LiteralType.class)) {
				LiteralType rs = new LiteralType();
				rs.setString(val);
				if (edmAttr != null
						&& StringUtils.equals(
								StringUtils.split(edmAttr, "@")[1], "xml:lang")) {
					LiteralType.Lang lang = new LiteralType.Lang();
					lang.setLang(valAttr);
					rs.setLang(lang);
				}
				lst.add(RDF.returnObject(RDF.getClazz(), rs));
			}

			Class<?>[] cls = new Class<?>[1];
			cls[0] = List.class;
			Method method = obj.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(obj, lst);
		} else {
			if (RDF.getClazz().isAssignableFrom(ResourceType.class)) {
				ResourceType rs = new ResourceType();
				rs.setResource(val != null ? val : valAttr);
				if(isURI(rs.getResource())){
					denormalize(rs.getResource(),iterations-1);
				}
				Class<?>[] cls = new Class<?>[1];
				cls[0] = RDF.getClazz();
				Method method = obj.getClass().getMethod(
						StringUtils.replace(RDF.getMethodName(), "get", "set"),
						cls);
				method.invoke(obj, RDF.returnObject(RDF.getClazz(), rs));
			} else if (RDF.getClazz().isAssignableFrom(LiteralType.class)) {
				LiteralType rs = new LiteralType();
				rs.setString(val);
				if(isURI(val)){
					denormalize(val, iterations-1);
				}
				if (edmAttr != null
						&& StringUtils.equals(
								StringUtils.split(edmAttr, "@")[1], "xml:lang")) {
					LiteralType.Lang lang = new LiteralType.Lang();
					lang.setLang(valAttr);
					rs.setLang(lang);
				}
				Class<?>[] cls = new Class<?>[1];
				cls[0] = RDF.getClazz();
				Method method = obj.getClass().getMethod(
						StringUtils.replace(RDF.getMethodName(), "get", "set"),
						cls);
				method.invoke(obj, RDF.returnObject(RDF.getClazz(), rs));

			} else if (RDF.getClazz().isAssignableFrom(
					ResourceOrLiteralType.class)) {
				ResourceOrLiteralType rs = new ResourceOrLiteralType();
				if (isURI(val)) {
					rs.setResource(val);
						denormalize(val,iterations-1);
				} else {
					rs.setString(val);
				}
				if (edmAttr != null
						&& StringUtils.equals(
								StringUtils.split(edmAttr, "@")[1], "xml:lang")) {
					Lang lang = new Lang();
					lang.setLang(valAttr);
					rs.setLang(lang);
				}
				Class<?>[] cls = new Class<?>[1];
				cls[0] = clazz;
				Method method = obj.getClass().getMethod(
						StringUtils.replace(RDF.getMethodName(), "get", "set"),
						cls);
				method.invoke(obj, RDF.returnObject(RDF.getClazz(), rs));
			}
		}

		//
		return obj;
	}

	private Concept appendConceptValue(Concept concept, String edmLabel,
			String val, String edmAttr, String valAttr,int iterations)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		RdfMethod RDF = null;
		for (RdfMethod rdfMethod : RdfMethod.values()) {
			if (StringUtils.equals(rdfMethod.getSolrField(), edmLabel)) {
				RDF = rdfMethod;
				break;
			}
		}
		List<Concept.Choice> lst = concept.getChoiceList() != null ? concept
				.getChoiceList() : new ArrayList<Concept.Choice>();
		if (RDF.getClazz().getSuperclass().isAssignableFrom(ResourceType.class)) {

			ResourceType obj = new ResourceType();
			obj.setResource(val != null ? val : valAttr);
			if(isURI(obj.getResource())){
				denormalize(obj.getResource(),iterations-1);
			}
			Class<?>[] cls = new Class<?>[1];
			cls[0] = RDF.getClazz();
			Concept.Choice choice = new Concept.Choice();
			Method method = choice.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(choice, RDF.returnObject(RDF.getClazz(), obj));
			lst.add(choice);

		} else if (RDF.getClazz().getSuperclass()
				.isAssignableFrom(ResourceOrLiteralType.class)) {

			ResourceOrLiteralType obj = new ResourceOrLiteralType();

			if (isURI(val)) {
				obj.setResource(val);
					denormalize(val,iterations-1);
			} else {
				obj.setString(val);
			}
			if (edmAttr != null
					&& StringUtils.equals(StringUtils.split(edmAttr, "@")[1],
							"xml:lang")) {
				Lang lang = new Lang();
				lang.setLang(valAttr);
				obj.setLang(lang);
			}
			Class<?>[] cls = new Class<?>[1];
			cls[0] = RDF.getClazz();
			Concept.Choice choice = new Concept.Choice();
			Method method = choice.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(choice, RDF.returnObject(RDF.getClazz(), obj));
			lst.add(choice);

		} else if (RDF.getClazz().getSuperclass()
				.isAssignableFrom(LiteralType.class)) {
			LiteralType obj = new LiteralType();
			obj.setString(val);
			if(isURI(val)){
				denormalize(val,iterations-1);
			}
			if (edmAttr != null) {
				LiteralType.Lang lang = new LiteralType.Lang();
				lang.setLang(valAttr);
				obj.setLang(lang);
			}
			Class<?>[] cls = new Class<?>[1];
			cls[0] = RDF.getClazz();
			Concept.Choice choice = new Concept.Choice();
			Method method = choice.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(choice, RDF.returnObject(RDF.getClazz(), obj));
			lst.add(choice);
		}
		concept.setChoiceList(lst);
		return concept;
	}

	private Map<String, List> denormalize(String val, int iterations) {
		try {
			ControlledVocabularyImpl controlledVocabulary = getControlledVocabulary(datastore, "URI", val);
			return denormalize(val, controlledVocabulary,iterations,false);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static boolean isURI(String uri) {

		try {
			new URL(uri);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}

	}

	public void setMappedField(String fieldToMap, EdmLabel europeanaField) {
		HashMap<String, List<EdmLabel>> elements = vocabulary.getElements() != null ? (HashMap<String, List<EdmLabel>>) vocabulary
				.getElements() : new HashMap<String, List<EdmLabel>>();
		List<EdmLabel> element = elements.get(fieldToMap);
		if(!element.contains(europeanaField)){
			element.add(europeanaField);
			elements.put(fieldToMap, element);
		vocabulary.setElements(elements);
		}

	}

	public String getMappedField(EdmLabel europeanaField) {
		for (String key : vocabulary.getElements().keySet()) {
			if (europeanaField.equals(vocabulary.getElements().get(key))) {
				return key;
			}
		}
		return null;
	}
	
	public ControlledVocabularyImpl getControlledVocabulary(
			Datastore datastore, String field, String filter)
			throws UnknownHostException, MongoException {
		String[] splitName = filter.split("/");
		if (splitName.length > 3) {
			String vocabularyName = splitName[0] + "/" + splitName[1] + "/"
					+ splitName[2] + "/";
			List<ControlledVocabularyImpl> vocabularies = datastore
					.find(ControlledVocabularyImpl.class)
					.filter(field, vocabularyName).asList();
			for (ControlledVocabularyImpl vocabulary : vocabularies) {
				boolean ruleController = true;
				for (String rule : vocabulary.getRules()) {
					ruleController = ruleController
							&& (filter.contains(rule) || StringUtils.equals(
									rule, "*"));
				}
				if (ruleController) {
					return vocabulary;
				}
			}
		}
		return null;
	}
}
