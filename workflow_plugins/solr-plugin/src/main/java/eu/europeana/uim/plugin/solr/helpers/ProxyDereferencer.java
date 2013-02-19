package eu.europeana.uim.plugin.solr.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;

import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.uim.plugin.solr.service.SolrWorkflowService;

public class ProxyDereferencer extends Dereferencer{

	public ProxyDereferencer(SolrWorkflowService solrWorkflowService) {
		super(solrWorkflowService);
	}

	@Override
	public <T> void dereference(RDF rdf, T t) throws MalformedURLException,
			SecurityException, IllegalArgumentException, IOException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		ProxyType proxy = (ProxyType)t;
		derefResourceOrLiteralList(rdf,  proxy.getHasMetList());
		derefResourceOrLiteralList(rdf, proxy.getHasTypeList());
		derefResourceOrLiteralList(rdf, proxy.getIncorporateList());
		derefResourceOrLiteralList(rdf, 
				proxy.getIsDerivativeOfList());
		derefResourceOrLiteralList(rdf, proxy.getIsRelatedToList());
		derefResourceOrLiteralList(rdf, proxy.getIsSimilarToList());
		derefResourceOrLiteralList(rdf, proxy.getIsSuccessorOfList());
		derefResourceOrLiteralList(rdf, proxy.getRealizeList());
		derefResourceOrLiteralList(rdf, proxy.getYearList());
		derefResourceOrLiteral(rdf, proxy.getCurrentLocation());
		List<eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice> choices = proxy
				.getChoiceList();
		if (choices != null) {
			for (eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice choice : choices) {
				if (choice.ifContributor())
					derefResourceOrLiteral(rdf, 
							choice.getContributor());
				if (choice.ifCoverage())
					derefResourceOrLiteral(rdf, choice.getCoverage());
				if (choice.ifCreated())
					derefResourceOrLiteral(rdf, choice.getCreated());
				if (choice.ifCreator())
					derefResourceOrLiteral(rdf, choice.getCreator());
				if (choice.ifDate())
					derefResourceOrLiteral(rdf, choice.getDate());
				if (choice.ifExtent())
					derefResourceOrLiteral(rdf, choice.getExtent());
				if (choice.ifFormat())
					derefResourceOrLiteral(rdf, choice.getFormat());
				if (choice.ifHasFormat())
					derefResourceOrLiteral(rdf, 
							choice.getHasFormat());
				if (choice.ifHasPart())
					derefResourceOrLiteral(rdf, choice.getHasPart());
				if (choice.ifHasVersion())
					derefResourceOrLiteral(rdf, 
							choice.getHasVersion());
				if (choice.ifIdentifier())
					derefResourceOrLiteral(rdf, 
							choice.getIdentifier());
				if (choice.ifIsFormatOf())
					derefResourceOrLiteral(rdf, 
							choice.getIsFormatOf());
				if (choice.ifIsPartOf())
					derefResourceOrLiteral(rdf, choice.getIsPartOf());
				if (choice.ifIsReferencedBy())
					derefResourceOrLiteral(rdf, 
							choice.getIsReferencedBy());
				if (choice.ifIsReplacedBy())
					derefResourceOrLiteral(rdf, 
							choice.getIsReplacedBy());
				if (choice.ifIsRequiredBy())
					derefResourceOrLiteral(rdf, 
							choice.getIsRequiredBy());
				if (choice.ifIssued())
					derefResourceOrLiteral(rdf, choice.getIssued());
				if (choice.ifIsVersionOf())
					derefResourceOrLiteral(rdf, 
							choice.getIsVersionOf());
				if (choice.ifLanguage())
					derefResourceOrLiteral(rdf, choice.getLanguage());
				if (choice.ifMedium())
					derefResourceOrLiteral(rdf, choice.getMedium());
				if (choice.ifPublisher())
					derefResourceOrLiteral(rdf, 
							choice.getPublisher());
				if (choice.ifReferences())
					derefResourceOrLiteral(rdf, 
							choice.getReferences());
				if (choice.ifRelation())
					derefResourceOrLiteral(rdf, choice.getRelation());
				if (choice.ifReplaces())
					derefResourceOrLiteral(rdf, choice.getReplaces());
				if (choice.ifSource())
					derefResourceOrLiteral(rdf, choice.getSource());
				if (choice.ifSpatial())
					derefResourceOrLiteral(rdf, choice.getSpatial());
				if (choice.ifSubject())
					derefResourceOrLiteral(rdf, choice.getSubject());
				if (choice.ifTemporal())
					derefResourceOrLiteral(rdf, choice.getTemporal());
				if (choice.ifTitle())
					derefResourceOrLiteral(rdf, choice.getTitle());
				if (choice.ifType())
					derefResourceOrLiteral(rdf, choice.getType());
			}
		}
	}

	
}
