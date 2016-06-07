package eu.europeana.uim.gui.cp.shared.validation;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class ImageCachingStatisticsResultDTO implements IsSerializable {

	private int numberStatisticsReports;
	
	private List<ImageCachingStatisticsDTO> statisticsReports;
	
	public ImageCachingStatisticsResultDTO() {
		super();
	}

	public ImageCachingStatisticsResultDTO(List<ImageCachingStatisticsDTO> statisticsReports, int number){
		this.statisticsReports = statisticsReports;
		this.numberStatisticsReports = number;
	}
	
	public int getNumberStatisticsReports() {
		return numberStatisticsReports;
	}

	public void setNumberStatisticsReports(int numberStatisticsReports) {
		this.numberStatisticsReports = numberStatisticsReports;
	}

	public List<ImageCachingStatisticsDTO> getStatisticsReports() {
		return statisticsReports;
	}

	public void setStatisticsReports(List<ImageCachingStatisticsDTO> reports) {
		this.statisticsReports = reports;
	}	
}
