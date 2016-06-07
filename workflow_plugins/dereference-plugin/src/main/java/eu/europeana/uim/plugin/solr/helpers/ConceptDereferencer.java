package eu.europeana.uim.plugin.solr.helpers;

import eu.europeana.corelib.definitions.jibx.Concept;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.uim.plugin.solr.service.SolrWorkflowService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class ConceptDereferencer extends Dereferencer {

	public ConceptDereferencer(SolrWorkflowService solrWorkflowService) {
		super(solrWorkflowService);
	}

	@Override
	public <T> void dereference(RDF rdf, T t) throws MalformedURLException,
			SecurityException, IllegalArgumentException, IOException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException, ResourceNotRDFException {
		Concept concept = (Concept)t;
		derefResourceOrLiteral(rdf,  concept.getAbout());
		if(concept.getChoiceList()!=null){
		for (eu.europeana.corelib.definitions.jibx.Concept.Choice choice : concept
				.getChoiceList()) {
			if (choice.ifBroadMatch())
				derefResourceOrLiteral(rdf, choice.getBroadMatch());
			if (choice.ifCloseMatch())
				derefResourceOrLiteral(rdf, choice.getCloseMatch());
			if (choice.ifExactMatch())
				derefResourceOrLiteral(rdf, choice.getExactMatch());
			if (choice.ifNarrowMatch())
				derefResourceOrLiteral(rdf, choice.getNarrowMatch());
			if (choice.ifRelated())
				derefResourceOrLiteral(rdf, choice.getRelated());
			if (choice.ifRelatedMatch())
				derefResourceOrLiteral(rdf, choice.getRelatedMatch());
		}
		}
	}
}
