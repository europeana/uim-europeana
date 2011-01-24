package eu.europeana.uim.command;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.UimEntity;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.osgi.service.command.CommandSession;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * uim:orchestrator
 * list
 * start <workflow> (collection | provider) <dataSet>
 * pause <requestId>
 * cancel <requestId>
 * status <requestId>
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@Command(name = "uim", scope = "request")
public class UIMOrchestratorCommand implements Action {

    enum Operation {list, start, pause, cancel, status, help}

    private final Registry registry;

    private final Orchestrator orchestrator;

    private static final DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");

    @Option(name = "-o", aliases = {"--operation"}, required = false)
    private Operation operation;

    @Argument(index = 0)
    private String argument0;

    @Argument(index = 1)
    private String argument1;

    @Argument(index = 2)
    private String argument2;

    public UIMOrchestratorCommand(Registry registry, Orchestrator orchestrator) {
        this.registry = registry;
        this.orchestrator = orchestrator;
    }

    @Override
    public Object execute(CommandSession session) throws Exception {
        PrintStream out = session.getConsole();

        if (operation == null) {
            out.println("Please specify an operation with the '-o' option. Possible values are:");
            out.println("  list\t\t\t\t\t\tlists the current executions");
            out.println("  start <workflow> <provider> <collection>\tstarts a new execution");
            out.println("  pause <execution>\t\t\t\tpauses the given execution");
            out.println("  cancel <execution>\t\t\t\tcancels the given execution");
            out.println("  status <execution>\t\t\t\tgives status information about the given execution");
            return null;
        }

        switch (operation) {
            case list:
                listExecutions(out);
                break;
            case start:
                start(out);
                break;
            case pause:
                pause(out);
                break;
            case cancel:
                cancel(out);
                break;
            case status:
                out.println("Master, this is not implemented yet.");
            default:
                out.println("Master, I am truly sorry but this doesn't work.");
        }

        return null;
    }

    private void pause(PrintStream out) {
        ActiveExecution execution = getOrListExecution(out, "pause");
        if(execution != null) {
            orchestrator.pause(execution);
        } else {
            out.println("Could not find execution to pause with ID " + argument0);
        }
    }

    private void cancel(PrintStream out) {
        ActiveExecution execution = getOrListExecution(out, "cancel");
        if(execution != null) {
            orchestrator.cancel(execution);
        } else {
            out.println("Could not find execution to cancel with ID " + argument0);
        }

    }

    private ActiveExecution getOrListExecution(PrintStream out, String command) {
        if(argument0 == null) {
            out.println("No can do. The correct syntax is: " + command + " <execution>");
            out.println("Possible executions are:");
            for(ActiveExecution e: orchestrator.getActiveExecutions()) {
                out.println(String.format("Execution %d: Workflow %s, data set %s", e.getId(), e.getWorkflow().getName(), e.getDataSet()));
            }
            out.println();
        }
        ActiveExecution execution = null;
        for(ActiveExecution e : orchestrator.getActiveExecutions()) {
            if(e.getId() == Long.parseLong(argument0)) {
                execution = e;
                break;
            }
        }
        return execution;
    }


    private void start(PrintStream out) {


        if (argument0 == null || argument1 == null || argument2 == null) {
            out.println("No can do. The correct syntax is: start <workflow> <provider> <collection>");
            out.println();
        }

        StorageEngine storage = registry.getStorage();
        List<Workflow> workflows = registry.getWorkflows();
        List<Provider> providers = storage.getProvider();

        if (argument0 == null) {
            printWorfklows(out, workflows);
        }
        if (argument0 != null && argument1 == null) {
            printProviders(out, storage, providers);
        }
        if (argument0 != null && argument1 != null && argument2 == null) {
            int p = Integer.parseInt(argument1);
            Provider provider = providers.get(p);
            out.println("No collection specified. Possible choices for provider " + provider.getName() + " are:");
            List<Collection> collections = storage.getCollections(provider);
            for (int i = 0; i < collections.size(); i++) {
                out.println(i + ") " + collections.get(i).getName());
            }
        }

        if (argument0 != null && argument1 != null && argument2 != null) {

            Workflow w = workflows.get(Integer.parseInt(argument0));
            Provider p = providers.get(Integer.parseInt(argument1));
            Collection c = storage.getCollections(p).get(Integer.parseInt(argument2));

            out.println();
            out.println("Starting to run worfklow '" + w.getName() + "' on collection '" + c.getName() + "' of provider '" + p.getName() + "'");

            ProgressMonitor pm = new ConsoleProgressMonitor(out);

            orchestrator.executeWorkflow(w, c, pm);
        }


    }

    private void printProviders(PrintStream out, StorageEngine storage, List<Provider> providers) {
        out.println("No provider specified. Possible choices are:");
        for (int i = 0; i < providers.size(); i++) {
            Provider p = storage.getProvider(i);
            out.println(i + ") " + p.getName());
        }
    }

    private void printWorfklows(PrintStream out, List<Workflow> workflows) {
        out.println("No workflow specified. Possible choices are:");
        for (int i = 0; i < workflows.size(); i++) {
            Workflow w = workflows.get(i);
            out.println(i + ") " + w.getName() + " - " + w.getDescription());
        }
    }

    private void listExecutions(PrintStream out) {
        out.println("Active executions:");
        for (ActiveExecution e : orchestrator.getActiveExecutions()) {
            out.println(df.format(e.getStartTime()) + "  Workflow '" + e.getWorkflow().getName() + "' DataSet '" + getDataSetName(e.getDataSet()) + "'");
        }
    }

    private String getDataSetName(UimEntity dataSet) {
        if (dataSet instanceof Collection) {
            return ((Collection) dataSet).getName();
        }
        if (dataSet instanceof Provider) {
            return ((Provider) dataSet).getName();
        }
        if (dataSet instanceof MetaDataRecord) {
            return "MetaDataRecord " + dataSet.getId();
        }
        if (dataSet instanceof Request) {
            Request request = ((Request) dataSet);
            return "Request on collection '" + request.getCollection().getName() + "' at " + df.format(request.getDate());
        }

        return "There is no spoon.";
    }
}
