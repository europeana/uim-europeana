package eu.europeana.uim.plugin.solr.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.TimeSpanType;
import eu.europeana.uim.plugin.solr.service.SolrWorkflowService;

public class TimespanDereferencer extends Dereferencer {

	public TimespanDereferencer(SolrWorkflowService solrWorkflowService) {
		super(solrWorkflowService);
	}

	@Override
	public <T> void dereference(RDF rdf, T t) throws MalformedURLException,
			SecurityException, IllegalArgumentException, IOException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException, ResourceNotRDFException {
		TimeSpanType timeSpan = (TimeSpanType) t;
		derefResourceOrLiteralList(rdf,  timeSpan.getHasPartList());
		derefResourceOrLiteralList(rdf, timeSpan.getIsPartOfList());
		derefResourceOrLiteralList(rdf, timeSpan.getSameAList());
		derefResourceOrLiteral(rdf,  timeSpan.getAbout());
		derefResourceOrLiteral(rdf, timeSpan.getBegin());
		derefResourceOrLiteral(rdf, timeSpan.getEnd());
	}

}
