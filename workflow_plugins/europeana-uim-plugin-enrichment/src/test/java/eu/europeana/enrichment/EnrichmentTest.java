package eu.europeana.enrichment;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import eu.annocultor.converters.europeana.Entity;
import eu.europeana.uim.enrichment.utils.EuropeanaEnrichmentTagger;


public class EnrichmentTest {
	
	@Test
	public void testAnnocultorEnrichment(){
		int port = 10000;
		try {
			MongodConfig conf = new MongodConfig(Version.V2_0_7, 10000,
					false,"src/test/resources/annocultor_db");
			MongodStarter runtime = MongodStarter.getDefaultInstance();
			MongodExecutable mongoExec = runtime.prepare(conf);
			mongoExec.start();
		
			EuropeanaEnrichmentTagger tagger = new EuropeanaEnrichmentTagger();
			tagger.init("Europeana","localhost",Integer.toString(port));
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("proxy_dc_subject", "paper");
			doc.addField("proxy_dcterms_temporal", "1928");
			doc.addField("proxy_dc_coverage","paris");
			doc.addField("proxy_dc_creator","rembrandt");
			List<Entity> entities = tagger.tagDocument(doc);
			Assert.assertEquals(7, entities.size());
			mongoExec.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
