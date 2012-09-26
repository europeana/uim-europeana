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

	public String getEdmLabel(String field) {

		return vocabulary != null ? vocabulary.getElements().get(field)
				.toString() : EdmLabel.NULL.toString();
	}

	private ControlledVocabularyImpl vocabulary;
	private Datastore datastore;
	private final static long UPDATETIMESTAMP = 5184000000l;

	public Map<String, List> denormalize(String resource,
			ControlledVocabularyImpl controlledVocabulary)
			throws SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {

		Map<String, List> denormalizedValues = new HashMap<String, List>();
		List<Concept> concepts = new ArrayList<Concept>();
		List<AgentType> agents = new ArrayList<AgentType>();
		List<TimeSpanType> timespans = new ArrayList<TimeSpanType>();
		List<PlaceType> places = new ArrayList<PlaceType>();
		Concept lastConcept = null;
		AgentType lastAgent = null;
		TimeSpanType lastTimespan = null;
		PlaceType lastPlace = null;
		if (controlledVocabulary != null) {
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
								

								if (sElem.getAttributes().hasNext()) {
									Attribute attr = (Attribute) sElem
											.getAttributes().next();
									String attribute = attr.getName()
											.getPrefix()

									+ ":" + attr.getName().getLocalPart();
									// Is the attribute mapped?
									if (isMapped(element + "_" + attribute)) {

										String attrVal = attr.getValue();
										String elem = null;
										if (xml.peek().isCharacters()) {
											elem = xml.nextEvent()
													.asCharacters().getData();
										}

										if (StringUtils.equals(
												getEdmLabel(element + "_"
														+ attribute),
												"skos_concept")) {
											if (lastConcept != null) {
												concepts.add(lastConcept);
											}

											lastConcept = createNewEntity(
													Concept.class, attrVal);
										} else

										if (StringUtils.equals(
												getEdmLabel(element + "_"
														+ attribute),
												"edm_agent")) {
											if (lastAgent != null) {
												agents.add(lastAgent);
											}

											lastAgent = createNewEntity(
													AgentType.class, attrVal);
										} else

										if (StringUtils.equals(
												getEdmLabel(element + "_"
														+ attribute),
												"edm_timespan")) {
											if (lastTimespan != null) {
												timespans.add(lastTimespan);
											}

											lastTimespan = createNewEntity(
													TimeSpanType.class, attrVal);
										} else

										if (StringUtils.equals(
												getEdmLabel(element + "_"
														+ attribute),
												"edm_place")) {
											if (lastPlace != null) {
												places.add(lastPlace);
											}
											lastPlace = createNewEntity(
													PlaceType.class, attrVal);
										} else {
											if (StringUtils.startsWith(
													getEdmLabel(element + "_"
															+ attribute), "cc")) {

												appendConceptValue(lastConcept,
														getEdmLabel(element),
														elem,
														getEdmLabel(element
																+ "_"
																+ attribute),
														attrVal);
											}

											else if (StringUtils.startsWith(
													getEdmLabel(element + "_"
															+ attribute), "ts")) {

												appendValue(TimeSpanType.class,
														lastTimespan,
														getEdmLabel(element),
														elem,
														getEdmLabel(element
																+ "_"
																+ attribute),
														attrVal);
											} else if (StringUtils.startsWith(
													getEdmLabel(element + "_"
															+ attribute), "ag")) {

												appendValue(AgentType.class,
														lastAgent,
														getEdmLabel(element),
														elem,
														getEdmLabel(element
																+ "_"
																+ attribute),
														attrVal);
											} else if (StringUtils.startsWith(
													getEdmLabel(element + "_"
															+ attribute), "pl")) {

												appendValue(PlaceType.class,
														lastPlace,
														getEdmLabel(element),
														elem,
														getEdmLabel(element
																+ "_"
																+ attribute),
														attrVal);
											}
										}

									}
									// Since the attribute is not mapped
									else {

										if (StringUtils.equals(
												getEdmLabel(element),
												"skos_concept")) {
											if (lastConcept != null) {
												concepts.add(lastConcept);
											}
											lastConcept = createNewEntity(
													Concept.class,
													xml.getElementText());

										} else if (StringUtils.equals(
												getEdmLabel(element),
												"edm_agent")) {
											if (lastAgent != null) {
												agents.add(lastAgent);
											}
											lastAgent = createNewEntity(
													AgentType.class,
													xml.getElementText());

										} else if (StringUtils.equals(
												getEdmLabel(element),
												"edm_timespan")) {
											if (lastTimespan != null) {
												timespans.add(lastTimespan);
											}
											lastTimespan = createNewEntity(
													TimeSpanType.class,
													xml.getElementText());

										} else if (StringUtils.equals(
												getEdmLabel(element),
												"edm_place")) {
											if (lastPlace != null) {
												places.add(lastPlace);
											}
											lastPlace = createNewEntity(
													PlaceType.class,
													xml.getElementText());

										}
										if (StringUtils.startsWith(
												getEdmLabel(element), "cc")) {
											appendConceptValue(lastConcept,
													getEdmLabel(element),
													xml.getElementText(), null,
													null);
										}

										else if (StringUtils.startsWith(
												getEdmLabel(element), "ts")) {
											appendValue(TimeSpanType.class,
													lastTimespan,
													getEdmLabel(element),
													xml.getElementText(), null,
													null);
										} else if (StringUtils.startsWith(
												getEdmLabel(element), "ag")) {
											appendValue(AgentType.class,
													lastAgent,
													getEdmLabel(element),
													xml.getElementText(), null,
													null);
										} else if (StringUtils.startsWith(
												getEdmLabel(element), "pl")) {
											appendValue(PlaceType.class,
													lastPlace,
													getEdmLabel(element),
													xml.getElementText(), null,
													null);

										}
									}

								}
								// Since it does not have attributes
								else {
									XMLEvent evt2 = xml.nextEvent();
									if (evt2.isCharacters()) {
										if (StringUtils.equals(
												getEdmLabel(element),
												"skos_concept")) {
											if (lastConcept != null) {
												concepts.add(lastConcept);
											}
											lastConcept = createNewEntity(
													Concept.class, evt2
															.asCharacters()
															.getData());

										} else if (StringUtils.equals(
												getEdmLabel(element),
												"edm_agent")) {
											if (lastAgent != null) {
												agents.add(lastAgent);
											}
											lastAgent = createNewEntity(
													AgentType.class, evt2
															.asCharacters()
															.getData());

										} else if (StringUtils.equals(
												getEdmLabel(element),
												"edm_timespan")) {
											if (lastTimespan != null) {
												timespans.add(lastTimespan);
											}
											lastTimespan = createNewEntity(
													TimeSpanType.class, evt2
															.asCharacters()
															.getData());

										} else if (StringUtils.equals(
												getEdmLabel(element),
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
												getEdmLabel(element), "cc")) {
											appendConceptValue(lastConcept,
													getEdmLabel(element), evt2
															.asCharacters()
															.getData(), null,
													null);
										}

										else if (StringUtils.startsWith(
												getEdmLabel(element), "ts")) {
											appendValue(TimeSpanType.class,
													lastTimespan,

													getEdmLabel(element), evt2
															.asCharacters()
															.getData(), null,
													null);
										} else if (StringUtils.startsWith(
												getEdmLabel(element), "ag")) {
											appendValue(AgentType.class,
													lastAgent,
													getEdmLabel(element), evt2
															.asCharacters()
															.getData(), null,
													null);
										} else if (StringUtils.startsWith(
												getEdmLabel(element), "pl")) {
											appendValue(PlaceType.class,
													lastPlace,
													getEdmLabel(element), evt2
															.asCharacters()
															.getData(), null,
													null);
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

										if (StringUtils.equals(
												getEdmLabel(element + "_"
														+ attribute),
												"skos_concept")) {
											if (lastConcept != null) {
												concepts.add(lastConcept);
											}

											lastConcept = createNewEntity(
													Concept.class,
													attr.getValue());
										} else

										if (StringUtils.equals(
												getEdmLabel(element + "_"
														+ attribute),
												"edm_agent")) {
											if (lastAgent != null) {
												agents.add(lastAgent);
											}

											lastAgent = createNewEntity(
													AgentType.class,
													attr.getValue());
										} else

										if (StringUtils.equals(
												getEdmLabel(element + "_"
														+ attribute),
												"edm_timespan")) {
											if (lastTimespan != null) {
												timespans.add(lastTimespan);
											}

											lastTimespan = createNewEntity(
													TimeSpanType.class,
													attr.getValue());
										} else

										if (StringUtils.equals(
												getEdmLabel(element + "_"
														+ attribute),
												"edm_place")) {
											if (lastPlace != null) {
												places.add(lastPlace);
											}
											lastPlace = createNewEntity(
													PlaceType.class,
													attr.getValue());
										} else {
											if (StringUtils.startsWith(
													getEdmLabel(element + "_"
															+ attribute), "cc")) {
												String elem = null;
												if (xml.peek().isCharacters()) {
													elem = xml.nextEvent()
															.asCharacters()
															.getData();
												}
												String attrVal = attr
														.getValue();

												appendConceptValue(lastConcept,
														getEdmLabel(element),
														elem,
														getEdmLabel(element
																+ "_"
																+ attribute),
														attrVal);
											}

											else if (StringUtils.startsWith(
													getEdmLabel(element + "_"
															+ attribute), "ts")) {
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
														getEdmLabel(element),
														elem,
														getEdmLabel(element
																+ "_"
																+ attribute),
														attrVal);
											} else if (StringUtils.startsWith(
													getEdmLabel(element + "_"
															+ attribute), "ag")) {
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
														getEdmLabel(element),
														elem,
														getEdmLabel(element
																+ "_"
																+ attribute),
														attrVal);
											} else if (StringUtils.startsWith(
													getEdmLabel(element + "_"
															+ attribute), "pl")) {
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
														getEdmLabel(element),
														elem,
														getEdmLabel(element
																+ "_"
																+ attribute),
														attrVal);
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
			}
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

	private <T> T appendValue(Class<T> clazz, T obj, String edmLabel,
			String val, String edmAttr, String valAttr)
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

			List lst = mthd.invoke(obj) != null ? (ArrayList) mthd.invoke(obj)
					: new ArrayList();
			if (RDF.getClazz().getSuperclass()
					.isAssignableFrom(ResourceType.class)) {

				ResourceType rs = new ResourceType();
				rs.setResource(val);
				lst.add(rs);

			} else if (RDF.getClazz().getSuperclass()
					.isAssignableFrom(ResourceOrLiteralType.class)) {
				ResourceOrLiteralType rs = new ResourceOrLiteralType();
				if (isURI(val)) {
					rs.setResource(val);
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
				lst.add(rs);
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
				lst.add(rs);
			}

			Class<?>[] cls = new Class<?>[1];
			cls[0] = clazz;
			Method method = obj.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(obj, lst);
		} else {
			if (RDF.getClazz().isAssignableFrom(ResourceType.class)) {
				ResourceType rs = new ResourceType();
				rs.setResource(val);
				Class<?>[] cls = new Class<?>[1];
				cls[0] = RDF.getClazz();
				Method method = obj.getClass().getMethod(
						StringUtils.replace(RDF.getMethodName(), "get", "set"),
						cls);
				method.invoke(obj, RDF.returnObject(RDF.getClazz(), rs));
			} else if (RDF.getClazz().isAssignableFrom(LiteralType.class)) {
				LiteralType rs = new LiteralType();
				rs.setString(val);
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
			String val, String edmAttr, String valAttr)
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
			obj.setResource(val);
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

	private static boolean isURI(String uri) {

		try {
			new URL(uri);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}

	}

	public void setMappedField(String fieldToMap, EdmLabel europeanaField) {
		HashMap<String, EdmLabel> elements = vocabulary.getElements() != null ? vocabulary
				.getElements() : new HashMap<String, EdmLabel>();
		elements.put(fieldToMap, europeanaField);
		vocabulary.setElements(elements);

	}

	public String getMappedField(EdmLabel europeanaField) {
		for (String key : vocabulary.getElements().keySet()) {
			if (europeanaField.equals(vocabulary.getElements().get(key))) {
				return key;
			}
		}
		return null;
	}
}
