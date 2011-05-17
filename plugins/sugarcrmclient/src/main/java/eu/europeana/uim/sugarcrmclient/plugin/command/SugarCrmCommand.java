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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.clientbindings.utils.Utils;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService;
import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMServiceImpl;


@Command(name = "uim", scope = "sugaragent")
public class SugarCrmCommand implements Function, Action {

	enum Operation {info,updatesession,showavailablemodules,showmodulefields,
		pending4ingestion,notifyIngestionSuccsess,notifyIngestionFailure,getpolltime,setpolltime}
	
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
		
		if (operation == null) {
			out.println("Please specify an operation with the '-o' option. Possible values are:");
			out.println("info                    \t\t\t\t provides inforamtion regarding the existing remote connection to SugarCRM");
			out.println("updatesession           \t\t\t\t creates a new session for the client");					
			out.println("pending4ingestion       \t\t\t\t shows all entries in SugarCRM who are candidates for ingestion initiation.");
			out.println("notifyIngestionSuccsess \t\t\t\t notifies SugarCRM that ingestion for the specified entries was successfull");
			out.println("notifyIngestionFailure  \t\t\t\t notifies SugarCRM that ingestion for the specified entries has failed");
			return null;
		}
		
		switch (operation) {
		case info:
			out.println(sugarcrmPlugin.showConnectionStatus());
			break;
		case updatesession:
			//out.println(sugarcrmPlugin.updateSession());
			break;
		case pending4ingestion:
			sugarcrmPlugin.pollForHarvestInitiators();
			break;
		case notifyIngestionSuccsess:
			if(argument0!=null)
			{	
			    //out.println(sugarcrmPlugin.notifySugarForIngestionSuccess(argument0));
			}
			else
			{
				out.println("Please define the module id");
			}
			break;	
		case notifyIngestionFailure:
			if(argument0!=null)
			{	
			    //out.println(sugarcrmPlugin.notifySugarForIngestionFailure(argument0));
			}
			else
			{
				out.println("Please define the module id");
			}
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
	 * @param getEntryListResponse
	 */
	private void outputInitiators(PrintStream out ,GetEntryListResponse getEntryListResponse){
		StringBuffer result = new StringBuffer();
		
		
		result.append("Found ");
		
		List<Element> anyList = getEntryListResponse.getReturn().getEntryList().getArray().getAnyList();
		
		result.append(anyList.size());
		result.append(" jobs pending for ingestion. \n");
		
		
		Iterator<Element> itr =anyList.iterator();
		
		  while(itr.hasNext()){ 
		     result.append("============================== \n"); 
			 result.append(" ITEM ID:");
			 
			 
			 
			 Element el = itr.next();
			 
			 result.append(ClientUtils.extractFromElement("id", el));
			 result.append("\n");
			 
			 
			 NodeList nl =el.getChildNodes();
			 

			 for (int i=0;i<nl.getLength();i++){
				  result.append(nl.item(i).getLocalName());
				  result.append("=");
				  result.append(nl.item(i).getTextContent());
				  result.append("\n");
			 }
		     
     
			result.append("============================== \n"); 
		  }
		  
		  out.println(result.toString());
	}
	
	
	

}
