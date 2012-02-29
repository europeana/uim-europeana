/**
 * 
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.httpzip;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import eu.europeana.uim.model.europeanaspecific.utils.DefUtils;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.workflow.AbstractWorkflowStart;
import eu.europeana.uim.workflow.TaskCreator;
import eu.europeana.uim.workflow.WorkflowStartFailedException;

/**
 * @author Georgios Markakis
 *
 */
public class HttpZipWorkflowStart extends AbstractWorkflowStart {


	private HttpRetriever retriever;
	
	/**
	 * @param name
	 * @param description
	 */
	public HttpZipWorkflowStart(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.WorkflowStart#getParameters()
	 */
	@Override
	public List<String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.WorkflowStart#getPreferredThreadCount()
	 */
	@Override
	public int getPreferredThreadCount() {
		return 5;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.WorkflowStart#getMaximumThreadCount()
	 */
	@Override
	public int getMaximumThreadCount() {
		return 5;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.WorkflowStart#createLoader(eu.europeana.uim.api.ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> TaskCreator<I> createLoader(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.WorkflowStart#isFinished(eu.europeana.uim.api.ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> boolean isFinished(ExecutionContext<I> context,
			StorageEngine<I> storage) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.WorkflowStart#initialize(eu.europeana.uim.api.ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> void initialize(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {

		
		try {
			URL url = new URL("ds");
			this.retriever = HttpRetriever.createInstance(url); 
			
			while(retriever.hasNext()){
				
				String rdfstring = retriever.next();
				
				RDF validedmrecord = DefUtils.unmarshallObject(rdfstring,RDF.class);
				
				I uuid = (I) validedmrecord.getChoiceList().get(0).getProvidedCHO().getAbout();

				MetaDataRecord<I> mdr = storage.getMetaDataRecord(uuid);
				
				if(mdr == null){
					mdr = storage.createMetaDataRecord((Collection<I>)context.getDataSet(),uuid.toString());
				}
						
				mdr.addValue(EuropeanaModelRegistry.UIMINGESTIONDATE, new Date().toString());
				mdr.addValue(EuropeanaModelRegistry.EDMRECORD, rdfstring);

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
		
		

		
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.WorkflowStart#completed(eu.europeana.uim.api.ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> void completed(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.WorkflowStart#getTotalSize(eu.europeana.uim.api.ExecutionContext)
	 */
	@Override
	public <I> int getTotalSize(ExecutionContext<I> context) {
		// TODO Auto-generated method stub
		return 0;
	}

}
