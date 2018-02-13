package com.supergolf500.golfchat.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.supergolf500.golfchat.Entity.FriendChatEntity;
import com.supergolf500.golfchat.Entity.MessageEntity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by supergolf500 on 11/11/2559.
 */

public class FriendMapping_DA {

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public FriendMapping_DA(Context context) {
        dbHelper= new DbHelper(context);
    }

    public void open()
    {
        database=dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }
    ///////////////////////////////////////////////////////////////////////////////
    public ArrayList<FriendChatEntity> GetAllFriendChat(String UserID)
    {
        ArrayList<FriendChatEntity> result = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM FriendMapping WHERE FriendID <> '" + UserID + "' ORDER BY LastTimeMessage;",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            FriendChatEntity item=new FriendChatEntity();
            item.FriendKey = cursor.getInt(cursor.getColumnIndex("FriendKey"));
            item.UserID = cursor.getString(cursor.getColumnIndex("UserID"));
            item.FriendID = cursor.getString(cursor.getColumnIndex("FriendID"));
            item.EditName = cursor.getString(cursor.getColumnIndex("EditName"));
            item.IsBlock = (cursor.getInt(cursor.getColumnIndex("IsBlock")) == 1); //--Boolean
            item.NotificationBlock = (cursor.getInt(cursor.getColumnIndex("NotificationBlock")) == 1); //--Boolean
            item.TextSpeech = (cursor.getInt(cursor.getColumnIndex("TextSpeech")) == 1); //--Boolean

            item.LastMessage = cursor.getString(cursor.getColumnIndex("LastMessage"));
            if(cursor.getLong(cursor.getColumnIndex("LastTimeMessage"))>10000)
            {
                item.LastTimeMessage = new Date(cursor.getLong(cursor.getColumnIndex("LastTimeMessage")));
            }
            else
            {
                item.LastTimeMessage = null;
            }

            item.CountMessage = cursor.getInt(cursor.getColumnIndex("CountMessage"));
            result.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }


    public FriendChatEntity GetFriendMapping(String FriendID)
    {
        ArrayList<FriendChatEntity> result = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM FriendMapping WHERE FriendID = '" + FriendID + "'",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            FriendChatEntity item=new FriendChatEntity();
            item.FriendKey = cursor.getInt(cursor.getColumnIndex("FriendKey"));
            item.UserID = cursor.getString(cursor.getColumnIndex("UserID"));
            item.FriendID = cursor.getString(cursor.getColumnIndex("FriendID"));
            item.EditName = cursor.getString(cursor.getColumnIndex("EditName"));
            item.IsBlock = (cursor.getInt(cursor.getColumnIndex("IsBlock")) == 1); //--Boolean
            item.NotificationBlock = (cursor.getInt(cursor.getColumnIndex("NotificationBlock")) == 1); //--Boolean
            item.TextSpeech = (cursor.getInt(cursor.getColumnIndex("TextSpeech")) == 1); //--Boolean

            item.LastMessage = cursor.getString(cursor.getColumnIndex("LastMessage"));
            if(cursor.getLong(cursor.getColumnIndex("LastTimeMessage"))>10000)
            {
                item.LastTimeMessage = new Date(cursor.getLong(cursor.getColumnIndex("LastTimeMessage")));
            }
            else
            {
                item.LastTimeMessage = null;
            }

            item.CountMessage = cursor.getInt(cursor.getColumnIndex("CountMessage"));
            result.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        if(result.size()==0)
        {
            return null;
        }
        else
        {
            return result.get(0);
        }

    }



    public void InsertFriendMapping(FriendChatEntity result)
    {


        ContentValues values = new ContentValues();
        values.put("UserID", result.UserID);
        values.put("FriendID", result.FriendID);
        values.put("EditName", result.EditName);
        values.put("IsBlock", result.IsBlock);
        values.put("NotificationBlock", result.NotificationBlock);
        values.put("TextSpeech", result.TextSpeech);
        values.put("LastMessage", result.LastMessage);
        values.put("LastTimeMessage", result.LastMessage);
        values.put("CountMessage", result.CountMessage);


        // insert the row
        database.insert("FriendMapping", null, values);

    }


    public void RemoveAll()
    {
       int row = database.delete("FriendMapping",null,null);
    }




}
