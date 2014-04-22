package eu.europeana.uim.enrichment.utils;

/**
 * Entity Entry bean
 *
 * @author Yorgos.Mamakis@ europeana.eu
 */
public class EntityEntry<T> {

    private String index;

    private T entityWrapper;

    private String className;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public T getEntityWrapper() {
        return entityWrapper;
    }

    public void setEntityWrapper(T entityWrapper) {
        this.entityWrapper = entityWrapper;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
