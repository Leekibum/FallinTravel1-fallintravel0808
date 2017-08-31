package com.probum.fallintravel;

/**
 * Created by alfo6-25 on 2017-08-30.
 */

public class ReplyItem {
    String content;
    String id;
    String profileimage;
    String reply;

    public ReplyItem(String content, String id, String profileimage, String reply) {
        this.content = content;
        this.id = id;
        this.profileimage = profileimage;
        this.reply = reply;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}


