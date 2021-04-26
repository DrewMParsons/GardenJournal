package com.lumpyslounge.gardenjournal.Util;


import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateFormatter
{
    private DateFormatter(){

    }

    public static String getStringFromDate(int year, int month, int day){
        return String.format("%d/%d/%d",month+1,day,year);
    }

    public static Timestamp getTimeStampFromString(String dateString) throws ParseException
    {
        Date date = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
        return new Timestamp(date);
    }

    public static String getStringFromTimestamp(Timestamp timestamp){
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    return df.format(timestamp.toDate());

    }
    public static String getDateTimeStringFromTimestamp(Timestamp timestamp){
        DateFormat df = new SimpleDateFormat("MM/dd/yy  HH:mm");
        return df.format(timestamp.toDate());

    }

    public static Long getLongFromTimestamp(Timestamp timestamp){
        return Long.parseLong(timestamp.toString());

    }



}
