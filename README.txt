INDEX
=====

1/ Project structure
2/ Installation
3/ Building UIM
4/ Test import from file
5/ Technologies in use

TODO: refactor 2 and 3, they somewhat overlap


#1 PROJECT STRUCTURE
====================

Path                              Name                                                    Description
-------------------------------------------------------------------------------------------------------------------------------------------
/                                 Unified Ingestion Manager                                The root maven project
/common                           Unified Ingestion Manager: Common                        Shared classes and resources (e.g. for testing)
/api                              Unified Ingestion Manager: API                           The UIM API bundle, used by plugins
/plugins/basic                    Unified Ingestion Manager: API Basic Implementation      The basic/default implementation of the API

/gui/uim-webconsole-extension     Unified Ingestion Manager: Webconsole extension          UIM GUI extension for the Karaf Webconsole

/storage/memory                   Unified Ingestion Manager: Storage Backend Memory        In-memory implementation of the storage engine

/plugins/fileimp                  Unified Ingestion Manager: Import from File              Bundle to import data from a XML file
/plugins/integration              Unified Ingestion Manager: Integration tests             The integration tests, using PAX-Exam
/plugins/dummy                    Unified Ingestion Manager: Dummy Plugin                  Our beloved dummy plugin

/workflows/dummy                  Unified Ingestion Manager: Dummy Workflow                Our beloved dummy workflow



#2 INSTALLATION
===============

1) Get Apache Felix Karaf at http://karaf.apache.org/

2) Build UIM with maven
   - 'mvn install'

3) Start Karaf:
   - go to the Karaf main directory
   - start it 'bin/start'

4) Connect to Karaf:
   - go to the Karaf main directory
   - connect with 'bin/client'

5) Set-up dependencies in Karaf:
   - install necessary dependencies
   - spring feature
     - features:install spring

6) Configure UIM Feature
   - features:addurl file://<project-path>/etc/uim-features.xml
   - you can check if the feature "uim-core" is available via 'features:list'
     - features:install uim-core

#3 BUILDING UIM
===============

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
   

#4 TEST IMPORT FROM FILE
========================

1) Install karaf and the UIM API

2) Build UIM module import file (build automatically when building the toplevel of UIM)

3) Install import file: 'osgi:install -s mvn:eu.europeana/europeana-uim-import-file/1.0.0-SNAPSHOT'

4) Verify if UIM File Import is up and running:
   - in Karaf shell: 'uim:file'
   - should complain about missing arguments
   
5) Import ESE file:
   - in Karaf shell: 'uim:file -c 000 file://<project-path>/common/src/test/resources/readingeurope.xml'


#5 TECHNOLOGIES IN USE
======================

- the project runs on Apache Karaf which bundles Felix and other Apache OSGi projects (http://karaf.apache.org)
- we use the OSGi Blueprint Container specification.
  Karaf/Felix uses the Apache Aries implementation for that purpose, which handles inversion of control through declarative configuration (see the OSGI-INF.blueprint packages)
- we use PAX Exam for integration tests (http://wiki.ops4j.org/display/paxexam/Pax+Exam)
- we further use Spring for dependency injection in JUnit tests (not integration tests, nor runtime) (http://www.springsource.org/)