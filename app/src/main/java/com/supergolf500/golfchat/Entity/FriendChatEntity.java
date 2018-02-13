package com.supergolf500.golfchat.Entity;

import java.util.Date;

/**
 * Created by supergolf500 on 11/11/2559.
 */

public class FriendChatEntity {

    public int FriendKey;
    public String UserID;
    public String FriendID;
    public String EditName;
    public Boolean  IsBlock;
    public Boolean  NotificationBlock;
    public Boolean  TextSpeech;

    public String LastMessage;
    public Date LastTimeMessage;
    public int CountMessage;
}
