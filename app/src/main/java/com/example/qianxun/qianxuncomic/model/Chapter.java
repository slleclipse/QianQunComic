package com.example.qianxun.qianxuncomic.model;

import java.io.Serializable;

/**
 * Created by sllEc_000 on 2017/2/26 0026.
 */

public class Chapter implements Serializable{
    private int chapter_id;
    private int chapter;
    private String chapter_url;
    private String page_url;
    private String page_address;
    private String chapter_name;

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getChapter_url() {
        return chapter_url;
    }

    public void setChapter_url(String chapter_url) {
        this.chapter_url = chapter_url;
    }

    public String getPage_address() {
        return page_address;
    }

    public void setPage_address(String page_address) {
        this.page_address = page_address;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

}
