package eu.europeana.uim.enrichment.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.definitions.solr.entity.Proxy;

public class EuropeanaDateUtils {

	public static List<String> createEuropeanaYears(FullBean fullbean) {
		List<String> years = new ArrayList<String>();
		try {
			String bcList = FileUtils.readFileToString(new File(
					"src/main/resources/bcList.txt"), "UTF-8");
			String[] bc = StringUtils.split(bcList, "\n");
			for (Proxy proxy : fullbean.getProxies()) {
				if (proxy.getDcDate() != null) {
					for (Entry<String, List<String>> dcDates : proxy
							.getDcDate().entrySet()) {
						years.addAll(refineDates(bc, dcDates.getValue()));
					}
				}
				if (proxy.getDctermsCreated() != null) {
					for (Entry<String, List<String>> dctermsCreated : proxy
							.getDctermsCreated().entrySet()) {
						years.addAll(refineDates(bc, dctermsCreated.getValue()));
					}
				}
				if (proxy.getDctermsTemporal() != null) {
					for (Entry<String, List<String>> dctermsTemporal : proxy
							.getDctermsTemporal().entrySet()) {
						years.addAll(refineDates(bc, dctermsTemporal.getValue()));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return years;
	}

	private static List<String> refineDates(String[] patterns,
			List<String> input) {
		List<String> dates = new ArrayList<String>();
		for (String inputField : input) {
			String contains = contains(inputField, patterns);
			if (contains != null) {
				dates.addAll(getDates(inputField, contains));
			} else if (StringUtils.contains(inputField, "-")) {
				dates.addAll(getDates(inputField, "-"));
			} else {
				dates.addAll(clean(inputField));
			}
		}
		return dates;
	}

	private static String contains(String input, String[] filters) {
		for (String filter : filters) {
			if (StringUtils.containsIgnoreCase(input, filter)) {
				return filter;
			}
		}
		return null;
	}

	private static List<String> clean(String inputField) {
		List<String> dates = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		char[] chars = inputField.toCharArray();
		int i = 0;
		for (char ch : chars) {
			if (Character.isDigit(ch)) {
				sb.append(ch);
				if (!Character.isDigit(chars[i + 1])) {
					dates.add(sb.toString());
					sb = new StringBuffer();
				}
			}
			i++;
		}

		return dates;
	}

	private static List<String> getDates(String input, String remove) {
		List<String> dates = new ArrayList<String>();
		String[] left = StringUtils.splitByWholeSeparator(input, remove);
		List<Character> chars = new ArrayList<Character>();
		for (String str : left) {

			chars.add('-');
			for (char ch : str.toCharArray()) {
				if (Character.isDigit(ch)) {
					chars.add(ch);
				} else if (ch == '\\' || ch == '/') {
					chars.add(',');
				}
			}
			chars.add(',');
		}
		StringBuffer sb = new StringBuffer();
		for (char ch : chars) {
			if (ch != ',') {
				sb.append(ch);
			} else {
				if (sb.length() > 0) {
					dates.add(sb.toString());
					sb = new StringBuffer();
				}
			}
		}
		return dates;
	}
}
