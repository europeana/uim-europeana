
INDEX
=====
1. Installation Instructions.
2. Usage Instructions.



1. Installation Instructions 
=============================

1. Compile UIM modules
--------------------------
Follow steps 1-2 as described in the /europeana-uim/README.txt in order to install 
karaf and compile and install the core UIM modules.


2.  Install the Europeana specific Sugarcrm server:
----------------------------------------------------
2.1. Download and install SugarCRM version 5.5.4 (NOT the latest version)
from http://www.sugarforge.org/frs/download.php/7016/SugarCE-5.5.4.zip

2.2.Use the wizard to install SugarCRM. Also install the optional MysqPHPAdmin application.
Start sugarCRM, login and create a user named test with password test. 

2.3. Download locally the following 2 files:
http://sip-manager.isti.cnr.it/geomark/sugarcrm-14-03-2011.sql
http://sip-manager.isti.cnr.it/geomark/sugarcrm-14-03-2011.tar.gz

2.4. Use the MysqPHPAdmin frontend to import the sugarcrm-14-03-2011.sql into the running 
SugarCRM instance.

2.5. Shutdown SugarCRM and unzip sugarcrm-14-03-2011.tar.gz. Copy the contents of
<extraction_dir>/var/www/sugarcrm/ directory  to 
<your_installation_dir>/sugarcrm-europeana/htdocs/sugarcrm directory.

2.6. Restart SugarCrm. Now you will normally have a replica of the original Europeana
SugarCRM running on your machine.



3. Compile the sugarcrm modules
-----------------------------------
3.1. Checkout everything in: 
svn co https://europeanalabs.eu/svn/europeana/trunk/uim/plugins/ 

3.2. Change to directory 
cd sugarclientbindings
mvn  install
to install the JIBX bindings

3.3.  Change to directory 
cd sugarcrmclient
mvn install

(If the unit tests fail, this means that the SugarCRM server running in the background is
either down or improperly configured).

4. Configure Karaf and install dependencies
--------------------------------------------
4.1 Before starting Karaf 
Edit <karaf_installation_folder>\etc\jre.properties and comment out the following elements

 #javax.xml.stream, \
 #javax.xml.stream.events, \
 #javax.xml.stream.util, \

4.2 Start Karaf (<karaf_installation_folder>\etc\ sh karaf)

4.3. In the Karaf console add the folowing features files:
'features:addurl file://<uim-project-path>/etc/uim-features.xml'
'features:addurl file://<uim-project-path>/etc/spring-features.xml'

4.4 Install the following features:
features:install spring-ws
features:install spring-dm
features:install spring-quartz

features:install uim-core
features:install war
features:install webconsole
features:install uim-core-gui

4.5 Manually install the sugarcrm plugin within UIM:
osgi:install -s mvn:eu.europeana/europeana-uim-plugin-sugarcrmclient/1.2.0-SNAPSHOT

4.6 Test the UIM plugin:
At the command line issue:
uim:sugarcrmagent

If the installation was successfull then instructions will shown regarding the functionality 
of the sugarcrm plugin.

 

 