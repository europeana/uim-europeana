package eu.europeana.enrichment;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.europeana.uim.enrichment.normalizer.AgentNormalizer;

public class AgentNormalizerTest {

	
	@Test
	public void test(){
		List<String> str  = new ArrayList<String>();
		str.add("Elämä, Osuuskunta [te] (te) ; 1982");
		str.add("Elämä, Osuuskunta ; 1982");
		System.out.println(AgentNormalizer.normalize(str));
		
	}
}
