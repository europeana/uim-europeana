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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.mapdb.DBMaker;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.google.code.morphia.Datastore;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.definitions.solr.entity.AbstractEdmEntity;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;

import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.corelib.solr.server.Neo4jServer;
import eu.europeana.corelib.solr.utils.EdmUtils;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.neo4j.rest.graphdb.index.RestIndex;

/**
 * @author Georgios Markakis (gwarkx@hotmail.com)
 *
 * Mar 13, 2014
 */
public class GraphConstructorSpring extends DefaultHandler implements Neo4jServer {

    private static final String DB_PATH = "/home/geomark/Software/neo4j-community-1.9.5/data/graph.db";

    ConcurrentHashMap<String, Map<String, String>> relationsmap;
    ConcurrentHashMap<String, Map<String, Object>> edmelementsmap;
    ConcurrentHashMap<String, RestNode> nodeCache = new ConcurrentHashMap<String, RestNode>();
    private String currentID;
    private String currentType;

    private String currentNonRootElementName;
    private String currentLang;
    private StringBuffer curCharValue = new StringBuffer(1024);

    private EDMRepositoryService edmservice;
    private Neo4jTemplate template;
    private static Mongo mongo;
    private static Datastore ds = null;
    private static DB db = null;
    private Transaction tx;

    GraphDatabaseService graphDb;
    private static Index<Node> nodeIndex;

    RestAPI restapi;

    /**
     *
     */
    GraphConstructorSpring() {
        this.edmservice = new EDMRepositoryService();
        this.template = edmservice.getTemplate();
        init();
    }

    /**
     *
     */
    GraphConstructorSpring(EDMRepositoryService edmservice) {
        this.edmservice = edmservice;
        this.template = edmservice.getTemplate();
        init();
    }

