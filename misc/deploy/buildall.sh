#!/bin/sh

# This statrtup script  Be sure to set the $UIM_SOURCE and $KARAF_UIM_HOME
# environment variables properly before executing it.


#Build UIM Core

cd $UIM_SOURCE/uim-core/
git pull

cd $UIM_SOURCE/uim-core/framework/trunk
mvn clean install -DskipTests

cd $UIM_SOURCE/uim-core/external/sugar/trunk
mvn clean install -DskipTests

cd $UIM_SOURCE/uim-core/external/repox/trunk
mvn clean install -DskipTests

cd $UIM_SOURCE/uim-core/framework/trunk/gui
mvn clean install -DskipTests

cd $UIM_SOURCE/uim-core/logging/database/trunk
mvn clean compile process-classes test-compile test jar:test-jar source:jar install -DskipTests  

cd  $UIM_SOURCE/uim-core/model/trunk
mvn clean install -DskipTests

cd $UIM_SOURCE/uim-core/plugins/check/trunk
mvn clean install -DskipTests

cd $UIM_SOURCE/uim-core/plugins/solr/trunk/solr4
mvn clean install -DskipTests

cd $UIM_SOURCE/uim-core/storage/mongo/trunk
mvn clean install -DskipTests

#Build Annocultor
cd $UIM_SOURCE/tools
git pull

cd $UIM_SOURCE/tools/annocultor_solr4
mvn clean install -DskipTests

#Build Core Library
cd $UIM_SOURCE/corelib
git pull
mvn clean install -DskipTests

#Build Europeana UIM components
cd $UIM_SOURCE/uim-europeana
git pull
mvn clean install -DskipTests

#Stop Karaf
$KARAF_UIM_HOME/bin/stop

#Wait for a while
sleep 5

#Copy the produced KAR file to the karaf deploy folder
cp $UIM_SOURCE/uim-europeana/misc/europeana-uim-deploy/target/*.kar $KARAF_UIM_HOME/deploy/

#Start Karaf again
$KARAF_UIM_HOME/bin/start