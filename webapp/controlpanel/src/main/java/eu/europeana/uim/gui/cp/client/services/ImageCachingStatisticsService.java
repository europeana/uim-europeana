package eu.europeana.uim.gui.cp.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.europeana.uim.gui.cp.shared.validation.ImageCachingStatisticsResultDTO;

/**
 * 
 * @author Alena Fedasenka
 *
 */
@RemoteServiceRelativePath("imagecachingstatistics")
public interface ImageCachingStatisticsService extends RemoteService {

	/**
	 * Retrieval method for image caching reports.
	 */
	public ImageCachingStatisticsResultDTO getImageCachingStatistics(int offset, int maxSize, List<String> collections); 
}
