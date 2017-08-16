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

    public Item(String time, String contentid) {
        this.time = time;
        this.contentid = contentid;
    }
}
