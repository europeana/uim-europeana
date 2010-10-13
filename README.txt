INSTALLATION
============

1) Get Apache Felix Karaf:
   - go to http://felix.apache.org/site/downloads.cgi
   - scroll down and download the "Karaf" project (not the Felix Framework Distribution)

2) Build UIM with maven
   - 'mvn install'

3) Start Karaf:
   - go to the Karaf main directory
   - run it via e.g. 'bin/karaf'

4) Install UIM
   - in the Karaf shell, type 'osgi:install -s mvn:eu.europeana/europeana-uim-api/1.0.0-SNAPSHOT'

5) Verify if UIM is up and running
   - in Karaf shell: uim:info

TEST IMPORT OF FILE
===================

1) Install karaf and the UIM API

2) Build UIM module import file (build automatically when building the toplevel of UIM)

3) Install import file:
   osgi:install -s mvn:eu.europeana/europeana-uim-import-file/1.0.0-SNAPSHOT

4) Verify if UIM File Import is up and runnign:
   - in Karaf shell: uim:file
   - should complain about missing parameters
   
5) Import ESE file:
   - in Karaf shell: uim:file /data/readingeurope/readingeurope.xml ese   
  
  
   