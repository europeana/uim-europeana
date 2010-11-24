package eu.europeana.uim.command;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.osgi.service.command.CommandSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


@Command(name = "uim", scope = "store")
public class UIMStore implements Action {

	private static final Logger log = Logger.getLogger(UIMStore.class.getName());
	
	private enum Operation {
		createProvider,
		updateProvider,
		listProvider,
		createCollection,
		updateCollection,
		listCollection,
		loadSampleData
	}

	private Registry registry;

	@Option(name="-o", aliases={"--operation"}, required=true)
	private Operation operation;

	@Option(name="-p", aliases={"--parent"})
	private String parent;

	@Argument(index=0)
	private String argument0;

	@Argument(index=1)
	private String argument1;

	@Argument(index=2)
	private String argument2;


	public UIMStore(Registry registry) {
        this.registry = registry;
	}


	@Override
	public Object execute(CommandSession session) throws Exception {
		StorageEngine storage = registry.getActiveStorage();
		switch(operation) {
		case createProvider: createProvider(storage, session); break;
		case updateProvider: updateProvider(storage, session); break;
		case listProvider: listProvider(storage, session); break;
		case createCollection: createCollection(storage, session); break;
		case updateCollection: updateCollection(storage, session); break;
		case listCollection: listCollection(storage, session); break;
		case loadSampleData: loadSampleData(storage, session); break;
		}
		return null;
	}



	private Provider createProvider(StorageEngine storage, CommandSession session) throws StorageEngineException {
		if (argument0 == null || argument1 == null) {
			session.getConsole().println("Failed to create provider. No arguments specified, should be <mnemonic> <name> [<true|false>]");
			return null;
		}

		Provider provider = storage.createProvider();
		provider.setMnemonic(argument0);
		provider.setName(argument1);
		if (argument2 != null) {
			provider.setAggregator(Boolean.parseBoolean(argument2));
		}

		if (parent != null) {
			Provider pParent = storage.findProvider(parent);

			if (pParent != null) {
				provider.getRelatedIn().add(pParent);
				pParent.getRelatedOut().add(provider);

				storage.updateProvider(provider);
				storage.updateProvider(pParent);
			} else {
				session.getConsole().println("Failed to create provider. Parent <" + parent + "> not found.");

			}
		} else {
			storage.updateProvider(provider);
		}
		return provider;
	}


	private void updateProvider(StorageEngine storage, CommandSession session) throws StorageEngineException {
		if (argument0 == null || argument1 == null || argument2 == null) {
			session.getConsole().println("Failed to update provider. No arguments specified, should be <mnemonic> <field> <value>");
			return;
		}

		Provider provider = storage.findProvider(argument0);

		String method = "set" + StringUtils.capitalize(argument1);
		try {
			Method setter = provider.getClass().getMethod(method, String.class);
			setter.invoke(provider, argument2);

			storage.updateProvider(provider);
		} catch (Throwable e) {
			session.getConsole().println("Failed to update provider. Failed to update using method <"+method+"(" + argument2 + ")");
		}
	}




	private void listProvider(StorageEngine storage, CommandSession session) throws StorageEngineException {
		List<Provider> mainprovs = new ArrayList<Provider>();
		List<Provider> providers = storage.getProvider();
		for (Provider provider : providers) {
			if (provider.getRelatedIn() == null || provider.getRelatedIn().isEmpty()) {
				mainprovs.add(provider);
			}
		}

		HashSet<Provider> processed = new HashSet<Provider>();
		printTree(mainprovs, processed, "+-", session.getConsole());
	}



	private void printTree(List<Provider> providers, Set<Provider> processed, String indent, PrintStream out) {
		for (Provider provider : providers) {

			String p = "(" + provider.getId() + ") " + provider.toString();
			out.println(indent + p);

			if (!processed.contains(provider)) {
				processed.add(provider);
				if (provider.getRelatedOut() != null && !provider.getRelatedOut().isEmpty()) {
					indent = indent.substring(0, indent.length() - 2);
					printTree(provider.getRelatedOut(), processed, indent + "|  +-", out);
				}
			}
		}
	}


	private Collection createCollection(StorageEngine storage, CommandSession session) throws StorageEngineException {
		if (argument0 == null || argument1 == null || parent == null) {
			session.getConsole().println("Failed to create collection. No arguments specified, should be -p provider <mnemonic> <name>");
			return null;
		}

		Provider provider = storage.findProvider(parent);
		if (provider == null) {
			session.getConsole().println("Failed to create collection. Provider \"" + parent + "\" not found.");
			return null;
		}

		Collection collection = storage.createCollection(provider);
		collection.setMnemonic(argument0);
		collection.setName(argument1);
		storage.updateCollection(collection);
		return collection;
	}


