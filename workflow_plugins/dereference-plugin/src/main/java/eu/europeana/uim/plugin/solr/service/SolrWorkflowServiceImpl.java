package eu.europeana.uim.plugin.solr.service;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.DefaultCreator;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.plugin.solr.utils.OsgiExtractor;
import eu.europeana.uim.plugin.solr.utils.PropertyReader;
import eu.europeana.uim.plugin.solr.utils.UimConfigurationProperty;
import org.apache.commons.lang.StringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SolrWorkflowServiceImpl implements SolrWorkflowService {
  private static OsgiExtractor extractor;
  private static RDFReader readerF;
  private static Datastore datastore;

  public SolrWorkflowServiceImpl() {
    BlockingInitializer datastoreInitializer = new BlockingInitializer() {

      @Override
      protected void initializeInternal() {

        if (datastore == null) {
          Morphia morphia = new Morphia();
          morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass(String clazz, DBObject object) {
              return MongoBundleActivator.getBundleClassLoader();
            }
          });
          morphia.map(ControlledVocabularyImpl.class);
          try {
            List<ServerAddress> addresses = new ArrayList<ServerAddress>();
            String[] mongoHost =
                PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL).split(",");
            for (String mongoStr : mongoHost) {
              ServerAddress address =
                  new ServerAddress(mongoStr, Integer.parseInt(PropertyReader
                      .getProperty(UimConfigurationProperty.MONGO_HOSTPORT)));
              addresses.add(address);
            }

          /*  List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
            MongoCredential credentials =
                MongoCredential.createCredential(PropertyReader
                    .getProperty(UimConfigurationProperty.MONGO_USERNAME), PropertyReader
                    .getProperty(UimConfigurationProperty.MONGO_AUTH_DB), PropertyReader
                    .getProperty(UimConfigurationProperty.MONGO_PASSWORD).toCharArray());
            credentialsList.add(credentials);
            MongoClient client = new MongoClient(addresses, credentialsList);*/

            Mongo mongo = new Mongo(addresses);
            if(StringUtils.isNotBlank(PropertyReader
                    .getProperty(UimConfigurationProperty.MONGO_USERNAME))) {
              datastore =
                      morphia.createDatastore(mongo,
                              PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_VOCABULARY), PropertyReader
                                      .getProperty(UimConfigurationProperty.MONGO_USERNAME), PropertyReader
                                      .getProperty(UimConfigurationProperty.MONGO_PASSWORD).toCharArray());
            } else {
              datastore =
                      morphia.createDatastore(mongo,
                              PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_VOCABULARY));
            }

          } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
         catch (UnknownHostException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
          datastore.ensureIndexes();
        }

      }
    };
    datastoreInitializer.initialize(Datastore.class.getClassLoader());

    final OsgiExtractor extractor = new OsgiExtractor();
    BlockingInitializer initializer = new BlockingInitializer() {

      @Override
      protected void initializeInternal() {
        // if(extractor == null){

        // }
        extractor.setDatastore(datastore);


      }
    };
    initializer.initialize(OsgiExtractor.class.getClassLoader());

    BlockingInitializer vocInitializer = new BlockingInitializer() {

      @Override
      protected void initializeInternal() {
        // TODO Auto-generated method stub
        List<ControlledVocabularyImpl> vocabularies = extractor.getControlledVocabularies();

      }
    };
    vocInitializer.initialize(ControlledVocabularyImpl.class.getClassLoader());

    BlockingInitializer rdfReaderInitializer = new BlockingInitializer() {

      @Override
      protected void initializeInternal() {
       readerF =  new RDFReaderFImpl().getReader();

      }
    };
    rdfReaderInitializer.initialize(RDFReaderFImpl.class.getClassLoader());
  }

  @Override
  public OsgiExtractor getExtractor() {
    // OsgiExtractor extractor = new OsgiExtractor(this);
    if (extractor == null) {
      extractor = new OsgiExtractor(this);
      extractor.setDatastore(datastore);
    }

    return extractor;
  }


  @Override
  public Datastore getDatastore() {
    return datastore;
  }

  @Override
  public RDFReader getRDFReader() {
    return readerF;
  }

}
