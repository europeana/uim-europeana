package eu.europeana.enrichment;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.europeana.corelib.definitions.jibx.Date;
import eu.europeana.corelib.definitions.jibx.EuropeanaType;
import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.uim.enrichment.utils.EuropeanaDateUtils;
import eu.europeana.uim.enrichment.utils.PropertyReader;

public class EuropeanaDateUtilsTest {

	@Test
	public void testUtils(){
		PropertyReader.loadPropertiesFromFile("src/test/resources/uim.properties");
		ProxyType proxy = new ProxyType();
		List<EuropeanaType.Choice> dates = new ArrayList<EuropeanaType.Choice>();
		//String dcDateString1 = "-150 / -70"; //should give -150 and -70
		//String dcDateString2 = "75/140 AD"; //should give 75 and 140
		String dcDateString3 = "circa 1500 BC"; //should give -1500
		String dcDateString4 = "http://semium.org/time/1996"; //should be empty
		String dcDateString5 = "110. Jahrestag Ortsvereinsgründung Wiedergründung SPD-Ortsverein";
		
//		EuropeanaType.Choice dateChoice1 = new EuropeanaType.Choice();
//		Date date1 = new Date();
//		date1.setString(dcDateString1);
//		dateChoice1.setDate(date1);
//		dates.add(dateChoice1);
//		EuropeanaType.Choice dateChoice2 = new EuropeanaType.Choice();
//		Date date2 = new Date();
//		date2.setString(dcDateString2);
//		dateChoice2.setDate(date2);
//		dates.add(dateChoice2);
		EuropeanaType.Choice dateChoice3 = new EuropeanaType.Choice();
		Date date3 = new Date();
		date3.setString(dcDateString3);
		dateChoice3.setDate(date3);
		dates.add(dateChoice3);
		EuropeanaType.Choice dateChoice4 = new EuropeanaType.Choice();
		Date date4 = new Date();
		date4.setString(dcDateString4);
		dateChoice4.setDate(date4);
		dates.add(dateChoice4);
		EuropeanaType.Choice dateChoice5 = new EuropeanaType.Choice();
		Date date5 = new Date();
		date5.setString(dcDateString5);
		dateChoice5.setDate(date5);
		dates.add(dateChoice5);
		proxy.setChoiceList(dates);
		
		//EuropeanaDateUtils.setPath(truncated);
		List<String> filteredDates = new EuropeanaDateUtils().createEuropeanaYears(proxy);
		Assert.assertTrue(filteredDates.size()==2);
		
		
	}
}
