package com.lumpyslounge.gardenjournal.Model.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Journal
{
    private String id;
    private String title;
    private String temperature;
    private String note;
    private Timestamp date;
    private String imageUrl;
    private String userId;
    private String userName;
    private Timestamp timeCreated;


    public Journal()
    {
    }

    public Journal(String title, String temperature, String note, Timestamp date, String imageUrl, String userId, String userName, Timestamp timeCreated)
    {
        this.title = title;
        this.temperature = temperature;
        this.note = note;
        this.date = date;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
        this.timeCreated = timeCreated;
    }






    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTemperature()
    {
        return temperature;
    }

    public void setTemperature(String temperature)
    {
        this.temperature = temperature;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public Timestamp getDate()
    {
        return date;
    }

    public void setDate(Timestamp date)
    {
        this.date = date;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Timestamp getTimeCreated()
    {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated)
    {
        this.timeCreated = timeCreated;
    }

}
