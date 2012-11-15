/**
 * 
 */
package eu.europeana.uim.plugin.thumbler.converters;

import java.util.HashMap;
import java.util.Map;

import org.theeuropeanlibrary.model.tel.ObjectModelRegistry;
import org.theeuropeanlibrary.uim.check.weblink.LinkCheckIngestionPlugin;

import eu.europeana.uim.adapter.UimDatasetAdapter;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.adapters.AdapterFactory;
import eu.europeana.uim.model.adapters.MetadataRecordAdapter;
import eu.europeana.uim.model.adapters.QValueAdapterStrategy;
import eu.europeana.uim.model.adapters.europeana.EuropeanaLinkAdapterStrategy;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * Adapter used for Tel's implementation of LinkChecker plugin
 */
public class LinkcheckerAdapter implements UimDatasetAdapter<MetaDataRecord<String>, String>{

	private static String pluginIdentifier = new LinkCheckIngestionPlugin<String>().getIdentifier();
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.adapter.UimDatasetAdapter#adapt(eu.europeana.uim.store.UimDataSet)
	 */
	@Override
	public MetaDataRecord<String> adapt(MetaDataRecord<String> mdr) {
        // Adapter that ensures compatibility with the europeana datamodel
        Map<TKey<?, ?>, QValueAdapterStrategy<?, ?, ?, ?>> strategies = new HashMap<TKey<?, ?>, QValueAdapterStrategy<?, ?, ?, ?>>();

        strategies.put(ObjectModelRegistry.LINK, new EuropeanaLinkAdapterStrategy());

        MetadataRecordAdapter<String, QValueAdapterStrategy<?, ?, ?, ?>> mdrad = AdapterFactory.getAdapter(
                mdr, strategies);
        
		return mdrad;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.adapter.UimDatasetAdapter#getPluginIdentifier()
	 */
	@Override
	public String getPluginIdentifier() {
		return LinkcheckerAdapter.pluginIdentifier;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.adapter.UimDatasetAdapter#unadapt(eu.europeana.uim.store.UimDataSet)
	 */
	@Override
	public MetaDataRecord<String> unadapt(MetaDataRecord<String> mdr) {
		@SuppressWarnings("unchecked")
		MetadataRecordAdapter<String, QValueAdapterStrategy<?, ?, ?, ?>> mdrad = (MetadataRecordAdapter<String, QValueAdapterStrategy<?, ?, ?, ?>>) mdr;
		
		return mdrad.getAdaptedRecord();
	}

}
