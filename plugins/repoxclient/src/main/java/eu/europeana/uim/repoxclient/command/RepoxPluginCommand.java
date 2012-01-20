/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.repoxclient.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.repox.AggregatorOperationException;
import eu.europeana.uim.repox.DataSourceOperationException;
import eu.europeana.uim.repox.HarvestingOperationException;
import eu.europeana.uim.repox.ProviderOperationException;
import eu.europeana.uim.repox.RepoxUIMService;
import eu.europeana.uim.repoxclient.jibxbindings.Success;
import eu.europeana.uim.repoxclient.utils.CommandUtils;

import eu.europeana.uim.store.Collection;

import eu.europeana.uim.store.Provider;

/**
 * Apache Karaf command line extension for RepoxUIM plugin
 * 
 * @author Georgios Markakis
 * @author Yorgos Mamakis
 */
@Command(name = "uim", scope = "repoxagent")
public class RepoxPluginCommand implements Function, Action {

	enum Operation {
		info, createaggregator, deleteaggregator, updateaggregator, createprovider, deleteprovider, updateprovider, createdatasource, deletedatasource, updatedatasource, retrieveaggregators, retrieveproviders, retrievedatasources, initiateharvesting, getharvestingstatus, getactiveharvests
	}

	private RepoxUIMService repoxservice;
	private Registry registry;

	@Option(name = "-o", aliases = { "--operation" }, required = false)
	private Operation operation;

	@Argument(index = 0)
	private String argument0;

	@Argument(index = 1)
	private String argument1;

	@Argument(index = 2)
	private String argument2;

	@Argument(index = 3)
	private String argument3;

	@Argument(index = 4)
	private String argument4;

	@Argument(index = 5)
	private String argument5;

	@Argument(index = 6)
	private String argument6;

	@Argument(index = 7)
	private String argument7;

	@Argument(index = 8)
	private String argument8;

