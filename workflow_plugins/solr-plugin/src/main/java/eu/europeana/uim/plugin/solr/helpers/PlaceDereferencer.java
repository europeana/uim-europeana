package eu.europeana.uim.plugin.solr.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import eu.europeana.corelib.definitions.jibx.PlaceType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.uim.plugin.solr.service.SolrWorkflowService;

public class PlaceDereferencer extends Dereferencer{

	public PlaceDereferencer(SolrWorkflowService solrWorkflowService) {
		super(solrWorkflowService);
	}

	@Override
	public <T> void dereference(RDF rdf, T t) throws MalformedURLException,
			SecurityException, IllegalArgumentException, IOException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		PlaceType place = (PlaceType)t;
		derefResourceOrLiteral(rdf,  place.getAbout());
		derefResourceOrLiteralList(rdf,  place.getIsPartOfList());
		derefResourceOrLiteralList(rdf, place.getSameAList());
		derefResourceOrLiteralList(rdf, place.getHasPartList());
	}

	
}
