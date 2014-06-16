/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europeana.uim.neo4jplugin.impl;

import eu.europeana.corelib.definitions.solr.entity.AbstractEdmEntity;
import java.util.Map;
import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.support.Neo4jTemplate;

/**
 *
 * @author gmamakis
 */
public class ManagedTransactionRelationshipRepository {

    private final ManagedTransaction transaction;
  
   //private final Neo4jTemplate neo4jTemplate;

    public ManagedTransactionRelationshipRepository(ManagedTransaction transaction, Neo4jTemplate neo4jTemplate) {
        this.transaction = transaction;
       // this.neo4jTemplate = neo4jTemplate;
    }

//    public void createRelationShip(Node entity1, Node entity2, String relationship,
//            Map<String, Object> properties) {
//        
//        transaction.prepareTransaction();
//        neo4jTemplate.createRelationshipBetween(entity1, entity2, relationship, properties);
//    }
}
