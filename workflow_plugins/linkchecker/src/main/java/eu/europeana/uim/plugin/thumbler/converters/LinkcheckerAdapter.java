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
import eu.europeana.uim.store.UimDataSet;

/**
 * Adapter used for Tel's implementation of LinkChecker plugin
 * @param <I>
 */
public class LinkcheckerAdapter <U extends UimDataSet<I>, I> implements UimDatasetAdapter<U,I>{

	private static String pluginIdentifier = new LinkCheckIngestionPlugin<String>().getIdentifier();
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.adapter.UimDatasetAdapter#adapt(eu.europeana.uim.store.UimDataSet)
	 */
	@Override
	public U adapt(U ds) {
		
		if(ds instanceof MetaDataRecord){
		
	    MetaDataRecord<?> mdr = (MetaDataRecord<?>) ds;
		//ds = (MetaDataRecord<?>) ds;
		
        // Adapter that ensures compatibility with the europeana datamodel
        Map<TKey<?, ?>, QValueAdapterStrategy<?, ?, ?, ?>> strategies = new HashMap<TKey<?, ?>, QValueAdapterStrategy<?, ?, ?, ?>>();

        strategies.put(ObjectModelRegistry.LINK, new EuropeanaLinkAdapterStrategy());

        MetadataRecordAdapter<String, QValueAdapterStrategy<?, ?, ?, ?>> mdrad = (MetadataRecordAdapter<String, QValueAdapterStrategy<?, ?, ?, ?>>) AdapterFactory.getAdapter(
        		mdr, strategies);
        
        return (U) mdrad;
		}
		return ds;
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
	public U unadapt(U ds) {
		
		if(ds instanceof MetaDataRecord){
		@SuppressWarnings("unchecked")
		MetadataRecordAdapter<String, QValueAdapterStrategy<?, ?, ?, ?>> mdrad = (MetadataRecordAdapter<String, QValueAdapterStrategy<?, ?, ?, ?>>) ds;
		
		ds = (U) mdrad.getAdaptedRecord();
		//return mdrad.getAdaptedRecord();
		}
		
		return ds;
	}

}
