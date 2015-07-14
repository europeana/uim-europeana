/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved 
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 *  
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under 
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of 
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under 
 *  the Licence.
 */
package eu.europeana.uim.neo4jplugin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InvalidAttributeValueException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.services.RequestType;

import eu.europeana.corelib.definitions.solr.DocType;
import eu.europeana.corelib.neo4j.entity.RelType;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.corelib.utils.EuropeanaUriUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Georgios Markakis (gwarkx@hotmail.com)
 *
 * Mar 13, 2014
 */
public class GraphConstructor {

    private Map<String, Set<RelTemp>> relationsmap;
    private Map<String, Map<String, Map<String, Object>>> edmelementsmap;
    private Map<String, Map<String, Boolean>> edmexistsmap;

    private Map<String, Map<String, RestNode>> retNodeMap;
    private Map<String, List<String>> deletionCandidates;
    private Map<String, Set<String>> parents;

    private EDMRepositoryService edmservice;
    private Transaction tx;

    private RestGraphDatabase graphDb;

    private RestAPI restapi;

    private String index;

    /**
     *
     */
    GraphConstructor() {
        this.edmservice = new EDMRepositoryService();
        init();
    }

    /**
     *
     */
    GraphConstructor(EDMRepositoryService edmservice) {
        this.edmservice = edmservice;
        init();
    }

    public void init() {

        graphDb = edmservice.getGraphDatabaseService();

        tx = graphDb.beginTx();

        relationsmap = new ConcurrentHashMap<String, Set<RelTemp>>();
        edmelementsmap = new ConcurrentHashMap<String, Map<String, Map<String, Object>>>();
        edmexistsmap = new ConcurrentHashMap<String, Map<String, Boolean>>();
        retNodeMap = new ConcurrentHashMap<String, Map<String, RestNode>>();
        deletionCandidates = new ConcurrentHashMap<String, List<String>>();
        parents = new ConcurrentHashMap<String, Set<String>>();
        restapi = graphDb.getRestAPI();
        index = edmservice.getIndex();
        if (!graphDb.index().existsForNodes(index)) {
            graphDb.index().forNodes(index);
        }

    }

