package org.smartregister.anc.domain;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ContactSummaryItem {

    private String name;
    private String template;
    private String relevance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
