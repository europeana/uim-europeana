PROJECT STRUCTURE
=================

Path                Name                                                    Description
----------------------------------------------------------------------------------------------------------------------------
/                   Unified Ingestion Manager                                The root maven project
/common             Unified Ingestion Manager: Common                        Shared classes and resources (e.g. for testing)
/api                Unified Ingestion Manager: API                           The UIM API bundle, used by plugins
/import/file        Unified Ingestion Manager: Import File                   Bundle to import data from a XML file
/plugins/dummy      Unified Ingestion Manager: Dummy Plugin                  Our beloved dummy plugin
/integration-tests  Unified Ingestion Manager: Integration tests             The integration tests for


INSTALLATION
============

1) Get Apache Felix Karaf at http://karaf.apache.org/

2) Build UIM with maven
   - 'mvn install'

3) Start Karaf:
   - go to the Karaf main directory
   - run it via e.g. 'bin/karaf'

4) Deploy Spring DM 2 on Karaf
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
   Note that maven compile might fail - we need a test-jar from the common module, therefore only mvn install works.
   
2) Install UIM in Karaf
   - in the Karaf shell, type
     'osgi:install -s mvn:eu.europeana/europeana-uim-api/1.0.0-SNAPSHOT'
     'osgi:install -s mvn:eu.europeana/europeana-uim-common/1.0.0-SNAPSHOT'

6) Verify if UIM is up and running (Note that auto completion with TAB does not work yet)
   - in Karaf shell: 'uim:info'


TEST IMPORT OF FILE
===================

1) Install karaf and the UIM API

2) Build UIM module import file (build automatically when building the toplevel of UIM)

3) Install import file: 'osgi:install -s mvn:eu.europeana/europeana-uim-import-file/1.0.0-SNAPSHOT'

4) Verify if UIM File Import is up and running:
   - in Karaf shell: 'uim:file'
   - should complain about missing parameters
   
5) Import ESE file:
   - in Karaf shell: 'uim:file <project-path>/common/src/test/resources/readingeurope.xml ese'