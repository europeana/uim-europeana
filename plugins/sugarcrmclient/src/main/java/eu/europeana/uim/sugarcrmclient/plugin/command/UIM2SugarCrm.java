package eu.europeana.uim.sugarcrmclient.plugin.command;

import java.io.PrintStream;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.osgi.service.command.CommandSession;
import org.osgi.service.command.Function;

import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgentImpl;


@Command(name = "uim", scope = "sugarCrmAgent")
public class UIM2SugarCrm implements Function, Action {

	enum Operation {info,showpendingjobs,getpolltime,setpolltime}
	
	private SugarCRMAgentImpl sugarcrmPlugin;
	
	@Option(name = "-o", aliases = {"--operation"}, required = false)
	private Operation operation;
	
	@Argument(index = 0)
	private String argument0;

	@Argument(index = 1)
	private String argument1;
	
	
	
	@Override
	public Object execute(CommandSession commandsession) throws Exception {

		PrintStream out = commandsession.getConsole();
		
		if (operation == null) {
			out.println("Please specify an operation with the '-o' option. Possible values are:");
			out.println("  info \t\t\t\t\t\t provides inforamtion regarding the existing remote connection to SugarCRM");
			out.println("  showpendingjobs \t\t\t\t shows all entries in SugarCRM who are candidates for ingestion initiation.");
			out.println("  getpolltime \t\t\t\t shows the time set for the next polling session");
			out.println("  setpolltime \t\t\t\t sets the time for the next polling session");
			return null;
		}
		
		switch (operation) {
		case info:
			out.println(sugarcrmPlugin.showConnectionStatus());
			break;
		case showpendingjobs:
			out.println(sugarcrmPlugin.pollForHarvestInitiators());
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

	@Override
	public Object execute(CommandSession commandsession, List<Object> arg1)
			throws Exception {

		PrintStream out = commandsession.getConsole();
		
		return null;
	}
	
	
	
	

}
