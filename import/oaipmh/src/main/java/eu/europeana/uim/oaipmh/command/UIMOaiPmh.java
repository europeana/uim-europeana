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
		DCX
	}

	@Argument()
	private String baseUrl;

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
		long[] ids = execute(storage, session);
		if (ids == null) {
			session.getConsole().println("Failed to read any records from: <" + baseUrl + ">");
		} else {
			session.getConsole().println("Read " + ids.length + " records from: <" + baseUrl + ">");
		}
		return null;
	}

	
	

	/** Method which uses the specified fields (filename, format, collection) to actually 
	 * call the doImport method and read the content fromt the file into the defined storage.
	 * 
	 * Some information is printed to the provided print stream.
	 * 
	 * @param storage storage engines which is the target of the file content
	 * @param out stream to which log/info output goes
	 * @return 
	 * @throws FileNotFoundException 
	 * @throws StorageEngineException 
	 * @throws Exception
	 */
	public long[] execute(StorageEngine storage, CommandSession commandSession) throws XMLStreamParserException, FileNotFoundException, StorageEngineException {
		Collection targetcoll = storage.findCollection(collection);
		if (targetcoll != null) {
			Request request = storage.createRequest(targetcoll);
			storage.updateRequest(request);
//			RecordLoader reader = new RecordLoader();
//			switch (format) {
//				case ESE:
//				default: return reader.doEseImport(f, storage, request, new ConsoleProgressMonitor(commandSession));
//			}
		} else {
			commandSession.getConsole().println("Collection: <" + collection + "> not found.");
		}
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
