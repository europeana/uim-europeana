package eu.europeana.uim.plugin.solr.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.WebResourceType;
import eu.europeana.uim.plugin.solr.service.SolrWorkflowService;

public class WebResourceDereferencer extends Dereferencer{

	public WebResourceDereferencer(SolrWorkflowService solrWorkflowService) {
		super(solrWorkflowService);
	}

	@Override
	public <T> void dereference(RDF rdf, T t) throws MalformedURLException,
			SecurityException, IllegalArgumentException, IOException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
				WebResourceType webResource = (WebResourceType)t;
				derefResourceOrLiteralList(rdf, webResource.getCreatedList());
				derefResourceOrLiteralList(rdf, webResource.getExtentList());
				derefResourceOrLiteralList(rdf,  webResource.getFormatList());
				derefResourceOrLiteralList(rdf, webResource.getHasPartList());
				derefResourceOrLiteralList(rdf, 
						webResource.getIsFormatOfList());
				derefResourceOrLiteralList(rdf, webResource.getIssuedList());
	}

}
