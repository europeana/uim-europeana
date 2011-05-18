package eu.europeana.uim.sugarcrmclient.plugin.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.clientbindings.utils.Utils;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService;
import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMServiceImpl;
import eu.europeana.uim.sugarcrmclient.plugin.objects.SugarCrmRecord;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.DatasetStates;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.RetrievableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.UpdatableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SimpleSugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.QueryResultException;


@Command(name = "uim", scope = "sugaragent")
public class SugarCrmCommand implements Function, Action {

	enum Operation {info,updatesession,retrieverecords,initworkflowbyID,
		initworkflowsbyState,fetchrecord,updaterecord,
		changeRecordStatus,populateUIMfromRecord,addNotetoRecord,addpollinglistener}
	
	private SugarCRMService sugarcrmPlugin;
	
	@Option(name = "-o", aliases = {"--operation"}, required = false)
	private Operation operation;
	
	@Argument(index = 0)
	private String argument0;

	@Argument(index = 1)
	private String argument1;
	
	
	public SugarCrmCommand (SugarCRMService sugarcrmPlugin ){
		this.sugarcrmPlugin = sugarcrmPlugin;
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.felix.gogo.commands.Action#execute(org.osgi.service.command.CommandSession)
	 */
	@Override
	public Object execute(CommandSession commandsession) throws Exception {

		PrintStream out = commandsession.getConsole();
	    BufferedReader in = new BufferedReader(new InputStreamReader(commandsession.getKeyboard()));
	    
		if (operation == null) {
			out.println("Please specify an operation with the '-o' option. Possible values are:");
			out.println("info                    \t\t\t\t provides information regarding the existing remote connection to SugarCRM");
			out.println("updatesession           \t\t\t\t creates a new session for the client");					
			out.println("retrieverecords         \t\t\t\t retrieves records");
			out.println("fetchrecord             \t\t\t\t retrieves a record by id");
			out.println("fetchrecord             \t\t\t\t retrieves a record by id");
			out.println("initworkflowbyID            \t\t\t\t initialize a workflow ");
			
			return null;
		}
		
		switch (operation) {
		case info:
			out.println(sugarcrmPlugin.showConnectionStatus());
			break;
		case updatesession:
			String username = assignvalue("Username", argument0,out,in);
			String password = assignvalue("Password", argument1,out,in);
			sugarcrmPlugin.updateSession(username, password);
			break;

		case retrieverecords:
			retrieverecordsCMD(out);
			break;
		case fetchrecord:
			String recId = assignvalue("Record ID", argument0,out,in);
			SugarCrmRecord rec = sugarcrmPlugin.retrieveRecord(recId);
			if (rec != null){
				out.println(rec.toString());
			}
			break;
		case updaterecord:
			String recordID = assignvalue("Record ID", argument0,out,in);
			String threcords = assignvalue("Total Harvested Records",out,in);
			String himages = assignvalue("Total Harvested Images",out,in);
			String htetx = assignvalue("Total Harvested Text",out,in);
			String hvideo = assignvalue("Total Harvested Video",out,in);
			String hsound = assignvalue("Total Harvested Sound",out,in);
			
			HashMap<UpdatableField, String> values  = new HashMap<UpdatableField, String>();
			values.put(UpdatableField.TOTAL_INGESTED, threcords);
			values.put(UpdatableField.INGESTED_IMAGE, himages);
			values.put(UpdatableField.INGESTED_TEXT, htetx);			
			values.put(UpdatableField.INGESTED_VIDEO, hvideo);
			values.put(UpdatableField.INGESTED_SOUND, hsound);
			
			sugarcrmPlugin.updateRecordData(recordID, values);
			break;
		case changeRecordStatus:
			if(argument0!=null)
			{	
			    //out.println(sugarcrmPlugin.notifySugarForIngestionSuccess(argument0));
			}
			else
			{
				out.println("Please define the module id");
			}
			break;	
		case initworkflowbyID:
			String worklfowName = "asd";
			SugarCrmRecord record = null;
			DatasetStates endstate = DatasetStates.MAPPING_AND_NORMALIZATION;
			sugarcrmPlugin.initWorkflowFromRecord(worklfowName, record, endstate);			
			break;
		case initworkflowsbyState:
		
			break;
		case populateUIMfromRecord:
			
			break;
		case addNotetoRecord:
			
			break;
		case addpollinglistener:
			
			break;
		default:
			out.println("Unknown Command...");
		}
		
		
		return null;
	}

	
	
	/* (non-Javadoc)
	 * @see org.osgi.service.command.Function#execute(org.osgi.service.command.CommandSession, java.util.List)
	 */
	@Override
	public Object execute(CommandSession commandsession, List<Object> arg1)
			throws Exception {

		PrintStream out = commandsession.getConsole();
		
		return null;
	}
	
	
	
	private void retrieverecordsCMD(PrintStream out) throws QueryResultException{
		SimpleSugarCrmQuery query =  new SimpleSugarCrmQuery();
		query.setMaxResults(1000);
		query.setOffset(0);
		query.setOrderBy(RetrievableField.DATE_ENTERED);
		query.setStatus(DatasetStates.MAPPING_AND_NORMALIZATION);
		List<SugarCrmRecord> records = sugarcrmPlugin.retrieveRecords(query);
		out.println("Number of Records retrieved: " + records.size());
		out.println("NO | RECORD ID");

		for(int i=0; i< records.size(); i++){
			out.println( i + "  :       " + records.get(i).getItemValue(RetrievableField.ID));
		}
	}
	
	private String assignvalue(String description,String argument,PrintStream out,BufferedReader in ) throws IOException{
		String retval = null;
		
		if (argument != null){
			return argument;
		}
		else{	
			while(retval!= null){
			    out.println("Please enter the"+ description +":");
			    retval = in.readLine();
			}		
		return retval;
		}
	}
	
	private String assignvalue(String description,PrintStream out,BufferedReader in ) throws IOException{

		String retval = null;
		while(retval!= null){
		    out.println("Please enter the"+ description +":");
		    retval = in.readLine();
		}		
	return retval;
	}
	
	

	
	

}
