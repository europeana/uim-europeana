/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.europeana.uim.neo4jplugin.impl;

import org.neo4j.graphdb.Transaction;
import org.springframework.data.neo4j.core.GraphDatabase;

public class ManagedTransaction {
	private static GraphDatabase graphDatabaseService;
	private static Transaction transaction;
        private static int i;
	private static ManagedTransaction  mt;
        private ManagedTransaction(){
            
        }

        public static ManagedTransaction getInstance(GraphDatabase dbService){
            if(mt==null){
                mt  = new ManagedTransaction();
                graphDatabaseService = dbService;
                transaction = graphDatabaseService.beginTx();
            }
            return mt;
        }
        
        public void stop(){
            transaction.success();
            transaction.finish();
        }
	public void prepareTransaction() {
		incrementOperationsCount();
		if (shouldCreateNewTransaction()) {
			commitAndStartNewTransaction();
		}
	}

	private void incrementOperationsCount() {
            System.out.println(i);
            i++;
	}

	private boolean shouldCreateNewTransaction() {
            if(i==100000){
                return true;
            }
            return false;
	}

	private void commitAndStartNewTransaction() {
            transaction.success();
            transaction.finish();
            transaction = graphDatabaseService.beginTx();
            i=0;
	}
}