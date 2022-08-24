package org.smartregister.anc.library.model;

public class LibraryContent {
    private String contentHeader;
    private String filename;

    public LibraryContent(String contentHeader, String filename) {
        this.contentHeader = contentHeader;
        this.filename = filename;
    }

    public String getContentHeader() {
        return contentHeader;
    }

    public String getContentFile() {
        return "file:///android_asset/library_content/" + filename + ".html";
    }

    public void setContentHeader(String contentHeader) {
        this.contentHeader = contentHeader;
    }
}
