package com.supergolf500.golfchat.DB;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.supergolf500.golfchat.Entity.MessageEntity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by supergolf500 on 7/11/2559.
 */

public class Message_DA {

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public Message_DA(Context context) {
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





    public boolean CheckExistMessage(int RunNo)
    {
        ArrayList<MessageEntity> result = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Message WHERE RunNo=" +  RunNo + ";",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            return true;
        }
        cursor.close();
        return false;
    }


    public int GetCountMessageUnread(String FriendID)
    {
        Cursor mCount= database.rawQuery("select count(*) from Message where MessageFrom='" + FriendID + "' AND ReadTime > Datetime(1000);", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();

        return count;
    }


    public ArrayList<MessageEntity> GetAllMessage(String FriendID)
    {
        ArrayList<MessageEntity> result = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Message WHERE MessageFrom='" + FriendID + "' OR MessageTo='" + FriendID + "' ORDER BY RunNo;",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            MessageEntity item=new MessageEntity();
            item.RunNo = cursor.getInt(cursor.getColumnIndex("RunNo"));
            item.MessageFrom = cursor.getString(cursor.getColumnIndex("MessageFrom"));
            item.MessageTo = cursor.getString(cursor.getColumnIndex("MessageTo"));
            if(cursor.getLong(cursor.getColumnIndex("SendTime"))>10000)
            {
                item.SendTime = new Date(cursor.getLong(cursor.getColumnIndex("SendTime")));
            }
            else
            {
                item.SendTime = null;
            }
            if(cursor.getLong(cursor.getColumnIndex("ReadTime"))>10000)
            {
                item.ReadTime = new Date(cursor.getLong(cursor.getColumnIndex("ReadTime")));
            }
            else
            {
                item.ReadTime = null;
            }
            item.MessageType = cursor.getString(cursor.getColumnIndex("MessageType"));
            item.value = cursor.getString(cursor.getColumnIndex("value"));
            item.Tag1 = cursor.getString(cursor.getColumnIndex("Tag1"));
            item.Tag2 = cursor.getString(cursor.getColumnIndex("Tag2"));
            result.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }


    public ArrayList<MessageEntity> GetNewMessage(String FriendID,int RunNo)
    {
        ArrayList<MessageEntity> result = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Message WHERE (MessageFrom='" + FriendID + "' OR MessageTo='" + FriendID + "') AND RunNo>" + RunNo + " ORDER BY RunNo;",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            MessageEntity item=new MessageEntity();
            item.RunNo = cursor.getInt(cursor.getColumnIndex("RunNo"));
            item.MessageFrom = cursor.getString(cursor.getColumnIndex("MessageFrom"));
            item.MessageTo = cursor.getString(cursor.getColumnIndex("MessageTo"));
            if(cursor.getLong(cursor.getColumnIndex("SendTime"))>10000)
            {
                item.SendTime = new Date(cursor.getLong(cursor.getColumnIndex("SendTime")));
            }
            else
            {
                item.SendTime = null;
            }
            if(cursor.getLong(cursor.getColumnIndex("ReadTime"))>10000)
            {
                item.ReadTime = new Date(cursor.getLong(cursor.getColumnIndex("ReadTime")));
            }
            else
            {
                item.ReadTime = null;
            }
            item.MessageType = cursor.getString(cursor.getColumnIndex("MessageType"));
            item.value = cursor.getString(cursor.getColumnIndex("value"));
            item.Tag1 = cursor.getString(cursor.getColumnIndex("Tag1"));
            item.Tag2 = cursor.getString(cursor.getColumnIndex("Tag2"));
            result.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }


    public ArrayList<MessageEntity> GetMyMessageNotRead(String UserID,String FriendID)
    {
        ArrayList<MessageEntity> result = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Message WHERE MessageFrom='" + UserID + "' AND MessageTo='" + FriendID + "' AND ReadTime > Datetime(1000) ORDER BY RunNo;",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            MessageEntity item=new MessageEntity();
            item.RunNo = cursor.getInt(cursor.getColumnIndex("RunNo"));
            item.MessageFrom = cursor.getString(cursor.getColumnIndex("MessageFrom"));
            item.MessageTo = cursor.getString(cursor.getColumnIndex("MessageTo"));
            if(cursor.getLong(cursor.getColumnIndex("SendTime"))>10000)
            {
                item.SendTime = new Date(cursor.getLong(cursor.getColumnIndex("SendTime")));
            }
            else
            {
                item.SendTime = null;
            }
            if(cursor.getLong(cursor.getColumnIndex("ReadTime"))>10000)
            {
                item.ReadTime = new Date(cursor.getLong(cursor.getColumnIndex("ReadTime")));
            }
            else
            {
                item.ReadTime = null;
            }
            item.MessageType = cursor.getString(cursor.getColumnIndex("MessageType"));
            item.value = cursor.getString(cursor.getColumnIndex("value"));
            item.Tag1 = cursor.getString(cursor.getColumnIndex("Tag1"));
            item.Tag2 = cursor.getString(cursor.getColumnIndex("Tag2"));

            result.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return result;

    }





    public long InsertMessage(String RunNo,String MessageFrom,String MessageTo,Date SendTime,Date ReadTime,String MessageType, String value,String Tag1,String Tag2)
    {
//        Date date = new Date();
//        date = new Date(Long.MIN_VALUE);

        ContentValues values = new ContentValues();
        values.put("RunNo", RunNo);
        values.put("MessageFrom", MessageFrom);
        values.put("MessageTo", MessageTo);
        values.put("SendTime", SendTime.getTime());
        values.put("ReadTime", ReadTime.getTime());
        values.put("MessageType", MessageType);
        values.put("value", value);
        values.put("Tag1", Tag1);
        values.put("Tag2", Tag2);

        // insert the row
        long id = database.insert("Message", null, values);

        return id;
    }


    public long InsertMessage(MessageEntity result)
    {
//        Date date = new Date();
//        date = new Date(Long.MIN_VALUE);
        ContentValues values = new ContentValues();
        values.put("RunNo", result.RunNo);
        values.put("MessageFrom", result.MessageFrom);
        values.put("MessageTo", result.MessageTo);
        if(result.SendTime!=null)
        {
            values.put("SendTime", result.SendTime.getTime());
        }
        if(result.ReadTime!=null)
        {
            values.put("ReadTime", result.ReadTime.getTime());
        }
        values.put("MessageType", result.MessageType);
        values.put("value", result.value);
        values.put("Tag1", result.Tag1);
        values.put("Tag2", result.Tag2);
        // insert the row
        long id = database.insert("Message", null, values);


        return id;
    }


    public long UpdateReadTime(ArrayList<Integer> listOfRunNo)
    {

        String conditionString = "RunNo IN (";
        for(int i=0;i<listOfRunNo.size();i++)
        {
            conditionString +=  listOfRunNo.get(i).toString() + ",";
        }
        conditionString += "0)";

        Date now= new Date();
        ContentValues values = new ContentValues();
        values.put("ReadTime",now.getTime());

        long id = database.update("Message",values,conditionString,null); //"RunNo IN (1,2,3)"


        return id;
    }

















}
