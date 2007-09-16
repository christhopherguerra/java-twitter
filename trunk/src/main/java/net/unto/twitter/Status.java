package net.unto.twitter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Status {

  public Status() {
  }
  
  private DateTime createdAt;

  public boolean hasCreatedAt() {
    return createdAt != null;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setCreatedAt(String createdAtString) {
    setCreatedAt(parseTwitterDateTimeString(createdAtString));
  }

  public String getRelativeCreatedAt() {
    // TODO(dewitt): Create relative_created_at string
    return null;
  }

  private String id;

  public boolean hasId() {
    return id != null;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String source;
  
  public boolean hasSource() {
    return source != null;
  }
  
  public String getSource() {
    return source;
  }
  
  public void setSource(String source) {
    this.source = source;
  }
  
  private String text;

  public boolean hasText() {
    return text != null;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  private User user;

  public boolean hasUser() {
    return user != null;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
  
  private final static DateTimeFormatter TWITTER_DATE_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy");
  
  private DateTime parseTwitterDateTimeString(String twitterDateString) {
    try {
      return TWITTER_DATE_FORMATTER.parseDateTime(twitterDateString);
    } catch (IllegalArgumentException e) {
      System.err.println(String.format("Could not parse date string '%s'", twitterDateString));
      return null;
    }
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public boolean equals(Object obj) { 
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
