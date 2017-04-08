package com.example.qianxun.qianxuncomic.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.qianxun.qianxuncomic.model.Chapter;
import com.example.qianxun.qianxuncomic.model.Comic;
import com.example.qianxun.qianxuncomic.model.Download;
import com.example.qianxun.qianxuncomic.model.History;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sllEc_000 on 2017/2/26 0026.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public DatabaseHelper(Context context, String name, int version){
        this(context,name,null,version);
    }

    public DatabaseHelper(Context context, String name){
        this(context,name,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*创建漫画表*/
        sqLiteDatabase.execSQL("CREATE TABLE comic (\n" +
                "  comic_id  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  url  varchar(20) DEFAULT NULL,\n" +
                "  name varchar(10) DEFAULT NULL,\n" +
                "  author varchar(10) DEFAULT NULL,\n" +
                "  sort varchar(10) DEFAULT NULL,\n" +
                "  introduce varchar(100) DEFAULT NULL,\n" +
                "  cover varchar(20) DEFAULT NULL,\n" +
                "  cover_address varchar(20) DEFAULT NULL,\n" +
                "  chapter int DEFAULT NULL\n" +
                ") ");
        /*创建漫画目录表*/
        sqLiteDatabase.execSQL("CREATE TABLE chapter (\n" +
                "  chapter_id  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  chapter int DEFAULT NULL,\n" +
                "  chapter_name varchar(20) DEFAULT NULL,\n" +
                "  chapter_url varchar(20) DEFAULT NULL,\n" +
                "  page_url varchar(20) DEFAULT NULL,\n" +
                "  page_address varchar(20)DEFAULT NULL\n" +
                ") ");
        /*创建观看历史表*/
        sqLiteDatabase.execSQL("CREATE TABLE history (\n" +
                "  history_id  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  comic_name  varchar(20) DEFAULT NULL,\n" +
                "  chapter_name varchar(20) DEFAULT NULL\n" +
                ") ");
        /*创建本地下载表*/
        sqLiteDatabase.execSQL("CREATE TABLE download (\n" +
                "  download_id  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  comic_name  varchar(20) DEFAULT NULL,\n" +
                "  chapter_name varchar(20) DEFAULT NULL\n" +
                ")  ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS download;\n" +
                "DROP TABLE IF EXISTS comic;\n" +
                "DROP TABLE IF EXISTS chapter;\n" +
                "DROP TABLE IF EXISTS history;");
        onCreate(sqLiteDatabase);
    }

    //添加漫画
    public void addComic(SQLiteDatabase db,Comic comic){
        String sql = "insert into comic(url,name,author,sort,introduce,cover,cover_address,chapter)values" +
                "('"+comic.getUrl()+"','"+comic.getName()+"','"+comic.getAuthor()+"','"+comic.getSort()+"','"+comic.getIntroduce()+"','"+comic.getCover()+"','"+comic.getCover_address()+"','"+comic.getChapter()+"')";
        db.execSQL(sql);
    }

    //删除漫画
    public void deleteComic(String comic_name){

        String sql = "delete from comic where name = '"+comic_name+"'";
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL(sql);
    }
    //添加漫画目录信息
    public void addChapter(SQLiteDatabase db,Chapter chapter){
        String sql = "insert into chapter(chapter,chapter_name,chapter_url,page_url,page_address)values('"+chapter.getChapter()+"','"+chapter.getChapter_name()+"','"+chapter.getChapter_url()+"','"+chapter.getPage_url()+"','"+chapter.getPage_address()+"')";
        db.execSQL(sql);
    }
    //删除漫画目录信息
    public void deleteChapter(SQLiteDatabase db,Chapter chapter){
        String sql = "delete from chapter where chapter_id = '"+chapter.getChapter_id()+"'";
        db.execSQL(sql);
    }
    //修改漫画目录信息
    public  void updateChapter(String page_url, String page_address,String chapter_name){
        String sql = "update chapter set page_url='"+page_url+"',page_address='"+page_address+"'where chapter_name='"+chapter_name+"'";
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL(sql);
    }

    //添加观看历史
    public void addHistory(SQLiteDatabase db,History history){
        String sql = "insert into history(comic_name,chapter_name)values('"+history.getComic_name()+"','"+history.getChapter_name()+"')";
        db.execSQL(sql);
    }

    //删除观看历史
    public void deleteHistory(SQLiteDatabase db,History history){
        String sql = "delete from history where comic_name = '"+history.getComic_name()+"'";
        db.execSQL(sql);
    }
    //查找所有观看历史信息
    public List<Comic> getAllHistory(){
        List<Comic> comicList=new ArrayList<Comic>();
        String selectQuery="select * from comic where name =(select distinct comic_name from history)";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        while(cursor.moveToNext()){
                Comic comic=new Comic();
                comic.setComic_id(Integer.parseInt(cursor.getString(0)));
                comic.setUrl(cursor.getString(1));
                comic.setName(cursor.getString(2));
                comic.setAuthor(cursor.getString(3));
                comic.setSort(cursor.getString(4));
                comic.setIntroduce(cursor.getString(5));
                comic.setCover(cursor.getString(6));
                comic.setCover_address(cursor.getString(7));
                comic.setChapter(Integer.parseInt(cursor.getString(8)));
                comicList.add(comic);
        }
        return comicList;
    }

    //添加下载
    public void addDownload(SQLiteDatabase db,Download download){
        String sql = "insert into download(comic_name,chapter_name)values('"+download.getComic_name()+"','"+download.getChapter_name()+"')";
        db.execSQL(sql);
    }
    //删除下载
    public void deleteDownload(SQLiteDatabase db,Download download){
        String sql = "delete from download where comic_name = '"+download.getComic_name()+"'";
        db.execSQL(sql);
    }
    //查找所有下载的漫画
    public List<Comic> getAllDownloadComic(){
        List<Comic> comicList=new ArrayList<Comic>();
        String selectQuery="select * from comic where name =(select distinct comic_name from download)";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        while(cursor.moveToNext()){
            Comic comic=new Comic();
            comic.setComic_id(Integer.parseInt(cursor.getString(0)));
            comic.setUrl(cursor.getString(1));
            comic.setName(cursor.getString(2));
            comic.setAuthor(cursor.getString(3));
            comic.setSort(cursor.getString(4));
            comic.setIntroduce(cursor.getString(5));
            comic.setCover(cursor.getString(6));
            comic.setCover_address(cursor.getString(7));
            comic.setChapter(Integer.parseInt(cursor.getString(8)));
            comicList.add(comic);
        }
        return comicList;
    }
    public Comic getComic(String comic_name){
        Comic comic=new Comic();
        String selectQuery="select * from comic where name ='"+comic_name+"'";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery, null);
        if(cursor.moveToNext()){
            comic.setComic_id(Integer.parseInt(cursor.getString(0)));
            comic.setUrl(cursor.getString(1));
            comic.setName(cursor.getString(2));
            comic.setAuthor(cursor.getString(3));
            comic.setSort(cursor.getString(4));
            comic.setIntroduce(cursor.getString(5));
            comic.setCover(cursor.getString(6));
            comic.setCover_address(cursor.getString(7));
            comic.setChapter(Integer.parseInt(cursor.getString(8)));
        }
        return comic;
    }
    //查找某漫画下载的章节
    public List<Chapter> getDownloadComicChapter(String comic_name){
        List<Chapter> chapterList=new ArrayList<Chapter>();
        String selectQuery="select * from chapter where chapter_name=(select chapter_name from download where comic_name ='"+comic_name+"')";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        while (cursor.moveToNext()) {
            Chapter chapter=new Chapter();
                chapter.setChapter_id(Integer.parseInt(cursor.getString(0)));
                chapter.setChapter(Integer.parseInt(cursor.getString(1)));
                chapter.setChapter_name(cursor.getString(2));
                chapter.setChapter_url(cursor.getString(3));
                chapter.setPage_url(cursor.getString(4));
                chapter.setPage_address(cursor.getString(5));
                chapterList.add(chapter);
        }
        return chapterList;
    }

}
