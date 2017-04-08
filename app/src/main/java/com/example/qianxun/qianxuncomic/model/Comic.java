/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.qianxun.qianxuncomic.model;

import java.io.Serializable;

public class Comic implements Serializable {
  private int comic_id;
  private String url;
  private String name;
  private String author;
  private String sort;
  private String introduce;
  private String cover;
  private String cover_address;
  private int chapter;

  public String getCover_address() {
    return cover_address;
  }

  public void setCover_address(String cover_address) {
    this.cover_address = cover_address;
  }

  public int getChapter() {
    return chapter;
  }

  public void setChapter(int chapter) {
    this.chapter = chapter;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public String getIntroduce() {
    return introduce;
  }

  public void setIntroduce(String introduce) {
    this.introduce = introduce;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getComic_id() {
    return comic_id;
  }

  public void setComic_id(int comic_id) {
    this.comic_id = comic_id;
  }
}