    private void createAbsoluteSequence(Set<String> parent, RelType relType,
            Direction dir) {

        ObjectNode obj = JsonNodeFactory.instance.objectNode();

        HttpClient httpClient = new HttpClient();
        HttpClientParams paramss = httpClient.getParams();
        paramss.setConnectionManagerTimeout(300000);
        httpClient.setParams(paramss);

        ArrayNode statements = JsonNodeFactory.instance.arrayNode();
        obj.put("statements", statements);
        int i = 0;
        for (String par : parent) {
            String query = "start n = node:edmsearch2(rdf_about={id}) match (n)-[:`dcterms:hasPart`]->(child) "
                    + "WHERE NOT (child)-[:`edm:isNextInSequence`]->() CREATE (child)-[:isFirstInSequence]->(n);";
            if (dir.equals(Direction.INCOMING)) {
                query = "start n = node:edmsearch2(rdf_about={id}) match (n)-[:`dcterms:hasPart`]->(child) "
                        + "WHERE NOT (child)<-[:`edm:isNextInSequence`]-() CREATE (child)-[:isLastInSequence]->(n);";
            }
            ObjectNode statement = JsonNodeFactory.instance.objectNode();
            statement.put("statement", query);
            ObjectNode parameters = statement.with("parameters");
            statements.add(statement);
            parameters.put("id", par);
            if (i == 100) {

                try {
                    String str = new ObjectMapper().writeValueAsString(obj);
                    PostMethod httpMethod = new PostMethod(
                            restapi.getBaseUri() + "/transaction/commit");
                    httpMethod.setRequestBody(str);
                    httpMethod.setRequestHeader("content-type",
                            "application/json");
                    httpMethod.setRequestHeader("X-Stream", "true");
                    httpClient.executeMethod(httpMethod);
                    //System.out.println(httpMethod.getStatusCode());
                    statements.removeAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i = 0;
            }

            i++;
        }
        try {
            String str = new ObjectMapper().writeValueAsString(obj);
            PostMethod httpMethod = new PostMethod(
                    restapi.getBaseUri() + "/transaction/commit");
            httpMethod.setRequestBody(str);
            //System.out.println(str);
            httpMethod.setRequestHeader("content-type", "application/json");
            httpMethod.setRequestHeader("X-Stream", "true");
            httpClient.executeMethod(httpMethod);
            //System.out.println(httpMethod.getStatusCode());
            statements.removeAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createIsFirstInSequence(Set<String> parent) {
        createAbsoluteSequence(parent, RelType.ISFIRSTINSEQUENCE,
                Direction.OUTGOING);
    }

    private void createIsLastInSequence(Set<String> parent) {
        createAbsoluteSequence(parent, RelType.ISLASTINSEQUENCE,
                Direction.INCOMING);
    }

    private class RelTemp {

        private String from;
        private String to;
        private String rel;

        public RelTemp(String from, String to, String rel) {
            this.from = from;
            this.to = to;
            this.rel = rel;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getRel() {
            return rel;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.from);
            hash = 67 * hash + Objects.hashCode(this.to);
            hash = 67 * hash + Objects.hashCode(this.rel);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RelTemp other = (RelTemp) obj;
            if (!Objects.equals(this.from, other.from)) {
                return false;
            }
            if (!Objects.equals(this.to, other.to)) {
                return false;
            }
            if (!Objects.equals(this.rel, other.rel)) {
                return false;
            }
            return true;
        }

    }

    public synchronized void parseMorphiaEntity(FullBeanImpl fullbean) {

        if (fullbean != null) {

            String id = processEntityID(fullbean.getAbout());
            ProxyImpl provProxy = getEuropeanaProxy(fullbean);

            DocType docType = fullbean.getType();
            Map<String, Object> elementsToSave = new HashMap<String, Object>();
            String collection = fullbean.getEuropeanaCollectionName()[0]
                    .split("_")[0];
            Map<String, Map<String, Object>> elementsForCollection = edmelementsmap
                    .get(collection);
            Set<String> parent = parents.get(collection);
            if (parent == null) {
                parent = new ConcurrentHashSet<>();
            }
            if (elementsForCollection == null) {
                elementsForCollection = new ConcurrentHashMap<String, Map<String, Object>>();
            }
            elementsToSave.put("rdf:about", id);
            if (provProxy.getDcTitle() != null) {
                Map<String, List<String>> titles = provProxy.getDcTitle();
                for (Entry<String, List<String>> entry : titles.entrySet()) {
                    List<String> langStr = new ArrayList<>();
                    langStr.addAll(entry.getValue());
                    elementsToSave.put("dc:title_xml:lang_" + entry.getKey(),
                            langStr);
                }
            }
            if (provProxy.getDcDescription() != null) {
                Map<String, List<String>> description = provProxy
                        .getDcDescription();
                for (Entry<String, List<String>> entry : description.entrySet()) {
                    List<String> langStr = new ArrayList<>();
                    for (String str : entry.getValue()) {
                        langStr.add(StringUtils.substring(str, 0, 255));
                    }
                    elementsToSave.put(
                            "dc:description_xml:lang_" + entry.getKey(),
                            langStr);
                }
            }

            if(provProxy.getDctermsIssued()!=null){
                 Map<String, List<String>> issued = provProxy
                        .getDctermsIssued();
                for (Entry<String, List<String>> entry : issued.entrySet()) {
                    List<String> langStr = new ArrayList<>();
                    for (String str : entry.getValue()) {
                        langStr.add(StringUtils.substring(str, 0, 255));
                    }
                    elementsToSave.put(
                            "dcterms:issued_xml:lang_" + entry.getKey(),
                            langStr);
                }
            }
            if(provProxy.getDcDate()!=null){
                 Map<String, List<String>> date = provProxy
                        .getDcDate();
                for (Entry<String, List<String>> entry : date.entrySet()) {
                    List<String> langStr = new ArrayList<>();
                    for (String str : entry.getValue()) {
                        langStr.add(StringUtils.substring(str, 0, 255));
                    }
                    elementsToSave.put(
                            "dc:date_xml:lang_" + entry.getKey(),
                            langStr);
                }
            }

            if(provProxy.getDctermsCreated()!=null){
                 Map<String, List<String>> created = provProxy
                        .getDctermsCreated();
                for (Entry<String, List<String>> entry : created.entrySet()) {
                    List<String> langStr = new ArrayList<>();
                    for (String str : entry.getValue()) {
                        langStr.add(StringUtils.substring(str, 0, 255));
                    }
                    elementsToSave.put(
                            "dcterms:created_xml:lang_" + entry.getKey(),
                            langStr);
                }
            }

            elementsToSave.put("edm:type", docType);
            elementsForCollection.put(id, elementsToSave);
            edmelementsmap.put(collection, elementsForCollection);
            Set<RelTemp> relationsMapCollection = relationsmap.get(collection);
            if (relationsMapCollection == null) {
                relationsMapCollection = new ConcurrentHashSet<RelTemp>();
            }
            if (provProxy.getDctermsHasPart() != null) {
                for (Entry<String, List<String>> entry : provProxy
                        .getDctermsHasPart().entrySet()) {
                    for (String str : entry.getValue()) {
                        String procId = EuropeanaUriUtils.createEuropeanaId(
                                collection, str);
                        relationsMapCollection.add(new RelTemp(id, procId,
                                "dcterms:hasPart"));
                        relationsMapCollection.add(new RelTemp(procId, id,
                                "dcterms:isPartOf"));
                        parent.add(id);
                    }
                }
            }
            if (provProxy.getDctermsIsPartOf() != null) {
                for (Entry<String, List<String>> entry : provProxy
                        .getDctermsIsPartOf().entrySet()) {
                    for (String str : entry.getValue()) {
                        String procId = EuropeanaUriUtils.createEuropeanaId(
                                collection, str);
                        relationsMapCollection.add(new RelTemp(id, procId,
                                "dcterms:isPartOf"));
                        relationsMapCollection.add(new RelTemp(procId, id,
                                "dcterms:hasPart"));
                        parent.add(procId);
                    }
                }
            }

            if (provProxy.getEdmIsNextInSequence() != null) {

                for (String str : provProxy.getEdmIsNextInSequence()) {
                    relationsMapCollection.add(new RelTemp(id,
                            EuropeanaUriUtils
                            .createEuropeanaId(collection, str),
                            "edm:isNextInSequence"));
                }
            }

            relationsmap.put(collection, relationsMapCollection);
            parents.put(collection, parent);
        }

    }

    private ProxyImpl getEuropeanaProxy(FullBeanImpl fullbean) {
        for (ProxyImpl proxy : fullbean.getProxies()) {
            if (!proxy.isEuropeanaProxy()) {
                return proxy;
            }
        }
        return null;
    }

    public void save() {
        tx.success();
        tx.finish();
    }

    public void computeDependencies(String collectionId) {
        Set<RelTemp> map = relationsmap.get(collectionId);
        System.out.println("Relationships are: " + map.size());
    }

    public void generateNodes(final String collectionId) {
        generateNodes(collectionId, 1000);
    }

    public void generateNodes(final String collectionId, int limit) {
        computeDependencies(collectionId);
        final ConcurrentHashMap<String, Map<String, Object>> map = new ConcurrentHashMap<String, Map<String, Object>>();
        File f = new File("urls");
        List<String> urls = new ArrayList<>();
        int i = 0;
        RestIndex<Node> index2;
        try {
            index2 = restapi.getIndex(index);
        } catch (Exception e) {

            index2 = graphDb.index().forNodes(index);

        }

        final RestIndex<Node> index = index2;
        Map<String, Map<String, Object>> edmCollection = edmelementsmap
                .get(collectionId);
        Map<String, Boolean> edmExistsCollection = new ConcurrentHashMap<String, Boolean>();
        final Map<String, Node> nodeList = new HashMap<String, Node>();
        for (String key : edmCollection.keySet()) {

            map.put(key, edmCollection.get(key));
            i++;

            if (map.size() == limit || edmCollection.size() == i) {
                final Map<String, Node> retNodes = new HashMap<String, Node>();
                Set<String> idset = map.keySet();
                Iterator<String> idsetIterator = idset.iterator();
                while (idsetIterator.hasNext()) {
                    String nodeKey = idsetIterator.next();
                    IndexHits<Node> savedNodes = index
                            .get("rdf_about", nodeKey);
                    if (savedNodes.size() > 0) {
                        retNodes.put(nodeKey, savedNodes.getSingle());
                        edmExistsCollection.put(nodeKey, Boolean.TRUE);
                    } else {
                        retNodes.put(nodeKey, null);
                        edmExistsCollection.put(nodeKey, Boolean.FALSE);
                    }

                }

                tx = graphDb.beginTx();

                try {
                    RestNode relationship = graphDb.getRestAPI().executeBatch(
                            new BatchCallback<RestNode>() {

                                @Override
                                public RestNode recordBatch(RestAPI batchRestApi) {

                                    for (Entry<String, Node> entryNode : retNodes
                                    .entrySet()) {

                                        Map<String, Object> nodemap = map
                                        .get(entryNode.getKey());
                                        Node node2;
                                        if (entryNode.getValue() != null) {
                                            node2 = entryNode.getValue();

                                            restapi.execute(
                                                    RequestType.PUT,
                                                    graphDb.getRestAPI()
                                                    .getBaseUri()
                                                    + "/node/"
                                                    + node2.getId()
                                                    + "/properties",
                                                    nodemap);
                                        } else {
                                            node2 = restapi.createNode(nodemap);

                                        }

                                        nodeList.put((String) nodemap
                                                .get("rdf:about"), node2);
                                    }

                                    return null;

                                }

                            });

                    tx.success();
                    tx.finish();
                    tx = graphDb.beginTx();
                    for (Entry<String, Node> node : nodeList.entrySet()) {
                        restapi.addToIndex(node.getValue(), index, "rdf_about",
                                node.getKey());
                        urls.add(node.getKey());
                    }
                    tx.success();
                    tx.finish();
                    nodeList.clear();
                    map.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            FileUtils.writeLines(f, urls);
        } catch (IOException ex) {
            Logger.getLogger(GraphConstructor.class.getName()).log(Level.SEVERE, null, ex);
        }
        edmexistsmap.put(collectionId, edmExistsCollection);
    }

    public void generateNodeLinks(String mnemonic)
            throws InvalidAttributeValueException {
        Set<String> parentIds = new HashSet<>();
        Set<RelTemp> relTemps = relationsmap.get(mnemonic);
        int i = 0;
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        ObjectNode objIndex = JsonNodeFactory.instance.objectNode();
        ObjectNode parentIndex = JsonNodeFactory.instance.objectNode();
        HttpClient httpClient = new HttpClient();

        ArrayNode statements = JsonNodeFactory.instance.arrayNode();
        obj.put("statements", statements);

        ArrayNode statementsIndex = JsonNodeFactory.instance.arrayNode();
        objIndex.put("statements", statementsIndex);

        ArrayNode parentCreationIndex = JsonNodeFactory.instance.arrayNode();
        parentIndex.put("statements", parentCreationIndex);
        long start = System.currentTimeMillis();
        for (RelTemp relTemp : relTemps) {
            final String id = relTemp.getFrom();

            final String reference = relTemp.getTo();
            final String linkname = relTemp.getRel();
            ObjectNode statement = JsonNodeFactory.instance.objectNode();
            statement
                    .put("statement",
                            "start from = node:edmsearch2(rdf_about={from}), to = node:edmsearch2(rdf_about={to}) create unique (from)-[:`"
                            + linkname + "`]->(to)");
            ObjectNode parameters = statement.with("parameters");
            statements.add(statement);
            parameters.put("from", id);
            parameters.put("to", reference);
            if (StringUtils.equals(linkname, "dcterms:hasPart")) {
                ObjectNode hasChildren = JsonNodeFactory.instance.objectNode();
                ObjectNode parent = hasChildren.with("parameters");

                hasChildren.put("statement",
                        "start n = node:edmsearch2 (rdf_about = {from}) SET n.hasChildren=true return n");
                parent.put("from", id);
                parentIds.add(id);
                statementsIndex.add(hasChildren);
            }

            if (StringUtils.equals(linkname, "dcterms:isPartOf")) {
                RestIndex index = restapi.getIndex("edmsearch2");
                if (index.get("rdf_about", reference).size() > 0) {
                    ObjectNode hasParent = JsonNodeFactory.instance.objectNode();

                    ObjectNode parent = hasParent.with("parameters");

                    hasParent.put("statement",
                            "start n = node:edmsearch2 (rdf_about = {from}) SET n.hasParent={to} return n");
                    parent.put("to", reference);
                    parent.put("from", id);

                    parentCreationIndex.add(hasParent);
                }
            }
            if (i == 100) {
                Logger.getLogger(this.getClass().getName()).info("Reached 1000 in " + (System.currentTimeMillis()
                        - start) + " ms");
                start = System.currentTimeMillis();
                try {
                    String str = new ObjectMapper().writeValueAsString(obj);
                    PostMethod httpMethod = new PostMethod(
                            restapi.getBaseUri() + "/transaction/commit");
                    httpMethod.setRequestBody(str);
                    httpMethod.setRequestHeader("content-type",
                            "application/json");
                    httpMethod.setRequestHeader("X-Stream", "true");
                    httpClient.executeMethod(httpMethod);
                    statements.removeAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String str = new ObjectMapper().writeValueAsString(objIndex);
                    PostMethod httpMethod = new PostMethod(
                            restapi.getBaseUri() + "/transaction/commit");
                    httpMethod.setRequestBody(str);
                    httpMethod.setRequestHeader("content-type",
                            "application/json");
                    httpMethod.setRequestHeader("X-Stream", "true");
                    httpClient.executeMethod(httpMethod);
                    statementsIndex.removeAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String str = new ObjectMapper().writeValueAsString(parentIndex);
                    PostMethod httpMethod = new PostMethod(
                            restapi.getBaseUri() + "/transaction/commit");
                    httpMethod.setRequestBody(str);
                    httpMethod.setRequestHeader("content-type",
                            "application/json");
                    httpMethod.setRequestHeader("X-Stream", "true");
                    httpClient.executeMethod(httpMethod);

                    parentCreationIndex.removeAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i = 0;
            }

            i++;

        }
        try {
            String str = new ObjectMapper().writeValueAsString(obj);
            PostMethod httpMethod = new PostMethod(
                    restapi.getBaseUri() + "/transaction/commit");
            httpMethod.setRequestBody(str);
            httpMethod.setRequestHeader("content-type", "application/json");
            httpClient.executeMethod(httpMethod);
            statements.removeAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String str = new ObjectMapper().writeValueAsString(objIndex);
            PostMethod httpMethod = new PostMethod(
                    restapi.getBaseUri() + "/transaction/commit");
            httpMethod.setRequestBody(str);
            httpMethod.setRequestHeader("content-type",
                    "application/json");
            httpMethod.setRequestHeader("X-Stream", "true");
            httpClient.executeMethod(httpMethod);
            statementsIndex.removeAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String str = new ObjectMapper().writeValueAsString(parentIndex);
            PostMethod httpMethod = new PostMethod(
                    restapi.getBaseUri() + "/transaction/commit");
            httpMethod.setRequestBody(str);
            httpMethod.setRequestHeader("content-type",
                    "application/json");
            httpMethod.setRequestHeader("X-Stream", "true");
            httpClient.executeMethod(httpMethod);
            System.out.println(str);
            parentIndex.removeAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        relationsmap.get(mnemonic).clear();
        edmelementsmap.get(mnemonic).clear();
        if (retNodeMap != null && retNodeMap.get(mnemonic) != null) {
            retNodeMap.get(mnemonic).clear();
        }

        try {
            FileUtils.writeLines(new File("parents-" + mnemonic), parentIds);
        } catch (IOException ex) {
            Logger.getLogger(GraphConstructor.class.getName()).log(Level.SEVERE, null, ex);
        }
        createIsFirstInSequence(parents.get(mnemonic));
        createIsLastInSequence(parents.get(mnemonic));
        //TODO: temp removal because of unsupported functionality on the production side
//        createFakeSequence(parents.get(mnemonic));
    }

    private void createFakeSequence(Set<String> parents) {
        HttpClient httpClient = new HttpClient();
        for (String parent : parents) {
            GetMethod method = new GetMethod(StringUtils.remove(restapi.getBaseUri(), "/db/data/") + "/order/fakeorder/nodeId/" + StringUtils.replace(parent, "/", "%2F"));
            try {
                System.out.println(method.getPath());
                httpClient.executeMethod(method);
            } catch (IOException ex) {
                Logger.getLogger(GraphConstructor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String processEntityID(String id) {

        String prid = id.replace("http://data.europeana.eu", "");
        prid = prid.replace("/item/", "/");
        return prid;
    }

    public void populateDeletionCandidates(String id, String mnemonic) {
        List<String> deletionCandidatesCollection = deletionCandidates
                .get(mnemonic);

        if (deletionCandidatesCollection == null) {
            deletionCandidatesCollection = new ArrayList<>();
        }
        deletionCandidatesCollection.add(id);
        deletionCandidates.put(mnemonic, deletionCandidatesCollection);
    }

    public void deleteNodes(String mnemonic) {

        removeRelationships(mnemonic);
        removeNodes(mnemonic);
        if (deletionCandidates != null
                && deletionCandidates.get(mnemonic) != null) {
            deletionCandidates.get(mnemonic).clear();
        }
    }

    public void removeFromIndex(String mnemonic) {
        final List<Node> tempList = new ArrayList<Node>();
        int i = 0;
        RestIndex<Node> restIndex = restapi.getIndex(index);
        List<String> deleNodes = deletionCandidates.get(mnemonic);
        if (deleNodes != null) {
            for (String node : deleNodes) {

                IndexHits<Node> retNodes = restIndex.get("rdf_about", node);
                if (retNodes.size() > 0) {
                    tempList.add(retNodes.getSingle());
                }
                if (tempList.size() == 100 || i == deleNodes.size()) {

                    tx = restapi.beginTx();
                    for (Node tempNode : tempList) {
                        restIndex.remove(tempNode);
                    }
                    tempList.clear();
                    tx.success();
                    tx.finish();

                }

                i++;
            }
        }
    }

    public void removeRelationships(String mnemonic) {
        final Set<Relationship> relationships = new HashSet<Relationship>();

        RestIndex<Node> restIndex = restapi.getIndex(index);
        List<String> deleNodes = deletionCandidates.get(mnemonic);
        if (deleNodes != null) {
            for (String node : deleNodes) {
                IndexHits<Node> retNodes = restIndex.get("rdf_about", node);
                if (retNodes.size() > 0) {

                    Iterable<Relationship> rels = retNodes.getSingle()
                            .getRelationships();
                    Iterator<Relationship> relIterator = rels.iterator();
                    while (relIterator.hasNext()) {
                        relationships.add(relIterator.next());

                    }
                    if (relationships.size() >= 100) {
                        removeRelationships(relationships);
                    }
                }

            }
            removeRelationships(relationships);
        }
    }

    private void removeRelationships(final Set<Relationship> relationships) {
        tx = restapi.beginTx();
        restapi.executeBatch(new BatchCallback<Node>() {

            @Override
            public Node recordBatch(RestAPI batchRestApi) {
                for (Relationship node : relationships) {
                    node.delete();

                }
                return null;
            }

        });
        relationships.clear();
        tx.success();
        tx.finish();
    }

    public void removeNodes(String mnemonic) {
        final List<Node> tempList = new ArrayList<Node>();
        RestIndex<Node> restIndex = restapi.getIndex(index);
        List<String> deleNodes = deletionCandidates.get(mnemonic);
        if (deleNodes != null) {
            for (String node : deleNodes) {
                IndexHits<Node> retNodes = restIndex.get("rdf_about", node);
                if (retNodes.size() > 0) {
                    tempList.add(retNodes.getSingle());

                    if (tempList.size() >= 100) {
                        remNodes(tempList);
                    }
                }

            }
            remNodes(tempList);
        }
    }

    private void remNodes(final List<Node> nodes) {
        RestIndex<Node> restIndex = restapi.getIndex(index);
        tx = restapi.beginTx();
        for (Node nd : nodes) {
            restIndex.remove(nd);
        }

        tx.success();
        tx.finish();
        tx = restapi.beginTx();
        restapi.executeBatch(new BatchCallback<Node>() {

            @Override
            public Node recordBatch(RestAPI batchRestApi) {
                for (Node node : nodes) {
                    node.delete();

                }
                return null;
            }

        });

        tx.success();
        tx.finish();

        nodes.clear();
    }
}
