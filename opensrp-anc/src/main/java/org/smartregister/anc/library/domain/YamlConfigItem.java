package org.smartregister.anc.library.domain;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class YamlConfigItem {

    public static final String FIELD_CONTACT_SUMMARY_ITEMS = "contactSummaryItems";

    private String template;
    private String relevance;
    private String isRedFont;
    private Boolean isMultiWidget;

    public YamlConfigItem() {
    }

    public YamlConfigItem(String template, String relevance, String isRedFont) {
        this.template = template;
        this.relevance = relevance;
        this.isRedFont = isRedFont;
        this.isMultiWidget = false;
    }

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

    public boolean isMultiWidget() {
        return isMultiWidget;
    }

    public void setIsMultiWidget(boolean multiWidget) {
        this.isMultiWidget = multiWidget;
    }
}
