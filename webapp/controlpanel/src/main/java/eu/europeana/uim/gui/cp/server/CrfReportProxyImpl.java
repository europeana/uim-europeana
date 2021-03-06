package eu.europeana.uim.gui.cp.server;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

import eu.europeana.harvester.client.HarvesterClient;
import eu.europeana.harvester.client.HarvesterClientConfig;
import eu.europeana.harvester.client.HarvesterClientImpl;
import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.domain.SourceDocumentReference;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.gui.cp.client.services.CrfReportProxy;
import eu.europeana.uim.gui.cp.server.util.PropertyReader;
import eu.europeana.uim.gui.cp.server.util.UimConfigurationProperty;
import eu.europeana.uim.gui.cp.shared.CRFFailedRecordReportDTO;
import eu.europeana.uim.gui.cp.shared.CRFFailedTaskDTO;
import eu.europeana.uim.gui.cp.shared.CRFReplyDTO;
import eu.europeana.uim.gui.cp.shared.CRFTaskDTO;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ymamakis on 9-7-15.
 */
public class CrfReportProxyImpl extends
        IntegrationServicesProviderServlet implements CrfReportProxy {
    private static HarvesterClient client;

    public CrfReportProxyImpl(){
        try {

            String[] mongoHosts = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTURL).split(",");
            int mongoPort = Integer.parseInt(PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTPORT));
            String dbName = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_DB);

            String username = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_USERNAME);
            String password = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_PASSWORD);
			List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
			for (String address : mongoHosts) {
				serverAddresses.add(new ServerAddress(address, mongoPort));
			}
            MongoClient mongo = new MongoClient(serverAddresses);
            Morphia morphia = new Morphia();
            Datastore ds;
            if(StringUtils.isNotEmpty(password)) {
            	boolean auth = mongo.getDB("admin").authenticate("admin", password.toCharArray());
                mongo.setReadPreference(ReadPreference.secondary());
                if (!auth) {
                	//FIXME this implementation is not used!
//    				throw new MongoException("ERROR: Couldn't authenticate the admin-user against admin-db");
    			}
                ds = morphia.createDatastore(mongo, dbName);
            } else {
                ds = morphia.createDatastore(mongo, dbName);
            }

            HarvesterClientConfig config = new HarvesterClientConfig();

            BlockingInitializer sdr = new BlockingInitializer() {

                @Override
                protected void initializeInternal() {
                    SourceDocumentReference sdrRef = new SourceDocumentReference();
                }
            };
            sdr.initialize(SourceDocumentReference.class.getClassLoader());
            BlockingInitializer pj = new BlockingInitializer() {

                @Override
                protected void initializeInternal() {
                    ProcessingJob pJob = new ProcessingJob();
                }
            };
            pj.initialize(ProcessingJob.class.getClassLoader());
            client = new HarvesterClientImpl(ds,config);
        } catch (IOException ex) {
            Logger.getLogger(CrfReportProxyImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public List<CRFReplyDTO> getAllActive() {

        return null;
    }

    @Override
    public List<CRFReplyDTO> getByProvider(CRFTaskDTO crfTask) {
        return null;
    }

    @Override
    public List<CRFReplyDTO> getByCollection(CRFTaskDTO crfTask) {



        return null;
    }

    @Override
    public List<CRFFailedRecordReportDTO> getFailedByCollection(CRFTaskDTO crfTask) {
        return null;
    }

    @Override
    public List<CRFFailedRecordReportDTO> getFailedByExecution(CRFFailedTaskDTO crfFailedTask) {
        return null;
    }
}
