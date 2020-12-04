package org.techtown.practice.recylcler_MyChatList;

import android.net.Uri;

public class MyChatData {
    private String index, title, txt_content, flag_borrow, flag_buy, flag_give_back, writer, date;
    String write_date_exchange, write_place_exchange;
    private Uri uri;

    public void setWrite_date_exchange(String write_date_exchange) {
        this.write_date_exchange = write_date_exchange;
    }

    public void setWrite_place_exchange(String write_place_exchange) {
        this.write_place_exchange = write_place_exchange;
    }

    public String getWrite_date_exchange() {
        return write_date_exchange;
    }

    public String getWrite_place_exchange() {
        return write_place_exchange;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriter() {
        return writer;
    }

    public void setFlag_borrow(String flag_borrow) {
        this.flag_borrow = flag_borrow;
    }

    public void setFlag_buy(String flag_buy) {
        this.flag_buy = flag_buy;
    }

    public void setFlag_give_back(String flag_give_back) {
        this.flag_give_back = flag_give_back;
    }

    public String getFlag_borrow() {
        return flag_borrow;
    }

    public String getFlag_buy() {
        return flag_buy;
    }

    public String getFlag_give_back() {
        return flag_give_back;
    }

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
