package org.techtown.practice.recycler_MyWritings;

import android.net.Uri;

public class MyWritingsData {
    private String index, title, txt_content;
    private Uri uri;

    public void setTxt_content(String txt_content) {
        this.txt_content = txt_content;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public String getTxt_content() {
        return txt_content;
    }

    public String getIndex() {  return index; }

    public void setIndex(String index) { this.index = index; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return txt_content;
    }

    public void setContent(String txt_content) {
        this.txt_content = txt_content;
    }
}
