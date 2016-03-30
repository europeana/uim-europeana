package eu.europeana.uim.gui.cp.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.europeana.uim.gui.cp.shared.validation.ImageCachingStatisticsResultDTO;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public interface ImageCachingStatisticsServiceAsync {

	public void getImageCachingStatistics(int offset, int maxSize, List<String> collections, String providerId, AsyncCallback<ImageCachingStatisticsResultDTO> reports);
}
