package eu.europeana.uim.deactivation.service;

import java.util.List;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

import eu.europeana.corelib.solr.exceptions.MongoDBException;
import eu.europeana.corelib.solr.server.EdmMongoServer;
import eu.europeana.corelib.solr.server.impl.EdmMongoServerImpl;

public class ExtendedEdmMongoServer extends EdmMongoServerImpl implements EdmMongoServer {

	public ExtendedEdmMongoServer(Mongo mongo, String mongoDB, String username,
			String password) throws MongoDBException {
		super(mongo, mongoDB, username, password);
	}

	/**
	 * Delete a list of objects
	 * 
	 * @param objList
	 */
	public <T> void delete(List<T> objList) {
		for (T obj : objList) {
			delete(obj);
		}
	}

	/**
	 * Delete a single item
	 * 
	 * @param obj
	 */
	public <T> void delete(T obj) {
		this.getDatastore().delete(obj, WriteConcern.FSYNC_SAFE);
	}

}
