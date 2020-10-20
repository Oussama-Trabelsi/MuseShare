package com.mvvm.util;

import com.mvvm.model.Conversation;
import com.mvvm.model.Message;
import com.mvvm.model.User;
import com.mvvm.repository.ChatRepository;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.view.fragment.FollowsFragmentCallback;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Common {//192.168.1.48
    public static String IP_ADDRESS = "192.168.1.5";
    public static String BASE_URL = "http://" + IP_ADDRESS + ":3000/";
    public static String  BASE_PROFILE_IMAGE_URL = "http://" + IP_ADDRESS + ":3000/profile_images/";
    public static String  BASE_TRACK_URL = "http://" + IP_ADDRESS + ":3000/tracks/";
    public static FollowsFragmentCallback followsFragmentCallback;

    public static String getElapsedTime(Date d) throws ParseException {
        String elapsedTimeString = "";
        SimpleDateFormat isoFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        Date date = isoFormat.parse(new Date().toString());
        Interval interval =  new Interval(d.getTime(),date.getTime());
        Period period = interval.toPeriod();

        int months = period.getMonths();
        int days = period.getDays();
        int hours = period.getHours();
        int minutes = period.getMinutes();
        int seconds = period.getSeconds();

        if(months > 0)
        {
            if(months == 1) return months + " month ago";
            else if(months < 10) return months + " months ago";
            else return d.toString().substring(0, 16);

        }

        else {
            if(days > 0)
            {
                if(days == 1) return days + " day ago";
                else return days + " days ago";
            }

            if(hours > 0)
            {
                if(hours == 1) elapsedTimeString += hours + " hour";
                else  elapsedTimeString += hours + " hours";
            }

            if(minutes > 0)
            {
                if(hours > 0) elapsedTimeString += " , ";
                if(minutes == 1) elapsedTimeString += minutes + " minute";
                else elapsedTimeString += minutes +" minutes";
            }

            else return "Just now";
        }


        return elapsedTimeString + " ago";
    }

    public static String getElapsedTimeForComment(Date d) throws ParseException {
        String elapsedTimeString = "";
        SimpleDateFormat isoFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        Date date = isoFormat.parse(new Date().toString());
        Interval interval =  new Interval(d.getTime(), date.getTime());
        Period period = interval.toPeriod();

        int months = period.getMonths();
        int days = period.getDays();
        int hours = period.getHours();
        int minutes = period.getMinutes();
        int seconds = period.getSeconds();

        if(months > 0)
        {
            if(months == 1) return months + " m ago";
            else if(months < 10) return months + " m ago";
            else return d.toString().substring(0, 16);

        }

        else {
            if(days > 0)
            {
                if(days == 1) return days + " d ago";
                else return days + " d ago";
            }

            if(hours > 0)
            {
                if(hours == 1) elapsedTimeString += hours + " h";
                else  elapsedTimeString += hours + " h";
            }

            if(minutes > 0)
            {
                if(hours > 0) elapsedTimeString += " , ";
                if(minutes == 1) elapsedTimeString += minutes + " min";
                else elapsedTimeString += minutes +" min";
            }

            else return "Just now";
        }


        return elapsedTimeString + " ago";
    }

    public static String getDateAMPM(Date d)
    {
        //Displaying current time in 12 hour format with AM/PM
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        return dateFormat.format(d);
    }

    public static boolean isInList(User u, List<User> userList)
    {
        for(User user : userList)
        {
            if(user.getId() == u.getId()) return true;
        }
        return false;
    }




}