	/**
	 * @param repoxservice
	 */
	public RepoxPluginCommand(RepoxUIMService repoxservice) {
		this.repoxservice = repoxservice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.felix.gogo.commands.Action#execute(org.apache.felix.service
	 * .command.CommandSession)
	 */
	@Override
	public Object execute(CommandSession commandsession) throws Exception {

		PrintStream out = commandsession.getConsole();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				commandsession.getKeyboard()));

		if (operation == null) {
			out.println("Please specify an operation with the '-o' option. Possible values are:");
			out.println("info                                                       \t\t\t\t Connection info to Sugarcrm");
			out.println("createaggregator <country_code,country_url>                          \t\t\t\t Creates a new aggregator");
			out.println("deleteaggregator <country_code>                                   \t\t\t\t Delete an aggregator");
			out.println("updateaggregator <country_code,aggr_name,aggr_name_code,aggr_url>                                     \t\t\t\t Updates an aggregator");
			out.println("createprovider  <prov_name,prov_mnemonic,prov_url,prov_descr,prov_country, prov_URL,prov_SgrID,prov_Type,prov_OAI_prfx> \t\t\t\t Create a provider");
			out.println("deleteprovider  <prov_name,prov_mnemonic>                       \t\t\t\t Delete a provider");
			out.println("updateprovider  <prov_name,prov_mnemonic,prov_url,prov_descr,prov_country, prov_URL,prov_SgrID,prov_Type,prov_OAI_prfx>  \t\t\t\t Update a provider");
			out.println("createdatasource  <prov_name,prov_mnemonic,coll_lang,coll_mnemonic,coll_name,coll_OAIPMHURI,coll_OAI_prfx> \t\t\t\t Initializes workflows according ot records states ");
			out.println("deletedatasource <datasource_name>                           \t\t\t\t Creates Collection/Providers objects from a record");
			out.println("updatedatasource <prov_name,prov_mnemonic,coll_lang,coll_mnemonic,coll_name,coll_OAIPMHURI,coll_OAI_prfx> \t\t\t\t Adds a note attachment to a specific record");
			out.println("retrieveaggregators                                         \t\t\t\t Retrieves all Aggregators");
			out.println("retrieveproviders                                     \t\t\t\t Retrieves all Providers");
			out.println("retrievedatasources                                    \t\t\t\t Retrieves all Datasources");
			out.println("initiateharvesting <prov_name,prov_mnemonic,coll_name,coll_mnemonic>                       \t\t\t\t Initiate the harvesting of a datasource");
			out.println("getharvestingstatus <prov_name,prov_mnemonic,coll_name,coll_mnemonic>                 \t\t\t\t Get the status of a harvest of a datasource");
			out.println("getactiveharvests                                       \t\t\t\t Get the active harvests");
			//not implemented yet
			//out.println("addpollinglistener <datasource_name>                           \t\t\t\t Add a polling listener to a datasource");
			//out.println("removepollinglisteners <datasource_name>                         \t\t\t\t Remove a polling listener from a datasource");

			return null;
		}
		

		switch (operation) {
		case info:
			out.println(CommandUtils.retrieveRepoxConnectionStatus(repoxservice));
			
			break;

		// --------------------Repox aggregator actions-----------------
		case createaggregator:
			out.println(CommandUtils.createAggregator(repoxservice, argument0, argument1, out, in));
			break;

		case deleteaggregator:
			out.println(CommandUtils.deleteAggregator(repoxservice, argument0, out, in));
			
			break;

		case updateaggregator:
			out.println(CommandUtils.updateAggregator(repoxservice, argument0, argument1, argument2, argument3, out, in));
			
			break;

		// ------------Repox provider actions---------------
		case createprovider:
			
			out.println(CommandUtils.executeCreateUpdateProviderAction("create", repoxservice,registry,argument0, argument1, argument2, argument3, argument4, argument5, argument6, argument7, argument8, out, in));
			
			break;

		case deleteprovider:
			out.println(CommandUtils.deleteProvider(repoxservice, registry, argument0, argument1, out, in));
			break;

		case updateprovider:
			out.println(CommandUtils.executeCreateUpdateProviderAction("update", repoxservice,registry,argument0, argument1, argument2, argument3, argument4, argument5, argument6, argument7, argument8, out, in));
			
			break;

		// --------------Data sources Actions---------------
		case createdatasource:
			out.println(CommandUtils.createUpdateDataSource("create",repoxservice, registry, argument0,argument1,argument2,argument3,argument4, argument5, argument6, out,in));
			
			
			break;

		case updatedatasource:
			out.println(CommandUtils.createUpdateDataSource("update",repoxservice, registry, argument0,argument1,argument2,argument3,argument4, argument5, argument6, out,in));

			break;

		case deletedatasource:
			out.println(CommandUtils.deleteDatasource(repoxservice,registry,argument0,argument1,argument2,argument3,argument4, out, in));
			
			break;

		case retrieveaggregators:
			out.println (CommandUtils.retrieveAggregators(repoxservice,out, in));
			
			break;

		case retrieveproviders:
			out.println(CommandUtils.retrieveProviders(repoxservice,out,in));
			break;

		case retrievedatasources:
			out.println(CommandUtils.retrieveDatasources(repoxservice,out,in));
			break;

		// -------------Harvesting--------------

		case initiateharvesting:
			out.println(CommandUtils.initiateHarvesting(repoxservice,registry, argument0,argument1,argument2,argument3, out,in));
			
			break;

		case getharvestingstatus:
			out.println(CommandUtils.getHarvestingStatus(repoxservice,registry,argument0,argument1,argument2,argument3,argument4,out,in));
			
			break;

		case getactiveharvests:
			out.println(CommandUtils.getActiveHarvests(repoxservice, out,in));
			
			break;
		}
			
		return null;
	}

	



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.felix.service.command.Function#execute(org.apache.felix.service
	 * .command.CommandSession, java.util.List)
	 */
	@Override
	public Object execute(CommandSession arg0, List<Object> arg1)
			throws Exception {
		return null;
	}
}
