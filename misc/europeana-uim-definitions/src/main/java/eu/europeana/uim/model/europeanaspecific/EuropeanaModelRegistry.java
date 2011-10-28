/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.model.europeanaspecific;

import eu.europeana.uim.common.TKey;
import eu.europeana.uim.edmcore.definitions.RDF;
import eu.europeana.uim.edmcore.definitions.ProvidedCHOType;
import eu.europeana.uim.edmcore.definitions.AgentType;
import eu.europeana.uim.edmcore.definitions.PlaceType;
import eu.europeana.uim.edmcore.definitions.TimeSpanType;


/**
 * TKeys Definitions for the Europeana Data Model
 * 
 * @author Georgios Markakis
 */
public final class EuropeanaModelRegistry {

    /** The key for all sorts of concepts.
     */
    public static final TKey<EuropeanaModelRegistry, String>  UNCLASSIFIED  = TKey.register(
    		                                                                 EuropeanaModelRegistry.class,
                                                                             "concept",
                                                                             String.class);
    /** The key for a full EDM Record representation
     */
    public static final TKey<EuropeanaModelRegistry, RDF>  EDMRECORD  = TKey.register(
    		                                                                 EuropeanaModelRegistry.class,
                                                                             "edmrecord",
                                                                             RDF.class);
    
    /** The key for the EDM Cultural Heritage Object Type
     */
    public static final TKey<EuropeanaModelRegistry, ProvidedCHOType>  PROVIDEDCHO  = TKey.register(
    		                                                                 EuropeanaModelRegistry.class,
                                                                             "providedcho",
                                                                             ProvidedCHOType.class);
    
    
    /** The key for the EDM Agent Type
     */
    public static final TKey<EuropeanaModelRegistry, AgentType>  AGENT  = TKey.register(
    		                                                                 EuropeanaModelRegistry.class,
                                                                             "agent",
                                                                             AgentType.class);
    
    
    /** The key for the EDM Place Type
     */
    public static final TKey<EuropeanaModelRegistry, PlaceType>  PLACE  = TKey.register(
    		                                                                 EuropeanaModelRegistry.class,
                                                                             "place",
                                                                             PlaceType.class);
    
    
    
    /** The key for the EDM TimeSpan Type
     */
    public static final TKey<EuropeanaModelRegistry, TimeSpanType>  TIMESPAN  = TKey.register(
    		                                                                 EuropeanaModelRegistry.class,
                                                                             "timespan",
                                                                             TimeSpanType.class);
    
    
	
}
