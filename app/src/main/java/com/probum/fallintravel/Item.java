package com.probum.fallintravel;


public class Item {

    String title;
    String firstimage;
    String time;
    String contentid;
    String contenttypeid;

    public Item(String title, String firstimage,String time,String contentid,String contenttypeid) {
        this.title = title;
        this.firstimage = firstimage;
        this.time=time;
        this.contentid=contentid;
        this.contenttypeid=contenttypeid;
    }

    public Item(String title, String firstimage,String contentid,String contenttypeid) {
        this.title = title;
        this.firstimage = firstimage;
        this.contentid=contentid;
        this.contenttypeid=contenttypeid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstimage() {
        return firstimage;
    }

    public void setFirstimage(String firstimage) {
        this.firstimage = firstimage;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getContenttypeid() {
        return contenttypeid;
    }

    public void setContenttypeid(String contenttypeid) {
        this.contenttypeid = contenttypeid;
    }
}
