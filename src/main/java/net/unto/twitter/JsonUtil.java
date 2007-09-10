package net.unto.twitter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonUtil {

  public final static Status[] newStatusArray(String jsonString)
      throws TwitterException {
    return newStatusArray(JSONArray.fromString(jsonString));
  }

  public final static Status[] newStatusArray(JSONArray jsonArray)
      throws TwitterException {
    List<Status> statusList = new ArrayList<Status>();
    for (int i = 0; i < jsonArray.length(); i++) {
      statusList.add(newStatus(jsonArray.getJSONObject(i)));
    }
    return statusList.toArray(new Status[statusList.size()]);
  }

  public final static Status newStatus(String jsonString)
      throws TwitterException {
    return newStatus(JSONObject.fromString(jsonString));
  }

  public final static Status newStatus(JSONObject jsonObject)
      throws TwitterException {
    if (jsonObject == null) {
      return null;
    }
    Status status = new Status();
    if (jsonObject.has("created_at")) {
      status.setCreatedAt(getDateFromJsonObject(jsonObject, "created_at"));
    }
    if (jsonObject.has("id")) {
      status.setId(jsonObject.getString("id"));
    }
    if (jsonObject.has("text")) {
      status.setText(jsonObject.getString("text"));
    }
    if (jsonObject.has("user")) {
      status.setUser(newUser(jsonObject.getJSONObject("user")));
    }
    return status;
  }

  public final static User[] newUserArray(String jsonString)
      throws TwitterException {
    return newUserArray(JSONArray.fromString(jsonString));
  }

  public final static User[] newUserArray(JSONArray jsonArray)
      throws TwitterException {
    List<User> userList = new ArrayList<User>();
    for (int i = 0; i < jsonArray.length(); i++) {
      userList.add(newUser(jsonArray.getJSONObject(i)));
    }
    return userList.toArray(new User[userList.size()]);
  }

  public final static User newUser(String jsonString) throws TwitterException {
    return newUser(JSONObject.fromString(jsonString));
  }

  public final static User newUser(JSONObject jsonObject)
      throws TwitterException {
    if (jsonObject == null) {
      return null;
    }
    User user = new User();
    if (jsonObject.has("description")) {
      user.setDescription(jsonObject.getString("description"));
    }
    if (jsonObject.has("id")) {
      user.setId(jsonObject.getString("id"));
    }
    if (jsonObject.has("location")) {
      user.setLocation(jsonObject.getString("location"));
    }
    if (jsonObject.has("name")) {
      user.setName(jsonObject.getString("name"));
    }
    if (jsonObject.has("profile_image_url")) {
      user.setProfileImageUrl(jsonObject.getString("profile_image_url"));
    }
    if (jsonObject.has("screen_name")) {
      user.setScreenName(jsonObject.getString("screen_name"));
    }
    if (jsonObject.has("status")) {
      user.setStatus(newStatus(jsonObject.getJSONObject("status")));
    }
    return user;
  }

  private static Date getDateFromJsonObject(JSONObject jsonObject, String key)
      throws TwitterException {
    if (jsonObject.has(key)) {
      String dateString = jsonObject.getString(key);
      try {
        return Status.getTwitterDateFormat().parse(dateString);
      } catch (ParseException e) {
        throw new TwitterException(e);
      }
    }
    return null;
  }
}
