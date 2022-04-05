package org.smartregister.anc.library.domain;

import android.content.Context;

import com.vijay.jsonwizard.domain.Form;

import org.checkerframework.checker.units.qual.C;
import org.smartregister.anc.library.helper.ContactHelper;

import java.io.Serializable;
import java.util.Map;

public class Contact extends Form implements Serializable {

    private String id;
    private int background;
    private Integer requiredFields;
    private String formName;
    private int contactNumber;
    private String jsonForm;
    private Map<String, String> globals;
    private String title;

    private ContactHelper contactHelper;

    // --- Initialization

    // Initialize contact data
    public void init(Context context, String id, String formName) {
        this.id = id;
        this.contactHelper = new ContactHelper(context);
        // Contact name
        this.setName(this.contactHelper.getContactString(this.id));
        // Form
        this.setFormName(formName);
    }

    public String getId() {
        return this.id;
    }


    // --- Background

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    // --- Required Fields

    public Integer getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(int requiredFields) {
        this.requiredFields = requiredFields;
    }

    // --- Form Name

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getTitle() {
        if (this.title != null) {
            return this.title;
        }
        return this.getName();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // --- Globals

    public Map<String, String> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, String> globals) {
        this.globals = globals;
    }

    // --- Contact Number

    public int getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(int contactNumber) {
        this.contactNumber = contactNumber;
    }

    // --- JSON Form

    public String getJsonForm() {
        return jsonForm;
    }

    public void setJsonForm(String jsonForm) {
        this.jsonForm = jsonForm;
    }
}
