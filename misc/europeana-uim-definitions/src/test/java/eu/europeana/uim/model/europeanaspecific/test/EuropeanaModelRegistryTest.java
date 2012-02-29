/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.model.europeanaspecific.test;

import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.MetaDataRecord.QualifiedValue;
import eu.europeana.uim.store.bean.MetaDataRecordBean;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;

import eu.europeana.uim.edmcore.definitions.RDF;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;

/**
 * 
 * @author Georgios Markakis
 */
public class EuropeanaModelRegistryTest {


    /**
     * 
     */
    @Test
    public void testFullEDMRecord() {
        MetaDataRecord<Long> record = new MetaDataRecordBean<Long>(1L, null);

        String rdf = new String();
        
        //rdf.getChoiceList().get(0).getProvidedCHO().getChoiceList().get(0).getAlternative().getLang().

		record.addValue(EuropeanaModelRegistry.EDMRECORD, rdf);
        
		List<String> values = record.getValues(EuropeanaModelRegistry.EDMRECORD);
		

    }
	
	
	
}
