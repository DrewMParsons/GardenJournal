package com.lumpyslounge.gardenjournal.Model.Model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Plant
{
    private String id;
    private String name;
    private String variety;
    private String note;
    private Timestamp datePlanted;
    private String imageUrl;
    private String userId;
    private String userName;
    private Timestamp timeCreated;

    public Plant()
    {
    }

    public Plant(String name, String variety, String note, Timestamp datePlanted, String imageUrl, String userId, String userName, Timestamp timeCreated)
    {
        this.name = name;
        this.variety = variety;
        this.note = note;
        this.datePlanted = datePlanted;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVariety()
    {
        return variety;
    }

    public void setVariety(String variety)
    {
        this.variety = variety;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public Timestamp getDatePlanted()
    {
        return datePlanted;
    }

    public void setDatePlanted(Timestamp datePlanted)
    {
        this.datePlanted = datePlanted;
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

