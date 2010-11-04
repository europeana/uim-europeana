package eu.europeana.uim.oaipmh.command;

import java.io.FileNotFoundException;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.osgi.service.command.CommandSession;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.common.parse.XMLStreamParserException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Request;

@Command(name = "uim", scope = "oaipmh")
public class UIMOaiPmh implements Action {

	private enum Format {
		ESE,
		TEL,
		DCX
	}

	@Option(name="-c",aliases={"--collection"},required=true)
	private String collection;

	@Option(name="-f",aliases={"--format"})
	private Format format = Format.ESE;

	private Registry registry;

	public UIMOaiPmh() {
	}

	@Autowired
	public UIMOaiPmh(Registry registry) {
		this.registry = registry;
	}


	@Override
	public Object execute(CommandSession session) throws Exception {
		StorageEngine storage = registry.getActiveStorage();
		Collection targetcoll = storage.findCollection(collection);
		if (targetcoll != null) {
			long[] ids = harvest(storage, targetcoll, session);
			if (ids == null) {
				session.getConsole().println("Failed to read any records from: <" + targetcoll.getOaiBaseUrl() + ">");
			} else {
				session.getConsole().println("Read " + ids.length + " records from: <" + targetcoll.getOaiBaseUrl() + ">");
			}
		} else {
			session.getConsole().println("Collection: <" + collection + "> not found.");
		}
		return null;
	}



	/** 
	 * 
	 * @param storage
	 * @param targetcoll
	 * @param commandSession
	 * @return
	 * @throws XMLStreamParserException
	 * @throws FileNotFoundException
	 * @throws StorageEngineException
	 */
	public long[] harvest(StorageEngine storage, Collection targetcoll, CommandSession commandSession) throws XMLStreamParserException, FileNotFoundException, StorageEngineException {
		Request request = storage.createRequest(targetcoll);
		storage.updateRequest(request);
		
		//			RecordLoader reader = new RecordLoader();
		//			switch (format) {
		//				case ESE:
		//				default: return reader.doEseImport(f, storage, request, new ConsoleProgressMonitor(commandSession));
		//			}
		return null;
	}



	/**
	 * @return the registry
	 */
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * @param registry the registry to set
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}



}
