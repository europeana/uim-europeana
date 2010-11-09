package eu.europeana.uim.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import eu.europeana.uim.common.parse.RecordMap;

public class DateUtils {
    
    private static final Logger log = Logger.getLogger(RecordMap.class.getName());

    private static final DateFormat dfFull = new SimpleDateFormat("yyyy-MM-dd");

    private static final DateFormat dfYear = new SimpleDateFormat("yyyy");
	
	
	public static Date parse(String datestring) {
        try {
            if (datestring.length() <= 4) {
                return (dfYear.parse(datestring));
            } else {
                return (dfFull.parse(datestring));
            }
        } catch (ParseException e) {
        	log.warning("Failed to parse <" + datestring + ">");
        }
        return null;
	}

}
