package com.example.qianxun.qianxuncomic.model;

/**
 * Created by sllEc_000 on 2017/2/26 0026.
 */

public class Download {
    private int download_id;
    private String comic_name;
    private String chapter_name;

    public int getDownload_id() {
        return download_id;
    }

    public void setDownload_id(int download_id) {
        this.download_id = download_id;
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
