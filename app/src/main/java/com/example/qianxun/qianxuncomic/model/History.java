package com.example.qianxun.qianxuncomic.model;

/**
 * Created by sllEc_000 on 2017/2/26 0026.
 */

public class History {
    private int history_id;
    private String comic_name;
    private String chapter_name;

    public int getHistory_id() {
        return history_id;
    }

    public void setHistory_id(int history_id) {
        this.history_id = history_id;
    }

    public String getComic_name() {
        return comic_name;
    }

    public void setComic_name(String comic_name) {
        this.comic_name = comic_name;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }
}
