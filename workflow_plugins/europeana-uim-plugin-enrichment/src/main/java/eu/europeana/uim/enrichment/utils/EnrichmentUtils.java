package eu.europeana.uim.enrichment.utils;

import eu.europeana.enrichment.api.external.InputValue;
import eu.europeana.uim.enrichment.enums.EnrichmentFields;
import eu.europeana.uim.enrichment.normalizer.AgentNormalizer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.solr.common.SolrInputDocument;

import java.util.ArrayList;
import java.util.List;

public class EnrichmentUtils {

    private static UrlValidator validator = new UrlValidator();

    public List<InputValue> createValuesForEnrichment(
            SolrInputDocument basicDocument) {
        List<InputValue> inputValueList = new ArrayList<InputValue>();
        for (String fieldName : basicDocument.keySet()) {
            for (EnrichmentFields field : EnrichmentFields.values()) {
                if (!validator.isValid(field.getValue())) {
                    if (StringUtils.equals(field.getValue(), fieldName)
                            || StringUtils.startsWith(fieldName, field.getValue())) {

                        if (field.equals(EnrichmentFields.DC_CREATOR)
                                || field.equals(EnrichmentFields.DC_CONTRIBUTOR)) {
                            for (String str : AgentNormalizer
                                    .normalize(basicDocument
                                            .getFieldValues(fieldName))) {
                                InputValue inValue = new InputValue();
                                inValue.setOriginalField(field.getValue());
                                inValue.setValue(str);
                                inValue.setVocabularies(field.getVocabularies());
                                inputValueList.add(inValue);
                            }
                        } else {
                            for (Object str : basicDocument
                                    .getFieldValues(fieldName)) {
                                InputValue inValue = new InputValue();
                                inValue.setOriginalField(field.getValue());
                                inValue.setLanguage(StringUtils.substringAfter(fieldName,field.getValue()+"."));
                                inValue.setValue(str.toString());
                                inValue.setVocabularies(field.getVocabularies());
                                inputValueList.add(inValue);
                            }
                        }
                    }
                }
            }
        }
        return inputValueList;
    }
}
