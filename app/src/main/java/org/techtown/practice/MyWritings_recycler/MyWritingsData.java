package org.techtown.practice.MyWritings_recycler;

public class MyWritingsData {

    private String title, content, date, writer, id;

    public MyWritingsData(String title, String content, String tag, String date, String writer, String id) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.writer = writer;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
