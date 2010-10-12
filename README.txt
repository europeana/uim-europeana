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

