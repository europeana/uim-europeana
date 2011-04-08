package eu.europeana.uim.sugarcrmclient.plugin.command;

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

import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent;
import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgentImpl;


@Command(name = "uim", scope = "sugaragent")
public class UIM2SugarCrm implements Function, Action {

	enum Operation {info,updatesession,showavailablemodules,showmodulefields,
		pending4ingestion,notifyIngestionSuccsess,notifyIngestionFailure,getpolltime,setpolltime}
	
	private SugarCRMAgent sugarcrmPlugin;
	
	@Option(name = "-o", aliases = {"--operation"}, required = false)
	private Operation operation;
	
	@Argument(index = 0)
	private String argument0;

	@Argument(index = 1)
	private String argument1;
	
	
	public UIM2SugarCrm (SugarCRMAgent sugarcrmPlugin ){
		this.sugarcrmPlugin = sugarcrmPlugin;
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.felix.gogo.commands.Action#execute(org.osgi.service.command.CommandSession)
	 */
	@Override
	public Object execute(CommandSession commandsession) throws Exception {

		PrintStream out = commandsession.getConsole();
		
		if (operation == null) {
			out.println("Please specify an operation with the '-o' option. Possible values are:");
			out.println("info                    \t\t\t\t provides inforamtion regarding the existing remote connection to SugarCRM");
			out.println("updatesession           \t\t\t\t creates a new session for the client");			
			out.println("showavailablemodules    \t\t\t\t shows the available modules in Sugar CRM");		
			out.println("showmodulefields        \t\t\t\t shows the available fields for a given module");		
			out.println("pending4ingestion       \t\t\t\t shows all entries in SugarCRM who are candidates for ingestion initiation.");
			out.println("notifyIngestionSuccsess \t\t\t\t notifies SugarCRM that ingestion for the specified entries was successfull");
			out.println("notifyIngestionFailure  \t\t\t\t notifies SugarCRM that ingestion for the specified entries has failed");
			out.println("getpolltime             \t\t\t\t shows the time set for the next polling session");
			out.println("setpolltime             \t\t\t\t sets the time for the next polling session");
			return null;
		}
		
		switch (operation) {
		case info:
			out.println(sugarcrmPlugin.showConnectionStatus());
			break;
		case updatesession:
			out.println(sugarcrmPlugin.updateSession());
			break;
		case showavailablemodules:
			out.println(sugarcrmPlugin.showAvailableModules());
			break;			
		case showmodulefields:
			if(argument0!=null)
			{	
			    out.println(sugarcrmPlugin.showModuleFields(argument0));
			}
			else
			{
				out.println("Please define the name of the module");
			}
			break;	
		case pending4ingestion:
			outputInitiators(out,sugarcrmPlugin.pollForHarvestInitiators());
			break;
		case notifyIngestionSuccsess:
			if(argument0!=null)
			{	
			    out.println(sugarcrmPlugin.notifySugarForIngestionSuccess(argument0));
			}
			else
			{
				out.println("Please define the module id");
			}
			break;	
		case notifyIngestionFailure:
			if(argument0!=null)
			{	
			    out.println(sugarcrmPlugin.notifySugarForIngestionFailure(argument0));
			}
			else
			{
				out.println("Please define the module id");
			}
			break;	
		case getpolltime:
			out.println("Not implemented yet.");
			break;
		case setpolltime:
			out.println("Not implemented yet.");
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
	
	
	
	/**
	 * @param out
	 * @param resultMap
	 */
	private void outputInitiators(PrintStream out ,HashMap<String, HashMap<String, String>> resultMap){
		StringBuffer result = new StringBuffer();
		
		
		result.append("Found ");
		result.append(resultMap.size());
		result.append(" jobs pending for ingestion. \n");
		
		
		Iterator<String> itr =resultMap.keySet().iterator();
		
		  while(itr.hasNext()){ 
		     result.append("============================== \n"); 
			 result.append(" ITEM ID:");
			 
			 String key = (String) itr.next();
			 result.append(key);
			 result.append("\n");
			 
		     HashMap<String, String> obj = resultMap.get(key);
		     
		     Iterator<String> itr2 = obj.keySet().iterator();
		     
			  while(itr2.hasNext()){ 
				  
				  String innerKey = (String) itr2.next();
				  String innerValue = obj.get(innerKey);
				  result.append(innerKey);
				  result.append("=");
				  result.append(innerValue);
				  result.append("\n");
				  
			  }
		     
			result.append("============================== \n"); 
		  }
		  
		  out.println(result.toString());
	}
	
	
	

}
