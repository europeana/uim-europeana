package eu.europeana.uim.enrichment.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

/**
 * Singleton implementation of the Solr InputDocument List
 * 
 * @author Yorgos.Mamakis@ kb.nl
 * 
 */
public class SolrList implements Runnable {

//	public static SolrList solrList;
//	private final static int MAX_QUEUE_SIZE = 1000;
	private ArrayBlockingQueue<SolrInputDocument> queue;
	public enum State{WORKING,FINISHED};
	SolrServer server;
	private State state;
	
	public SolrList(ArrayBlockingQueue<SolrInputDocument> queue,SolrServer server) {
		this.queue = queue;
		this.server = server;
		this.state=State.WORKING;
	}

	public void setState(State state){
		this.state = state;
	}
	
//	/**
//	 * Get the current queue of documents
//	 * 
//	 * @return
//	 */
//	public ArrayBlockingQueue<SolrInputDocument> getQueue() {
//		return queue;
//	}
//
//	/**
//	 * Add a document to a queue by saving first if the size exceeds the
//	 * MAX_QUEUE_SIZE
//	 * 
//	 * @param server
//	 * @param doc
//	 * @throws SolrServerException
//	 * @throws IOException
//	 */
//	public void addToQueue(, SolrInputDocument doc)
//			throws SolrServerException, IOException {
//		try {
//			
//
//			if (queue.size() == MAX_QUEUE_SIZE) {
//				server.add(queue);
//				queue.clear();
//				
//			}
//			queue.put(doc);
//		} catch (InterruptedException e) {
//			server.add(queue);
//			queue.clear();
//		}
//
//	}
//
//	/**
//	 * Get a single instance of the SolrList
//	 * 
//	 * @return
//	 */
//	public static SolrList getInstance() {
//		if (solrList == null) {
//			solrList = new SolrList();
//		}
//		return solrList;
//	}

	@Override
	public void run() {
		while(this.state == State.WORKING){
			if(queue.size()==1000){
				save();
			}
		}
		if(this.state==State.FINISHED){
			save();
		}
	}
	
	private void save(){
		try {
			server.add(queue);
			queue.clear();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
