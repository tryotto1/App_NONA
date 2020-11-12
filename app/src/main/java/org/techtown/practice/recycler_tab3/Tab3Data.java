package org.techtown.practice.recycler_tab3;

public class Tab3Data {

    private String title, content, date, writer, email;
    private int id_num;

    public Tab3Data(String title, String content, String date, String writer, String email, int id_num) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.writer = writer;
        this.email = email;
        this.id_num = id_num;
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

    public String getEmail() {
        return email;
    }

    public void setEmail() {
        this.email = email;
    }

    public int getId_num() {
        return id_num;
    }

    public void setId_num(int id_num) {
        this.id_num = id_num;
    }
}
