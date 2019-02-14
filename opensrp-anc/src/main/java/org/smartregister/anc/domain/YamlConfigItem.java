package org.smartregister.anc.domain;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class YamlConfigItem {

    public static final String FIELD_CONTACT_SUMMARY_ITEMS = "contactSummaryItems";

    private String template;
    private String relevance;
    private String isRedFont;

    public String getIsRedFont() {
        return isRedFont;
    }

    public void setIsRedFont(String isRedFont) {
        this.isRedFont = isRedFont;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getRelevance() {
        return relevance;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }
}
