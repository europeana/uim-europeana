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
package eu.europeana.uim.gui.cp.shared.validation;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Value for a facet together with occurrence count.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @since May 27, 2011
 */
public class FacetValueDTO implements IsSerializable {
    private String value;
    private int    count;

    /**
     * Creates a new instance of this class.
     */
    public FacetValueDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param value
     * @param count
     */
    public FacetValueDTO(String value, int count) {
        this.value = value;
        this.count = count;
    }

    /**
     * @return facet value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return count of occurrence of the facet value
     */
    public int getCount() {
        return count;
    }
}