PROJECT STRUCTURE
=================

Path                Name                                                    Description
----------------------------------------------------------------------------------------------------------------------------
/                     Unified Ingestion Manager                                The root maven project
/common               Unified Ingestion Manager: Common                        Shared classes and resources (e.g. for testing)
/api                  Unified Ingestion Manager: API                           The UIM API bundle, used by plugins
/plugins/basic        Unified Ingestion Manager: API Basic Implementation      The basic/default implementation of the API

/import/file          Unified Ingestion Manager: Import from File              Bundle to import data from a XML file
/import/oaipmh        Unified Ingestion Manager: Import from OAI-PMH           Bundle to import data from an OAI-PMH 

/plugins/integration  Unified Ingestion Manager: Integration tests             The integration tests for
/plugins/dummy        Unified Ingestion Manager: Dummy Plugin                  Our beloved dummy plugin


INSTALLATION
============

1) Get Apache Felix Karaf at http://karaf.apache.org/

2) Build UIM with maven
   - 'mvn install'

3) Start Karaf:
   - go to the Karaf main directory
   - start it 'bin/start'

4) Connect to Karaf:
   - go to the Karaf main directory
   - connect with 'bin/client'

5) Connect to Karaf:
   - install necessary dependencies
   - spring feature
     - features:install spring

6) Configure UIM Feature
   - features:addurl file://<project-path>/etc/uim-features.xml
   - you can check if the feature "uim-core" is available via 'features:list'
     - features:install uim-core

NOT NECESSARY WHEN USING BLUEPRINT!!!
6) Deploy Spring DM 2 on Karaf
   - for the moment, Karaf ships with Spring DM 1.2.x, and we need 2.x
   - in the Karaf console, run:
     - features:addurl file://<project-path>/etc/spring-features.xml
     - features:install spring-dm-2
   - you can check if the feature is installed via 'features:list'

   For the moment, you will most likely get a bunch of stacktraces from Spring DM and Felix.
   This should go away in the future


BUILDING UIM
============

1) Goto <project-path> and do a maven install 
   Note that maven compile might fail - we need a test-jar from the common module, 
   therefore only mvn install works. As soon as this is once build you can also run
   mvn compile in the project root directory.
   
2) Install UIM in Karaf
   - STEP BY STEP: in the Karaf shell, type
     'osgi:install -s mvn:eu.europeana/europeana-uim-common/1.0.0-SNAPSHOT'
     
     'osgi:install -s mvn:eu.europeana/europeana-uim-api/1.0.0-SNAPSHOT'

     'osgi:install -s mvn:eu.europeana/europeana-uim-plugin-basic/1.0.0-SNAPSHOT'
     
     'osgi:install -s mvn:eu.europeana/europeana-uim-storage-memory/1.0.0-SNAPSHOT'
     
   - BY FEATURE: in the Karaf shell, type
     'features:install uim-core'  
     
3) Verify if UIM is up and running (Note that auto completion with TAB does only work when blueprint is used)
   - in Karaf shell: 'uim:info'


4) Load/Show sample data:
   - in Karaf shell: 'uim:store -o loadSampleData'
   - in Karaf shell: 'uim:store -o listProvider'
   - in Karaf shell: 'uim:store -o listCollection'
   

TEST IMPORT FROM FILE
=====================

1) Install karaf and the UIM API

2) Build UIM module import file (build automatically when building the toplevel of UIM)

3) Install import file: 'osgi:install -s mvn:eu.europeana/europeana-uim-import-file/1.0.0-SNAPSHOT'

4) Verify if UIM File Import is up and running:
   - in Karaf shell: 'uim:file'
   - should complain about missing arguments
   
5) Import ESE file:
   - in Karaf shell: 'uim:file -c 000 file://<project-path>/common/src/test/resources/readingeurope.xml'
   
   