package com.mvvm.retrofit;

import com.mvvm.model.Comment;
import com.mvvm.model.Conversation;
import com.mvvm.model.Message;
import com.mvvm.model.Post;
import com.mvvm.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;



public class Parser {
    public static List<Post> parsePostsList(JSONArray jsonArray) throws JSONException, ParseException {
        List<Post> postsList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            User u = parseUser(jsonObject.getJSONObject("user"));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
            Date date = sdf.parse(jsonObject.getString("date"));

            Post p = new Post(jsonObject.getInt("id"),
                    jsonObject.getString("description"),
                    jsonObject.getString("trackUrl"),
                    jsonObject.getString("iconUrl"),
                    jsonObject.getString("trackname"),
                    date,
                    u,
                    jsonObject.getInt("likesCount"),
                    jsonObject.getInt("commentsCount"),
                    Parser.parseUserList(jsonObject.getJSONArray("likes"))
            );

            postsList.add(p);
        }
        return postsList;
    }

    public static User parseUser(JSONObject jsonObject) throws JSONException
    {
        User u = new User(jsonObject.getInt("user_id"),
                jsonObject.getString("username"),
                jsonObject.getString("user_firstName"),
                jsonObject.getString("user_lastName"),
                jsonObject.getString("user_email"),
                jsonObject.getString("user_password"),
                jsonObject.getString("role"),
                jsonObject.getString("user_bio"),
                jsonObject.getInt("followersCount"),
                jsonObject.getInt("followingCount"),
                jsonObject.getString("profileImgUrl"),
                jsonObject.getBoolean("isPrivate"),
                jsonObject.getBoolean("isOnline"),
                jsonObject.getInt("postsCount"));
        return u;
    }

    public static User parseFullUser(JSONObject jsonObject) throws JSONException
    {
        User u = new User(jsonObject.getInt("user_id"),
                jsonObject.getString("username"),
                jsonObject.getString("user_firstName"),
                jsonObject.getString("user_lastName"),
                jsonObject.getString("user_email"),
                jsonObject.getString("user_password"),
                jsonObject.getString("role"),
                jsonObject.getString("user_bio"),
                jsonObject.getInt("followersCount"),
                jsonObject.getInt("followingCount"),
                jsonObject.getString("profileImgUrl"),
                jsonObject.getBoolean("isPrivate"),
                jsonObject.getBoolean("isOnline"),
                jsonObject.getInt("postsCount"),
                parseUserList(jsonObject.getJSONArray("followers")),
                parseUserList(jsonObject.getJSONArray("following")));
        return u;
    }

    public static List<User> parseUserList(JSONArray jsonArray) throws JSONException {
        List<User> foundUsers = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            foundUsers.add(parseUser(jsonObject));
        }
        return foundUsers;
    }

    public static List<Comment> parseCommentList(JSONArray jsonArray) throws JSONException, ParseException{
        List<Comment> foundComments = new ArrayList<>();


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = sdf.parse(jsonObject.getString("date"));

            Comment comment = new Comment(jsonObject.getInt("id"),
                                        jsonObject.getString("content"),
                                        date,
                                        parseUser(jsonObject.getJSONObject("user")),
                                        parseUserList(jsonObject.getJSONArray("likes")),
                                        jsonObject.getInt("likesCount"));

            foundComments.add(comment);
        }

        return foundComments;
    }

    public static Message parseMessage(JSONObject jsonObject) throws JSONException, ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        Date date = sdf.parse(jsonObject.getString("date"));

        Message m = new Message(jsonObject.getInt("id"),
                jsonObject.getString("content"),
                jsonObject.getString("status"),
                date,
                parseUser(jsonObject.getJSONObject("sender")),
                parseUser(jsonObject.getJSONObject("reciever")));
        return m;
    }

    public static List<Message> parseMessageList(JSONArray jsonArray) throws JSONException, ParseException
    {
        List<Message> foundMessages = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            foundMessages.add(parseMessage(jsonObject));
        }

        return foundMessages;
    }

    public static Message parsePartialMessage(JSONObject jsonObject) throws JSONException, ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        Date date = sdf.parse(jsonObject.getString("date"));

        Message m = new Message(jsonObject.getInt("id"),
                jsonObject.getString("content"),
                jsonObject.getString("status"),
                date);
        return m;
    }

    public static List<Message> parsePartialMessageList(JSONArray jsonArray) throws JSONException, ParseException
    {
        List<Message> foundMessages = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            foundMessages.add(parsePartialMessage(jsonObject));
        }

        return foundMessages;
    }



    public static List<Conversation> parseConversationList(JSONArray jsonArray) throws JSONException, ParseException{
        List<Conversation> foundConversations = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            final JSONObject jsonObject = jsonArray.getJSONObject(i);



            Conversation conversation = new Conversation(jsonObject.getInt("id"),
                    parseUser(jsonObject.getJSONObject("first_user")),
                    parseUser(jsonObject.getJSONObject("second_user")),
                    parsePartialMessageList(jsonObject.getJSONArray("messages")));

            foundConversations.add(conversation);
        }

        return foundConversations;
    }


}
