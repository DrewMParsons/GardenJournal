package com.lumpyslounge.gardenjournal.Model.Model;

import com.google.firebase.Timestamp;

public class Event
{
    private String id;
    private String title;
    private String type;
    private String note;
    private Timestamp date;
    private String imageUrl;
    private String userId;
    private String userName;
    private Timestamp timeCreated;

    public Event()
    {
    }

    public Event(String title, String type, String note, Timestamp date, String imageUrl, String userId, String userName, Timestamp timeCreated)
    {
        this.title = title;
        this.type = type;
        this.note = note;
        this.date = date;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
        this.timeCreated = timeCreated;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}

