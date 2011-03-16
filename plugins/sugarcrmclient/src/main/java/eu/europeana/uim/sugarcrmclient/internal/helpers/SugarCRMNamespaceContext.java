package eu.europeana.uim.sugarcrmclient.internal.helpers;

import java.util.Iterator;
import javax.xml.*;
import javax.xml.namespace.NamespaceContext;

public class SugarCRMNamespaceContext implements NamespaceContext {

    public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("ns1".equals(prefix)) return "http://www.sugarcrm.com/sugarcrm";
        else if ("xsi".equals(prefix)) return "http://www.w3.org/2001/XMLSchema-instance";    
        else if ("SOAP-ENC".equals(prefix)) return "http://schemas.xmlsoap.org/soap/encoding/";  
        
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
    }

    // This method isn't necessary for XPath processing.
    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    // This method isn't necessary for XPath processing either.
    public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }

}