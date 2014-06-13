package eu.europeana.uim.neo4jplugin.impl;



import eu.europeana.corelib.definitions.solr.entity.AbstractEdmEntity;
import java.util.List;
import org.springframework.data.neo4j.support.Neo4jTemplate;


public class EDMRepository{
       
    private final ManagedTransaction transaction ;
    private final Neo4jTemplate template;
    
    public EDMRepository(Neo4jTemplate template){
        //super(AbstractEdmEntity.class, template);
        this.template = template;
        transaction = ManagedTransaction.getInstance(template.getGraphDatabase());
        
    }
    
  //  @Override
    public AbstractEdmEntity save(AbstractEdmEntity entity){
        transaction.prepareTransaction();
        template.save(entity);
        return entity;
    }

    
    public void save(List<AbstractEdmEntity> entity){
        transaction.prepareTransaction();
        template.save(entity);
    }
}