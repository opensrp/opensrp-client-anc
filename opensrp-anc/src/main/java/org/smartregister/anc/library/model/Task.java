package org.smartregister.anc.library.model;

public class Task {

    private Long id;
    private String baseEntityId;
    private String key;
    private String value;
    private Long createdAt;
    private boolean isUpdated;
    private boolean isComplete;

    public Task() {
    }

    public Task(String baseEntityId, String key, String value, boolean isUpdated, boolean isComplete) {
        this.baseEntityId = baseEntityId;
        this.key = key;
        this.value = value;
        this.isUpdated = isUpdated;
        this.isComplete = isComplete;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