	private void updateCollection(StorageEngine storage, CommandSession session) throws StorageEngineException {
		if (argument0 == null || argument1 == null || argument2 == null) {
			session.getConsole().println("Failed to update collection. No arguments specified, should be <mnemonic> <field> <value>");
			return;
		}

		Collection collection = storage.findCollection(argument0);

		String method = "set" + StringUtils.capitalize(argument1);
		try {
			Method setter = collection.getClass().getMethod(method, String.class);
			setter.invoke(collection, argument2);

			storage.updateCollection(collection);
		} catch (Throwable e) {
			session.getConsole().println("Failed to update collection. Failed to update using method <"+method+"(" + argument2 + ")");
		}
	}
	private void listCollection(StorageEngine storage, CommandSession session) throws StorageEngineException {
		if (parent == null) {
			List<Provider> providers = storage.getProvider();
			for (Provider provider : providers) {
				List<Collection> collections = storage.getCollections(provider);
				if (collections != null && !collections.isEmpty()) {
					session.getConsole().println(provider.getMnemonic());
					for (Collection collection : collections) {
						String p = "|  -+" + collection.toString();
						session.getConsole().println(p);
					}
				}
			}
		} else {
			Provider provider = storage.findProvider(parent);
			session.getConsole().println(provider.getMnemonic());

			List<Collection> collections = storage.getCollections(provider);
			for (Collection collection : collections) {
				String p = "|  -+" + collection.toString();
				session.getConsole().println(p);
			}
		}
	}



	private void loadSampleData(StorageEngine storage, CommandSession session) throws StorageEngineException, IOException {
		InputStream stream = UIMStore.class.getResourceAsStream("/sampledata.properties");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		String line = reader.readLine();
		while (line != null) {
			if (line.trim().startsWith("#") ||
					line.trim().length() == 0) {
			} else {
				String[] split = line.split("=");
				if (split[0].startsWith("provider")) {
					setFieldValues(split);
					createProvider(storage, session);
				} else if (split[0].startsWith("oai.provurl")) {
					String[] arguments = split[1].split("\\|");
					Provider provider = storage.findProvider(arguments[0]);
					if (provider != null) {
						provider.setOaiBaseUrl(arguments[1]);
						storage.updateProvider(provider);
					} else {
						log.warning("Failed to set provider oai url. Provider <" + arguments[0] + " not found.");
					}
				} else if (split[0].startsWith("oai.provprefix")) {
					String[] arguments = split[1].split("\\|");
					Provider provider = storage.findProvider(arguments[0]);
					if (provider != null) {
						provider.setOaiPrefix(arguments[1]);
						storage.updateProvider(provider);
					} else {
						log.warning("Failed to set provider oai prefix. Provider <" + arguments[0] + " not found.");
					}
				} else if (split[0].startsWith("collection")) {
					setFieldValues(split);
					createCollection(storage, session);
				} else if (split[0].startsWith("oai.collurl")) {
					String[] arguments = split[1].split("\\|");
					Collection collection = storage.findCollection(arguments[0]);
					if (collection != null) {
						collection.setOaiBaseUrl(arguments[1]);
						storage.updateCollection(collection);
					} else {
						log.warning("Failed to set collection oai url. Collection <" + arguments[0] + " not found.");
					}
				} else if (split[0].startsWith("oai.collprefix")) {
					String[] arguments = split[1].split("\\|");
					Collection collection = storage.findCollection(arguments[0]);
					if (collection != null) {
						collection.setOaiPrefix(arguments[1]);
						storage.updateCollection(collection);
					} else {
						log.warning("Failed to set collection oai prefix. Collection <" + arguments[0] + " not found.");
					}
				} else if (split[0].startsWith("oai.collset")) {
					String[] arguments = split[1].split("\\|");
					Collection collection = storage.findCollection(arguments[0]);
					if (collection != null) {
						collection.setOaiSet(arguments[1]);
						storage.updateCollection(collection);
					} else {
						log.warning("Failed to set collection oai set. Collection <" + arguments[0] + " not found.");
					}
				}
			}
			line = reader.readLine();
		}
	}


	private void setFieldValues(String[] split) {
		String[] arguments = split[1].split("\\|");
		this.argument0 = null;
		this.argument1 = null;
		this.argument2 = null;
		this.parent = null;

		this.argument0 = arguments[0];
		if (arguments.length > 1) {
			this.argument1 = arguments[1];
		}
		if (arguments.length > 2) {
			this.argument2 = arguments[2];
		}
		if (arguments.length > 3) {
			this.parent = arguments[3];
		}
	}



	/**
	 * @return the registry
	 */
	public Registry getRegistry() {
		return registry;
	}

}
