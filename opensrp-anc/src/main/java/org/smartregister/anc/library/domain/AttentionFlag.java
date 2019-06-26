package org.smartregister.anc.library.domain;

/**
 * Created by ndegwamartin on 10/08/2018.
 */
public class AttentionFlag {
    private String title;
    private boolean redFlag;

    public AttentionFlag(String title, boolean isRedFlag) {
        this.title = title;
        redFlag = isRedFlag;
    }

    public boolean isRedFlag() {
        return redFlag;
    }

    public void setRedFlag(boolean redFlag) {
        this.redFlag = redFlag;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
