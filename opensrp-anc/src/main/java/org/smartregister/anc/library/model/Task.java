package org.smartregister.anc.library.model;

public class Task {

    private Long id;
    private String baseEntityId;
    private String key;
    private String value;
    private String contactNo;
    private Long createdAt;

    public Task() {
    }

    public Task(String baseEntityId, String key, String value, String contactNo) {
        this.baseEntityId = baseEntityId;
        this.key = key;
        this.value = value;
        this.contactNo = contactNo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
