package eu.europeana.uim.enrichment.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.corelib.solr.entity.ConceptImpl;
import eu.europeana.corelib.solr.entity.ContextualClassImpl;
import eu.europeana.corelib.solr.entity.PlaceImpl;
import eu.europeana.corelib.solr.entity.TimespanImpl;

/**
 * Cache implementation for enrichment entities
 *
 * @author Yorgos.Mamakis@ europeana.eu
 */
public class EntityCache {

    private static Map<String, EntityEntry> entriesByLabel = Collections
            .synchronizedMap(new HashMap<String, EntityEntry>());
    private static Map<String, EntityEntry> entriesByUri = Collections
            .synchronizedMap(new HashMap<String, EntityEntry>());

    /**
     * Check whether the cache contains an entry for the given label
     *
     * @param label
     * @return
     */
    public boolean containsEntity(String label) {
        if (entriesByLabel.containsKey(label)) {
            return true;
        }
        return false;
    }

    private <T> void addEntity(String label, String uri, T entity) {
        EntityEntry entry = new EntityEntry();
        entry.setIndex(label);
        entry.setClassName(entity.getClass().getName());
        entry.setEntityWrapper(entity);
        entriesByUri.put(uri, entry);

        if (label != null) {
            EntityEntry entryLabel = new EntityEntry();
            entryLabel.setIndex(uri);
            entryLabel.setClassName(entity.getClass().getName());
            entryLabel.setEntityWrapper(entity);
            entriesByLabel.put(label, entryLabel);
        }
    }

    /**
     * Add an entity to the cache
     *
     * @param entities
     */
    public void addEntities(List<RetrievedEntity> entities) {
        for (RetrievedEntity entity : entities) {
            addEntity(entity.getOriginalLabel() != null ? entity.getOriginalLabel().toLowerCase() : null, entity.
                    getUri(),
                    entity.getEntity());
        }
    }

    /**
     * Method for retrieving Enriched entities based on a label for a given field
     *
     * @param label
     * @param originalField
     * @return
     */
    public <T> List<RetrievedEntity> retrieveEntities(String label,
            String originalField) {
        List<RetrievedEntity> entities = new ArrayList<RetrievedEntity>();
        T entity = (T) entriesByLabel.get(label).getEntityWrapper();
        RetrievedEntity retEntity = new RetrievedEntity();
        retEntity.setEntity(entity);

        retEntity.setOriginalField(originalField);
        entities.add(retEntity);
        while (entity != null) {
            ContextualClassImpl retrieved = (ContextualClassImpl) getParentEntity(entity);
            if (retrieved != null && !((ContextualClassImpl) entity).getAbout().equals(retrieved.getAbout())) {
                entity = (T) retrieved;
                if (entity != null) {
                    RetrievedEntity parentEntity = new RetrievedEntity();
                    parentEntity.setEntity(entity);
                    entities.add(parentEntity);
                }
            } else {
                entity = null;
            }
        }
        return entities;
    }

    private <T> T getParentEntity(T entity) {
        if (entity.getClass().getName().equals(TimespanImpl.class.getName())) {
            return getTimespanParent(entity);
        }
        if (entity.getClass().getName().equals(PlaceImpl.class.getName())) {
            return getPlaceParent(entity);
        }
        if (entity.getClass().getName().equals(ConceptImpl.class.getName())) {
            return getConceptParent(entity);
        }
        return null;
    }

    private <T> T getConceptParent(T entity) {

        ConceptImpl concept = (ConceptImpl) entity;
        if (concept.getBroader() != null && concept.getBroader().length > 0) {
            return entriesByUri.get(concept.getBroader()[0]) != null
                    ? (T) entriesByUri
                    .get(concept.getBroader()[0]).getEntityWrapper() : null;
        }

        return null;
    }

    private <T> T getPlaceParent(T entity) {
        PlaceImpl place = (PlaceImpl) entity;
        if (place.getIsPartOf() != null) {
            return place.getIsPartOf().get("def") != null && entriesByUri
                    .get(place.getIsPartOf().get("def").get(0)) != null ? (T) entriesByUri.get(place.getIsPartOf().get(
                                    "def").get(0)).
                    getEntityWrapper() : null;

        }

        return null;
    }

    private <T> T getTimespanParent(T entity) {
        TimespanImpl ts = (TimespanImpl) entity;
        if (ts.getIsPartOf() != null) {
            return ts.getIsPartOf().get("def") != null && entriesByUri
                    .get(ts.getIsPartOf().get("def").get(0)) != null
                    ? (T) entriesByUri
                    .get(ts.getIsPartOf().get("def").get(0)).getEntityWrapper()
                    : null;
        }
        return null;
    }

}
