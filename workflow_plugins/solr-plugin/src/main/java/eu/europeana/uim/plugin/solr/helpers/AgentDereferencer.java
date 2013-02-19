package eu.europeana.uim.plugin.solr.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import eu.europeana.corelib.definitions.jibx.AgentType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.uim.plugin.solr.service.SolrWorkflowService;

public class AgentDereferencer extends Dereferencer{

	public AgentDereferencer(SolrWorkflowService solrWorkflowService) {
		super(solrWorkflowService);
	}

	@Override
	public <T> void dereference(RDF rdf, T t) throws MalformedURLException, SecurityException, IllegalArgumentException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		AgentType agent = (AgentType)t;
		derefResourceOrLiteral(rdf, agent.getAbout());
		derefResourceOrLiteralList(rdf, agent.getHasMetList());
		derefResourceOrLiteralList(rdf, agent.getIsRelatedToList());
		
	}

	
}
