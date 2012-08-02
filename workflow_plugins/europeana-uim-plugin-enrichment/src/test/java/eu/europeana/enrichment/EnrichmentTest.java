package eu.europeana.enrichment;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.solr.common.SolrInputDocument;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.junit.Ignore;
import org.junit.Test;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.solr.utils.SolrConstructor;
import eu.europeana.uim.enrichment.utils.EuropeanaEnrichmentTagger;


public class EnrichmentTest {
	private static final String RECORD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<rdf:RDF xmlns:dcterms=\"http://purl.org/dc/terms/\""
			+ "xmlns:edm=\"http://www.europeana.eu/schemas/edm/\""
			+ "xmlns:enrichment=\"http://www.europeana.eu/schemas/edm/enrichment/\""
			+ "xmlns:owl=\"http://www.w3.org/2002/07/owl#\""
			+ "xmlns:wgs84=\"http://www.w3.org/2003/01/geo/wgs84_pos#\""
			+ "xmlns:skos=\"http://www.w3.org/2004/02/skos/core\""
			+ "xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\""
			+ "xmlns:ore=\"http://www.openarchives.org/ore/terms/\""
			+ "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
			+ "xmlns:dc=\"http://purl.org/dc/elements/1.1/\""
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ "xsi:schemaLocation=\"http://www.w3.org/1999/02/22-rdf-syntax-ns# EDM.xsd\">"
			+ "<edm:ProvidedCHO rdf:about=\"#GNM:728331\">"
			+ "<dc:identifier>#GNM:728331</dc:identifier>"
			+ "<dc:coverage></dc:coverage>"
			+ "<dc:description xml:lang=\"de\">Stoßmechanik.</dc:description>"
			+ "<dc:title xml:lang=\"de\">Modell einer primitiven Stoßmechanik</dc:title>"
			+ "<edm:type>IMAGE</edm:type>"
			+ "</edm:ProvidedCHO>"
			+ "<edm:WebResource rdf:about=\"http://www.mimo-db.eu/media/GNM/IMAGE/MINe298_H_1304604690701_2.jpg\">"
			+ "<edm:rights rdf:resource=\"http://creativecommons.org/licenses/by-nc-sa/3.0/\"></edm:rights>"
			+ "</edm:WebResource>"
			+ "<ore:Aggregation rdf:about=\"http://www.mimo-db.eu/GNM/728331\">"
			+ "<edm:aggregatedCHO rdf:resource=\"#GNM:728331\"></edm:aggregatedCHO>"
			+ "<edm:hasView rdf:resource=\"http://www.mimo-db.eu/media/GNM/IMAGE/MINe298_H_1304604690701_2.jpg\"></edm:hasView>"
			+ "<edm:dataProvider>Germanisches Nationalmuseum</edm:dataProvider>"
			+ "<edm:provider>MIMO - Musical Instrument Museums Online</edm:provider>"
			+ "<edm:rights rdf:resource=\"http://creativecommons.org/licenses/by-nc-sa/3.0/\"></edm:rights>"
			+ "<edm:isShownBy rdf:resource=\"http://www.mimo-db.eu/media/GNM/IMAGE/MINe298_H_1304604690701_2.jpg\"></edm:isShownBy>"
			+ "<edm:isShownAt rdf:resource=\"http://www.mimo-db.eu/GNM/728331\"></edm:isShownAt>"
			+ "<edm:object rdf:resource=\"http://www.mimo-db.eu/media/GNM/IMAGE/MINe298_H_1304604690701_2.jpg\"></edm:object>"
			+ "</ore:Aggregation>" + "</rdf:RDF>";
	
	@SuppressWarnings("unchecked")
	@Ignore
	@Test
	public void testAnnocultorEnrichment(){
		EuropeanaEnrichmentTagger tagger = new EuropeanaEnrichmentTagger();
		try {
			tagger.init("Europeana");
			IBindingFactory bfact = BindingDirectory.getFactory(RDF.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(RECORD));
			SolrInputDocument solrInputDocument = tagger
					.tagDocument(SolrConstructor.constructSolrDocument(rdf));
			StringBuilder sb = new StringBuilder();
			
			for (String fieldName : solrInputDocument.getFieldNames()){
				sb.append("Field name: ");
				sb.append(fieldName);
				sb.append(" | ");
				Object fieldValue = solrInputDocument.getFieldValue(fieldName);
				if (fieldValue!=null){
					if(fieldValue instanceof ArrayList){
						for (String value : (ArrayList<String>) fieldValue){
							sb.append(value);
							sb.append(" | ");
						}
						
					}
					else {
						ClassType clt = ClassType
								.getClassType(fieldValue.getClass().getCanonicalName());
						sb.append(clt.toString(fieldValue));
						sb.append(" | ");
					}
				}
				sb.append("\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private enum ClassType {
		java_lang_Integer() {
			@Override
			public String toString(Object fieldValue) {

				return Integer.toString((Integer) fieldValue);
			}
		},
		java_lang_Float() {
			@Override
			public String toString(Object fieldValue) {
				return Float.toString((Float) fieldValue);
			}
		},
		java_lang_String() {
			@Override
			public String toString(Object fieldValue) {

				return (String) fieldValue;
			}
		},
		java_lang_Boolean() {
			@Override
			public String toString(Object fieldValue) {

				return Boolean.toString((Boolean) fieldValue);
			}
		},
		java_util_Date() {
			@Override
			public String toString(Object fieldValue) {
				return DateFormatUtils.formatUTC((Date) fieldValue, "yyyy-MM-dd", new Locale("en"));
				
			}
		}
		;

		public static ClassType getClassType(String value){
			return ClassType.valueOf(StringUtils.replace(value,".","_"));
		}
		

		public abstract String toString(Object fieldValue);
	}

}
