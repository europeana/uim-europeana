package eu.europeana.uim.enrichment;

import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice;
import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.entity.AggregationImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.corelib.solr.utils.EseEdmMap;
import eu.europeana.corelib.tools.utils.HashUtils;
import eu.europeana.corelib.tools.utils.PreSipCreatorUtils;
import eu.europeana.corelib.tools.utils.SipCreatorUtils;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.utils.PropertyReader;
import eu.europeana.uim.enrichment.utils.UimConfigurationProperty;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.store.MetaDataRecord;

public class LookupCreationPlugin<I> extends
		AbstractIngestionPlugin<MetaDataRecord<I>, I> {

	public LookupCreationPlugin(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	public LookupCreationPlugin() {
		super("", "");
		// TODO Auto-generated constructor stub
	}

	private static String repository = PropertyReader
			.getProperty(UimConfigurationProperty.UIM_REPOSITORY);
	private static String mongoDB;
	private static String mongoHost = PropertyReader
			.getProperty(UimConfigurationProperty.MONGO_HOSTURL);
	private static String mongoPort = PropertyReader
			.getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
	private static String uname;
	private static String pass;
	private static Mongo mongo;
	private static EnrichmentService enrichmentService;
	private static IBindingFactory bfact;
	static {
		try {
			// Should be placed in a static block for performance reasons
			bfact = BindingDirectory.getFactory(RDF.class);

		} catch (JiBXException e) {
			e.printStackTrace();
		}

	}

	@Override
	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getOptionalFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getParameters() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	@Override
	public int getPreferredThreadCount() {
		// TODO Auto-generated method stub
		return 12;
	}

	@Override
	public int getMaximumThreadCount() {
		// TODO Auto-generated method stub
		return 15;
	}

	@Override
	public boolean process(MetaDataRecord<I> mdr,
			ExecutionContext<MetaDataRecord<I>, I> context)
			throws IngestionPluginFailedException, CorruptedDatasetException {
		IUnmarshallingContext uctx;

		try {

			String value = null;
			if (mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD) != null
					&& mdr.getValues(
							EuropeanaModelRegistry.EDMDEREFERENCEDRECORD)
							.size() > 0) {
				value = mdr.getValues(
						EuropeanaModelRegistry.EDMDEREFERENCEDRECORD).get(0);
			} else {
				value = mdr.getValues(EuropeanaModelRegistry.EDMRECORD).get(0);
			}
			uctx = bfact.createUnmarshallingContext();
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
			FullBeanImpl fullBean = constructFullBeanMock(rdf);
			String collectionId = (String) mdr.getCollection().getMnemonic();
			String fileName;
			String oldCollectionId = enrichmentService
					.getCollectionMongoServer().findOldCollectionId(
							collectionId);
			if (oldCollectionId != null) {
				collectionId = oldCollectionId;
				fileName = oldCollectionId;
			} else {
				fileName = (String) mdr.getCollection().getName();
			}

			String hash = hashExists(collectionId, fileName, fullBean);

			if (StringUtils.isNotEmpty(hash)) {

				enrichmentService.createLookupEntry(fullBean, collectionId,
						hash);
			}
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private FullBeanImpl constructFullBeanMock(RDF rdf) {
		FullBeanImpl fBean = new FullBeanImpl();
		AggregationImpl aggr = new AggregationImpl();
		List<AggregationImpl> aggrs = new ArrayList<AggregationImpl>();
		aggr.setAbout(rdf.getAggregationList().get(0).getAbout());
		aggr.setEdmIsShownAt(rdf.getAggregationList().get(0).getIsShownAt() != null ? rdf
				.getAggregationList().get(0).getIsShownAt().getResource()
				: null);
		aggr.setEdmIsShownBy(rdf.getAggregationList().get(0).getIsShownBy() != null ? rdf
				.getAggregationList().get(0).getIsShownBy().getResource()
				: null);
		aggr.setEdmObject(rdf.getAggregationList().get(0).getObject() != null ? rdf
				.getAggregationList().get(0).getObject().getResource()
				: null);
		aggrs.add(aggr);
		List<ProxyImpl> proxies = new ArrayList<ProxyImpl>();
		ProxyImpl proxy = new ProxyImpl();
		Map<String, List<String>> dcIdentifiers = new HashMap<String, List<String>>();
		ProxyType proxyRDF = findProxy(rdf);
		if (proxyRDF != null) {
			List<Choice> choices = proxyRDF.getChoiceList();
			List<String> val = new ArrayList<String>();
			for (Choice choice : choices) {
				if (choice.ifIdentifier()) {
					val.add(choice.getIdentifier().getString());
				}
			}
			dcIdentifiers.put("def", val);
			proxy.setDcIdentifier(dcIdentifiers);
			proxies.add(proxy);
			fBean.setProxies(proxies);
		}
		fBean.setAggregations(aggrs);
		return fBean;
	}

	private ProxyType findProxy(RDF rdf) {
		for (ProxyType proxy : rdf.getProxyList()) {
			if (proxy.getEuropeanaProxy() != null) {
				if (!proxy.getEuropeanaProxy().isEuropeanaProxy()) {
					return proxy;
				}
			}
		}
		return null;
	}

	@Override
	public void initialize(ExecutionContext<MetaDataRecord<I>, I> context)
			throws IngestionPluginFailedException {
		try {
			mongo = new Mongo(mongoHost, Integer.parseInt(mongoPort));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mongoDB = enrichmentService.getMongoDB();
		uname = PropertyReader
				.getProperty(UimConfigurationProperty.MONGO_USERNAME) != null ? PropertyReader
				.getProperty(UimConfigurationProperty.MONGO_USERNAME) : "";
		pass = PropertyReader
				.getProperty(UimConfigurationProperty.MONGO_PASSWORD) != null ? PropertyReader
				.getProperty(UimConfigurationProperty.MONGO_PASSWORD) : "";

	}

	@Override
	public void completed(ExecutionContext<MetaDataRecord<I>, I> context)
			throws IngestionPluginFailedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	private String hashExists(String collectionId, String fileName,
			FullBean fullBean) {
		SipCreatorUtils sipCreatorUtils = new SipCreatorUtils();
		sipCreatorUtils.setRepository(repository);
		String hashField = sipCreatorUtils.getHashField(fileName, fileName);
		if (hashField != null) {
			return HashUtils.createHash(EseEdmMap.getEseEdmMap(
					StringUtils.contains(hashField, "[") ? StringUtils
							.substringBefore(hashField, "[") : hashField)
					.getEdmValue(fullBean));
		}
		PreSipCreatorUtils preSipCreatorUtils = new PreSipCreatorUtils();
		preSipCreatorUtils.setRepository(repository);
		if (preSipCreatorUtils.getHashField(fileName, fileName) != null) {
			return HashUtils.createHash(EseEdmMap.getEseEdmMap(
					preSipCreatorUtils.getHashField(collectionId, fileName))
					.getEdmValue(fullBean));
		}
		return null;
	}

	public EnrichmentService getEnrichmentService() {
		return enrichmentService;
	}

	public void setEnrichmentService(EnrichmentService enrichmentService) {
		LookupCreationPlugin.enrichmentService = enrichmentService;
	}
}
