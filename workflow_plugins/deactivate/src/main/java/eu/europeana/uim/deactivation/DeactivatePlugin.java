package eu.europeana.uim.deactivation;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.deactivation.service.DeactivationService;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * Collection Deactivation Plugin
 * 
 * @author gmamakis
 * 
 */
public class DeactivatePlugin extends AbstractIngestionPlugin {


	private static DeactivationService dService;

	public DeactivatePlugin(DeactivationService serv, String name,
			String description) {
		super(name, description);
		dService = serv;
	}

	public <I> void completed(ExecutionContext<I> arg0)
			throws IngestionPluginFailedException {
		// TODO Auto-generated method stub

	}

	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMaximumThreadCount() {
		// TODO Auto-generated method stub
		return 5;
	}

	public TKey<?, ?>[] getOptionalFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public TKey<?, ?>[] getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPreferredThreadCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public <I> void initialize(ExecutionContext<I> arg0)
			throws IngestionPluginFailedException {
		@SuppressWarnings("rawtypes")
		Collection coll = (Collection) arg0.getDataSet();
		try {
			dService.getSolrServer().deleteByQuery(
					"europeana_collectionName:" + StringUtils.split(coll.getName(),"_")[0]+"*");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <I> boolean processRecord(MetaDataRecord<I> mdr,
			ExecutionContext<I> arg1) throws IngestionPluginFailedException,
			CorruptedMetadataRecordException {
		// TODO Auto-generated method stub
		IBindingFactory bfact;

		try {
			bfact = BindingDirectory.getFactory(RDF.class);

			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();

			List<String> values = mdr.getValues(
					EuropeanaModelRegistry.EDMDEREFERENCEDRECORD);
			String value = values.get(0);
			// TODO: disable in UIM
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
			FullBean fBean = dService.getMongoServer().getFullBean(
					rdf.getProvidedCHOList().get(0).getAbout());
			dService.getMongoServer().delete(fBean.getAggregations());
			dService.getMongoServer().delete(fBean.getProvidedCHOs());
			dService.getMongoServer().delete(fBean.getProxies());
			dService.getMongoServer().delete(fBean.getEuropeanaAggregation());
			dService.getMongoServer().delete(fBean);
			return true;
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
