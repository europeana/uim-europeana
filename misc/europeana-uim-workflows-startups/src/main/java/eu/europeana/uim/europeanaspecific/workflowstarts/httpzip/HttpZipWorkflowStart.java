/**
 * 
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.httpzip;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jibx.runtime.JiBXException;

import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.edmcore.definitions.RDF;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.model.europeanaspecific.utils.DefUtils;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.UimDataSet;
import eu.europeana.uim.workflow.AbstractWorkflowStart;
import eu.europeana.uim.workflow.TaskCreator;
import eu.europeana.uim.workflow.WorkflowStartFailedException;

/**
 * @author Georgios Markakis
 * 
 */
public class HttpZipWorkflowStart extends AbstractWorkflowStart {

	/** Property which allows to overwrite base url from collection/provider */
	public static final String httpzipurl = "http.overwrite.zip.baseUrl";

	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add(httpzipurl);
		}
	};

	private HttpRetriever retriever;

	/**
	 * @param name
	 * @param description
	 */
	public HttpZipWorkflowStart(String name, String description) {
		super(name, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.workflow.WorkflowStart#getParameters()
	 */
	@Override
	public List<String> getParameters() {
		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.workflow.WorkflowStart#getPreferredThreadCount()
	 */
	@Override
	public int getPreferredThreadCount() {
		return 5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.workflow.WorkflowStart#getMaximumThreadCount()
	 */
	@Override
	public int getMaximumThreadCount() {
		return 5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#createLoader(eu.europeana.uim
	 * .api.ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> TaskCreator<I> createLoader(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#isFinished(eu.europeana.uim.api
	 * .ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> boolean isFinished(ExecutionContext<I> context,
			StorageEngine<I> storage) {
		return retriever.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#initialize(eu.europeana.uim.api
	 * .ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> void initialize(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {

		UimDataSet<I> dataset = context.getDataSet();
		if (dataset instanceof Collection) {
			
			Collection<I> collection = (Collection<I>)dataset;
			URL url = null;
			String httpzipurlprop = context.getProperties().getProperty(httpzipurl);
			
			
			try {
				
				if(httpzipurlprop != null){
					url = new URL(httpzipurlprop);
				}
				else{
					url = new URL(collection.getValue(ControlledVocabularyProxy.MINTPUBLICATIONLOCATION));
				}

				this.retriever = HttpRetriever.createInstance(url);

				while (retriever.hasNext()) {

					String rdfstring = retriever.next();

					RDF validedmrecord = DefUtils.unmarshallObject(rdfstring,
							RDF.class);

					I uuid = (I) validedmrecord.getChoiceList().get(0)
							.getProvidedCHO().getAbout();

					MetaDataRecord<I> mdr = storage.getMetaDataRecord(uuid);

					if (mdr == null) {
						mdr = storage.createMetaDataRecord(collection,uuid.toString());
					}

					mdr.addValue(EuropeanaModelRegistry.UIMINGESTIONDATE,
							new Date().toString());
					mdr.addValue(EuropeanaModelRegistry.EDMRECORD, rdfstring);
					
					storage.updateMetaDataRecord(mdr);

				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Retriever exception
				e.printStackTrace();
			} catch (StorageEngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JiBXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (dataset instanceof Request) {
			throw new IllegalArgumentException(
					"A request cannot be the basis for a new import.");
		} else {
			throw new IllegalStateException("Unsupported dataset <"
					+ context.getDataSet() + ">");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#completed(eu.europeana.uim.api
	 * .ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> void completed(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#getTotalSize(eu.europeana.uim
	 * .api.ExecutionContext)
	 */
	@Override
	public <I> int getTotalSize(ExecutionContext<I> context) {
		return retriever.getNumber_of_recs().intValue();
	}

}
