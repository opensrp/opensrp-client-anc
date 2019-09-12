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
        this.isMultiWidget = Boolean.FALSE;
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

    public Boolean isMultiWidget() {
        if (isMultiWidget == null) {
            setIsMultiWidget(Boolean.FALSE);
        }

        return isMultiWidget;
    }

    public void setIsMultiWidget(Boolean multiWidget) {
        this.isMultiWidget = multiWidget;
    }
}
