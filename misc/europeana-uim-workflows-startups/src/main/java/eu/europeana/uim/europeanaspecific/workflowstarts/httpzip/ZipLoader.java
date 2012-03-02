/**
 * 
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.httpzip;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jibx.runtime.JiBXException;

import eu.europeana.uim.api.LoggingEngine;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.edmcore.definitions.RDF;
import eu.europeana.uim.europeanaspecific.workflowstarts.oaipmh.OaiPmhRecord;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.utils.DefUtils;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.Request;

/**
 * @author geomark
 *
 */
public class ZipLoader {

    @SuppressWarnings("rawtypes")
    private final StorageEngine          storage;
    @SuppressWarnings("unused")
    private LoggingEngine<?>             loggingEngine;
    @SuppressWarnings("rawtypes")
    private final Request                request;
    private final ProgressMonitor        monitor;
    private final Iterator<String> zipiterator;
    
	public <I> ZipLoader(Iterator<String> zipiterator, StorageEngine<I> storage, Request<I> request, 
    		ProgressMonitor monitor, LoggingEngine<I> loggingEngine) {
        super();
        this.zipiterator = zipiterator;
        this.storage = storage;
        this.request = request;
        this.monitor = monitor;
        this.loggingEngine = loggingEngine;
	}
	
    /**
     * @param <I>
     * @param batchSize
     *            number of loaded records
     * @param save
     *            Should they be saved to the index?
     * @return list of loaded MARC records
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public synchronized <I> List<MetaDataRecord<I>> doNext(int batchSize, boolean save) {
        List<MetaDataRecord<I>> result = new ArrayList<MetaDataRecord<I>>();
        int progress = 0;
    	
		while (zipiterator.hasNext()) {

            if (progress >= batchSize) {
                break;
            }
			
			String rdfstring = zipiterator.next();

			RDF validedmrecord;
			try {
				validedmrecord = DefUtils.unmarshallObject(rdfstring,
						RDF.class);
				I uuid = (I) validedmrecord.getChoiceList().get(0)
						.getProvidedCHO().getAbout();

				MetaDataRecord<I> mdr = storage.getMetaDataRecord(uuid);

				if (mdr == null) {
					mdr = storage.createMetaDataRecord(request.getCollection(),uuid.toString());
				}

				mdr.addValue(EuropeanaModelRegistry.UIMINGESTIONDATE,
						new Date().toString());
				mdr.addValue(EuropeanaModelRegistry.EDMRECORD, rdfstring);
				
				storage.updateMetaDataRecord(mdr);
				
				result.add(mdr);
                storage.addRequestRecord(request, mdr);

                progress++;
			} catch (JiBXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (StorageEngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	return result;
    }

	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getExpectedRecordCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
