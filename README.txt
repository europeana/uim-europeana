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
     - features:addurl <project-path>/etc/spring-features.xml
     - features:install spring-dm-2
   - you can check if the feature is installed via 'features:list'

   For the moment, you will most likely get a bunch of stacktraces from Spring DM and Felix.
   This should go away in the future

5) Install UIM
   - in the Karaf shell, type 'osgi:install -s mvn:eu.europeana/europeana-uim-api/1.0.0-SNAPSHOT'

6) Verify if UIM is up and running
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
   - in Karaf shell: 'uim:file <project-path>/import/file/src/test/resources/readingeurope.xml ese'