    public void init() {
        try {
            graphDb = template.getGraphDatabaseService();
            tx = graphDb.beginTx();

            org.mapdb.DB mdb = DBMaker.newMemoryDB().make();
            relationsmap = new ConcurrentHashMap<String, Map<String, String>>();
            edmelementsmap = new ConcurrentHashMap<String, Map<String, Object>>();

//	    Morphia morphia = new Morphia();
//		morphia.map(ProxyImpl.class);
//		morphia.map(AgentImpl.class);
//		morphia.map(ConceptImpl.class);
//		morphia.map(TimespanImpl.class);
//		morphia.map(PlaceImpl.class);
//		morphia.map(ProvidedCHOImpl.class);
//		morphia.map(AggregationImpl.class);
//		ds = morphia.createDatastore(mongo, "europeana");
            RestGraphDatabase gdb = (RestGraphDatabase) graphDb;
            restapi = gdb.getRestAPI();

        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(AbstractEdmEntity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends AbstractEdmEntity> T find(String ID, T obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(String ID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void createBatch(final List<Rel> rels) {
        restapi.executeBatch(new BatchCallback<RestNode>() {

            @Override
            public RestNode recordBatch(RestAPI batchRestApi) {
                for (Rel rel : rels) {
                    try {

                        rel.getNodeFrom().createRelationshipTo(rel.getNodeTo(), rel.
                                getRelType());
                        //tx.success();

                    } catch (Exception e) {
                       System.out.println(e.getMessage() +" : " + rel.getNodeFrom().getProperty("rdf:about") +  " " +rel.getNodeTo().getProperty("rdf:about"));
                    }
                }
                return null;
            }
        });
    }

    /**
     * @author Georgios Markakis (gwarkx@hotmail.com)
     *
     * Nov 29, 2013
     */
    private class RelType implements RelationshipType {

        private String name;

        /**
         * @param name
         */
        public RelType(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

    }

    private class Rel {

        private Node nodeFrom;
        private Node nodeTo;
        private RelType relType;

        public Rel(Node nodeFrom, Node nodeTo, RelType relType) {
            this.nodeFrom = nodeFrom;
            this.nodeTo = nodeTo;
            this.relType = relType;
        }

        public Node getNodeFrom() {
            return nodeFrom;
        }

        public Node getNodeTo() {
            return nodeTo;
        }

        public RelType getRelType() {
            return relType;
        }
    }

    public void parseMorphiaEntity(FullBeanImpl fullbean) {

        if (fullbean != null) {
            String rdf = EdmUtils.toEDM(fullbean, false);

            //Extract references from XML
            parseXML(rdf);


            /*
             final List<AbstractEdmEntity> total = new LinkedList<AbstractEdmEntity>();
			
             List<AgentImpl> agents = fullbean.getAgents();
             List<ConceptImpl> concepts = fullbean.getConcepts();			
             List<TimespanImpl> timespans = fullbean.getTimespans();
             List<PlaceImpl> places = fullbean.getPlaces();
             List<ProvidedCHOImpl> prchos = fullbean.getProvidedCHOs();
             List<AggregationImpl> aggs = fullbean.getAggregations();
             EuropeanaAggregation euagg = fullbean.getEuropeanaAggregation();
			
             List<ProxyImpl> proxies = fullbean.getProxies();
						
             total.addAll(agents);
             total.addAll(concepts);
             total.addAll(timespans);
             total.addAll(places);
             total.addAll(prchos);
             total.addAll(aggs);
             total.add(euagg);
             total.addAll(proxies);
			
             List<WebResourceImpl> webresources = new ArrayList<WebResourceImpl>(); 
			
             for(AggregationImpl agg : aggs){
             List<? extends WebResource> wresources = agg.getWebResources();
             webresources.addAll((Collection<? extends WebResourceImpl>) wresources);
             }
			    
             total.addAll(webresources);
			
             for(AbstractEdmEntity entity : total){
             System.out.println("Saving Neo4jBean:" + entity.getAbout() + " : : " + entity.getClass());
             saveNeo4jBean(entity);
             }
             */
            //saveNeo4jBean(total);
        }

    }

    /**
     * @param xml
     */
    public synchronized void parseXML(String xml) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse(new InputSource(new StringReader(xml)), this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void save() {
        tx.success();
        tx.finish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String namespaceURI, String sName, // simple name
            String qName, // qualified name
            Attributes attrs) throws SAXException {

        //Collect the attribute references here
        if (qName.equalsIgnoreCase("edm:Proxy")
                || qName.equalsIgnoreCase("ore:Proxy")
                || qName.equalsIgnoreCase("edm:ProvidedCHO")
                || qName.equalsIgnoreCase("edm:WebResource")
                || qName.equalsIgnoreCase("edm:Agent")
                || qName.equalsIgnoreCase("edm:Place")
                || qName.equalsIgnoreCase("edm:TimeSpan")
                || qName.equalsIgnoreCase("skos:Concept")
                || qName.equalsIgnoreCase("ore:Aggregation")
                || qName.equalsIgnoreCase("edm:EuropeanaAggregation")) {

            currentID = processEntityID(attrs.getValue("rdf:about"));
            currentType = qName;

            Map<String, Object> nodedatamap = new ConcurrentHashMap<String, Object>();
            nodedatamap.put("rdf:about", currentID);

            nodedatamap.put("__type__", currentType);
            edmelementsmap.put(currentID, nodedatamap);
        } else {
            currentNonRootElementName = qName;

        }

        String eName = sName; // element name
        if ("".equals(eName)) {
            eName = qName; // not namespace-aware
        }
        if (attrs != null) {

            Map<String, String> referencelist = currentID != null ? relationsmap.get(currentID)
                    : new ConcurrentHashMap<String, String>();
            if (referencelist == null) {
                referencelist = new ConcurrentHashMap<String, String>();
            }
            if (currentID != null) {
                relationsmap.put(currentID, referencelist);
            }

            for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getQName(i); //getLocalName(i); // Attr name

                //System.out.println("aName:" + aName);
                if (aName.equals("rdf:resource")) {
                    //System.out.println("Found resource:" + processEntityID(attrs.getValue(i)));
                    referencelist.put(processEntityID(attrs.getValue(i)), eName);
                }

                if ("".equals(aName)) {
                    aName = attrs.getQName(i);
                }

            }

            if (currentID != null) {
                relationsmap.put(currentID, referencelist);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String namespaceURI, String sName, // simple name
            String qName // qualified name
    ) throws SAXException {
        String eName = sName; // element name
        if ("".equals(eName)) {
            eName = qName; // not namespace-aware
        }
        if (qName.equalsIgnoreCase("edm:Proxy")
                || qName.equalsIgnoreCase("ore:Proxy")
                || qName.equalsIgnoreCase("edm:ProvidedCHO")
                || qName.equalsIgnoreCase("edm:WebResource")
                || qName.equalsIgnoreCase("edm:Agent")
                || qName.equalsIgnoreCase("edm:Place")
                || qName.equalsIgnoreCase("edm:TimeSpan")
                || qName.equalsIgnoreCase("skos:Concept")
                || qName.equalsIgnoreCase("ore:Aggregation")
                || qName.equalsIgnoreCase("edm:EuropeanaAggregation")) {
        } else {
            Map<String, Object> nodedatamap = edmelementsmap.get(currentID);

            if (nodedatamap == null) {
                nodedatamap = new ConcurrentHashMap<String, Object>();
                edmelementsmap.put(currentID, nodedatamap);
            }
            if (currentNonRootElementName != null) {
                nodedatamap.get(currentNonRootElementName);
            }
            if (currentNonRootElementName != null && curCharValue != null) {
                nodedatamap.put(currentNonRootElementName, curCharValue.toString());
                edmelementsmap.put(currentID, nodedatamap);
            }

            currentNonRootElementName = null;
            curCharValue = null;

        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        //already synchronized
        if (ch != null) {
            if (curCharValue == null) {
                curCharValue = new StringBuffer(1024);
            }
            curCharValue.append(ch, start, length);
        }
    }

    public void generateNodes() {
        System.out.println("Generating nodes");
        final ConcurrentHashMap<String, Map<String,Object>> map = new ConcurrentHashMap<String, Map<String, Object>>();
        int i=0;
        for(String key : edmelementsmap.keySet()){
        map.put(key, edmelementsmap.get(key));
        i++;
        if(map.size()==100|| edmelementsmap.size()==i){
        RestNode relationship = restapi.executeBatch(new BatchCallback<RestNode>() {

            @Override
            public RestNode recordBatch(RestAPI batchRestApi) {
                Set<String> idset = map.keySet();
                RestIndex<Node> index = restapi.getIndex("edmsearch2");
                
                Iterator<String> idesetiterator = idset.iterator();
                while (idesetiterator.hasNext()) {

                    Map<String, Object> nodemap = map.get(idesetiterator.next());
                    if (nodemap.get("rdf:about") != null) {
                        System.out.println("Generating element:" + nodemap.get("rdf:about"));

//                        RestNode node2 = batchRestApi.createNode(nodemap);
                      
                        RestNode node2 = batchRestApi.getOrCreateNode(index, "rdf:about", nodemap.get("rdf:about"), nodemap);
                        nodeCache.put((String) nodemap.get("rdf:about"), node2);
                        index.add(node2, "rdf:about", nodemap.get("rdf:about"));
                    }
                }
                return null;
            }

        });
        map.clear();
        }
        }
    }

    /**
     *
     */
    public void generateNodeLinks2() {
        System.out.println("Generating links");
        Iterator<String> it = relationsmap.keySet().iterator();

        if (nodeIndex == null) {
            nodeIndex = template.getIndex("edmsearch2");
        }

        while (it.hasNext()) {

            String id = processEntityID(it.next());
            System.out.println("Trying to retrieve Node with ID:" + id);

            Node nd = nodeIndex.get("rdf:about", id).getSingle();

            if (nd == null) {
                System.out.println("Unfortunately nothing returned here:" + id);
            } else {
                System.out.println("Success!!");
            }

            Map<String, String> values = relationsmap.get(id);

            Set<String> set = values.keySet();

            Iterator<String> it2 = set.iterator();

            while (it2.hasNext()) {
                String reference = processEntityID(it2.next());
                String linkname = values.get(reference);

                System.out.println("Trying to retieve Reference (" + linkname + "): " + reference + " | of" + id);

                //Transaction tx = graphDb.beginTx();
                try {
                    Node ndref = nodeIndex.get("rdf:about", reference).getSingle();
                    if (ndref != null) {
                        System.out.println("Found!" + reference);
                        Relationship relationship = nd.createRelationshipTo(ndref, new RelType(linkname));
                        //tx.success();
                    } else {
                        System.out.println("Tzifos");
                    }

                } finally {
                    //tx.finish();
                }
            }
        }
    }

    public void generateNodeLinks3() {
        Iterator<String> it = relationsmap.keySet().iterator();
        List<Rel> rels = new ArrayList<Rel>();
        while (it.hasNext()) {
            final String id = processEntityID(it.next());
            nodeIndex = restapi.getIndex("edmsearch2");
            System.out.println("Trying to retrieve Node with ID:" + id);
            final Node nd = nodeIndex.get("rdf:about", id).getSingle();

            if (nd == null) {
                System.out.println("Unfortunately nothing returned here:" + id);
            } else {
                System.out.println("Success!!");
            }

            Map<String, String> values = relationsmap.get(id);

            Set<String> set = values.keySet();

            Iterator<String> it2 = set.iterator();

            while (it2.hasNext()) {
                final String reference = processEntityID(it2.next());
                final String linkname = values.get(reference);

                System.out.
                        println("Trying to retieve Reference (" + linkname + "): " + reference + " | of" + id);
                final Node ndref = nodeIndex.get("rdf:about", reference).getSingle();
                if (ndref != null) {
                    System.out.println("Found!" + reference);
                    Rel rel = new Rel(nd, ndref, new RelType(linkname));
                    rels.add(rel);
                    if (rels.size() == 100) {
                        createBatch(rels);
                        rels = new ArrayList<Rel>();
                    }
                } else {
                    System.out.println("Tzifos");
                }
                //Transaction tx = graphDb.beginTx();

            }
            createBatch(rels);
            rels = new ArrayList<Rel>();

        }
    }

    public void generateNodeLinks() {
        int i=0;
        Iterator<String> it = relationsmap.keySet().iterator();
        System.out.println("Relations size"  + relationsmap.keySet().size());
        System.out.println("Cache size"  + nodeCache.size());
        List<Rel> rels = new ArrayList<Rel>();
        while (it.hasNext()) {
            final String id = processEntityID(it.next());

            System.out.println("Trying to retrieve Node with ID:" + id);
            final Node nd = nodeCache.get(id);

            if (nd == null) {
                System.out.println("Unfortunately nothing returned here:" + id);
            } else {
                System.out.println("Success!!");
            }
            if(nd!=null){
            Map<String, String> values = relationsmap.get(id);

            Set<String> set = values.keySet();

            Iterator<String> it2 = set.iterator();
            
            while (it2.hasNext()) {
                
                final String reference = processEntityID(it2.next());
                final String linkname = values.get(reference);

                System.out.
                        println("Trying to retrieve Reference (" + linkname + "): " + reference + " | of" + id);
                final Node ndref = nodeCache.get(reference);
                if (ndref != null) {
                    i+=1;
                    System.out.println("Found!" + reference);
                    Rel rel = new Rel(nd, ndref, new RelType(linkname));
                    rels.add(rel);
                    if (rels.size() == 100) {
                        
                        createBatch(rels);
                        rels.clear();
                    }
                    
                } else {
                    System.out.println("Reference not found");
                }
                System.out.println(i);
                //Transaction tx = graphDb.beginTx();

            }
           
            }
            
        }
         createBatch(rels);
         rels.clear();
         nodeCache.clear();
    }

    /**
     *
     */
    public void clearDb() {
        try {
            FileUtils.deleteRecursively(new File(DB_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param bean
     */
    public void saveNeo4jBean(AbstractEdmEntity bean) {

        edmservice.getEdmrepository().save(bean);

    }

    /**
     * @param bean
     */
    public void saveNeo4jBean(List<AbstractEdmEntity> beans) {

        edmservice.getEdmrepository().save(beans);

    }

    /**
     * @param id
     * @return
     */
    public Node retrieveNeo4jEntity(String field, String id) {
        Node nd = nodeIndex.get(field, id).getSingle();
        return nd;
    }

    /**
     * @param ID
     * @return
     */
    public ProxyImpl retrieveMorphiaEntity(String ID) {
        ProxyImpl proxy = ds.find(ProxyImpl.class).filter("about", ID).get();
        return proxy;
    }

    private String processEntityID(String id) {

        String prid = id.replace("http://data.europeana.eu", "");
        prid = prid.replace("/item", "");
        return prid;
    }

}
