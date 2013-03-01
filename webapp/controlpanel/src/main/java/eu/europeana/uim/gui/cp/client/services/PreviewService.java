package eu.europeana.uim.gui.cp.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("previewService")
public interface PreviewService extends RemoteService{

	public Boolean commit();
}
