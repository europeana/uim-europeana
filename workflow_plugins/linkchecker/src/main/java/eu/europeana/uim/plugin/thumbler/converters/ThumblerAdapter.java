/**
 * 
 */
package eu.europeana.uim.plugin.thumbler.converters;


import eu.europeana.uim.plugin.thumbler.service.ThumblerPlugin;

/**
 * @author geomark
 *
 */
public class ThumblerAdapter extends LinkcheckerAdapter{

	private static String pluginIdentifier = new ThumblerPlugin<String>().getIdentifier();
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.adapter.UimDatasetAdapter#getPluginIdentifier()
	 */
	@Override
	public String getPluginIdentifier() {
		return ThumblerAdapter.pluginIdentifier;
	}
}
