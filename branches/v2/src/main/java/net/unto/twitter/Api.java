package net.unto.twitter;

import net.unto.twitter.TwitterProtos.Device;
import net.unto.twitter.UtilProtos.Url;
import net.unto.twitter.UtilProtos.Url.Scheme;
import net.unto.twitter.methods.CreateFavoriteRequest;
import net.unto.twitter.methods.CreateFriendshipRequest;
import net.unto.twitter.methods.DestroyDirectMessageRequest;
import net.unto.twitter.methods.DestroyFavoriteRequest;
import net.unto.twitter.methods.DestroyFriendshipRequest;
import net.unto.twitter.methods.DestroyStatusRequest;
import net.unto.twitter.methods.DirectMessagesRequest;
import net.unto.twitter.methods.EndSessionRequest;
import net.unto.twitter.methods.FavoritesRequest;
import net.unto.twitter.methods.FollowerIdsRequest;
import net.unto.twitter.methods.FollowersRequest;
import net.unto.twitter.methods.FriendIdsRequest;
import net.unto.twitter.methods.FriendsRequest;
import net.unto.twitter.methods.FriendsTimelineRequest;
import net.unto.twitter.methods.FriendshipExistsRequest;
import net.unto.twitter.methods.NewDirectMessageRequest;
import net.unto.twitter.methods.PublicTimelineRequest;
import net.unto.twitter.methods.RateLimitStatusRequest;
import net.unto.twitter.methods.RepliesRequest;
import net.unto.twitter.methods.Request;
import net.unto.twitter.methods.SearchRequest;
import net.unto.twitter.methods.SentDirectMessagesRequest;
import net.unto.twitter.methods.ShowStatusRequest;
import net.unto.twitter.methods.ShowUserRequest;
import net.unto.twitter.methods.TrendsRequest;
import net.unto.twitter.methods.UpdateDeliveryDeviceRequest;
import net.unto.twitter.methods.UpdateProfileBackgroundImageRequest;
import net.unto.twitter.methods.UpdateProfileColorsRequest;
import net.unto.twitter.methods.UpdateProfileImageRequest;
import net.unto.twitter.methods.UpdateProfileRequest;
import net.unto.twitter.methods.UpdateStatusRequest;
import net.unto.twitter.methods.UserTimelineRequest;
import net.unto.twitter.methods.VerifyCredentialsRequest;

/**
 * Instances of the Api class provide access to the Twitter web service.
 * <p>
 * The Api class acts a factory for individual Api requests. The Api class and
 * request methods attempt to provide sensible defaults when possible, and rely
 * on standard builder patterns to customize each as required.
 * </p>
 * <p>
 * Example usage:
 * <p>
 * 
 * <pre>
 * Api api = Api.DEFAULT_API;
 * List&lt;Status&gt; statuses = api.publicTimeline().build().get();
 * for (Status status : statuses) {
 *   System.out.println(status.getText());
 * }
 * </pre>
 * 
 * <p>
 * A more involved example:
 * </p>
 * 
 * <pre>
 * Api api = Api.builder().username(&quot;username&quot;).password(&quot;password&quot;).build();
 * Status status = api.updateStatus(&quot;Hello Twitter&quot;).inReplyToStatusId(12345)
 *     .build().post();
 * System.out.println(status.getText());
 * </pre>
 * 
 * @author DeWitt Clinton <dewitt@unto.net>
 */
public class Api {

  public static class Builder {

    private String host = DEFAULT_HOST;

    private HttpManager httpManager = DEFAULT_HTTP_MANAGER;
    private String password = null;
    private int port = DEFAULT_PORT;
    private Url.Scheme scheme = DEFAULT_SCHEME;
    private String username = null;

    public Api build() {
      if ((username != null) && (password == null)) {
        throw new IllegalStateException(
            "Password must be set if username is set.");
      }
      if ((password != null) && (username == null)) {
        throw new IllegalStateException(
            "Username must be set if password is set.");
      }
      if ((username != null) && (httpManager != null)) {
        throw new IllegalStateException(
            "Only one of httpManager and username can be set.");
      }
      return new Api(this);
    }

    public Builder host(String host) {
      this.host = host;
      return this;
    }

    public Builder httpManager(HttpManager httpManager) {
      this.httpManager = httpManager;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder scheme(Url.Scheme scheme) {
      this.scheme = scheme;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }
  }

  public static final Api DEFAULT_API = Api.builder().build();

  public static final String DEFAULT_HOST = "twitter.com";

  public static final HttpManager DEFAULT_HTTP_MANAGER = TwitterHttpManager
      .builder().build();

  public static final int DEFAULT_PORT = 80;

  public static final Url.Scheme DEFAULT_SCHEME = Url.Scheme.HTTP;

  public static Builder builder() {
    return new Builder();
  }

  private String host;

  private HttpManager httpManager = null;

  private int port;

  private Scheme scheme;

  private Api(Builder builder) {
    assert (builder.host != null);
    assert (builder.port > 0);
    assert (builder.scheme != null);
    if (builder.httpManager != null) {
      this.httpManager = builder.httpManager;
    } else if (builder.username != null && builder.password != null) {
      this.httpManager = TwitterHttpManager.builder()
          .username(builder.username).password(builder.password).build();
    } else {
      this.httpManager = TwitterHttpManager.builder().build();
    }
    host = builder.host;
    port = builder.port;
    scheme = builder.scheme;
  }

  /**
   * Destroys the status specified by the required ID parameter. The
   * authenticating user must be the author of the specified status.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>Status status = api.destroyStatus(12345).build().post();</code>
   * </p>
   * 
   * @param id The ID of the status to destroy.
   * @return A DestroyStatusRequest.Builder.
   */
  public DestroyStatusRequest.Builder destroyStatus(long id) {
    DestroyStatusRequest.Builder builder = DestroyStatusRequest.builder(id);
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the authenticating user's followers, each with current status
   * inline. They are ordered by the order in which they joined Twitter (this is
   * going to be changed).
   * <p>
   * Supports the <code>id</code> and <code>page</code> optional parameters.
   * </p>
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>List<User> followers = api.followers().build().get();</code>
   * </p>
   * 
   * @return A FollowersRequest.Builder supporting the <code>id</code> and
   *         <code>page</code> optional parameters.
   */
  public FollowersRequest.Builder followers() {
    FollowersRequest.Builder builder = FollowersRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the authenticating user's friends, each with current status inline.
   * They are ordered by the order in which they were added as friends. It's
   * also possible to request another user's recent friends list via the id
   * parameter below.
   * <p>
   * Supports the <code>id</code> and <code>page</code> optional parameters.
   * </p>
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>List<User> friends = api.friends().build().get();</code>
   * </p>
   * 
   * @return A FriendsRequest.Builder supporting the <code>id</code> and
   *         <code>page</code> optional parameters.
   */
  public FriendsRequest.Builder friends() {
    FriendsRequest.Builder builder = FriendsRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the 20 most recent statuses posted by the authenticating user and
   * that user's friends. This is the equivalent of /home on the Web.
   * <p>
   * Supports the <code>count</code>, <code>page</code>, <code>since</code>, and
   * <code>sinceId</code> optional parameters.
   * </p>
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>List<Status> friendsTimeline = api.friendsTimeline().build().get();</code>
   * </p>
   * 
   * @return A FriendsTimelineRequest.Builder supporting the <code>count</code>,
   *         <code>page</code>, <code>since</code>, and <code>sinceId</code>
   *         optional parameters.
   */
  public FriendsTimelineRequest.Builder friendsTimeline() {
    FriendsTimelineRequest.Builder builder = FriendsTimelineRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the 20 most recent statuses from non-protected users who have set a
   * custom user icon. Does not require authentication. Note that the public
   * timeline is cached for 60 seconds so requesting it more often than that is
   * a waste of resources.
   * 
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>List<Status> publicTimeline = api.publicTimeline().build().get();</code>
   * </p>
   * 
   * @return {@link PublicTimelineRequest.Builder}
   */
  public PublicTimelineRequest.Builder publicTimeline() {
    PublicTimelineRequest.Builder builder = PublicTimelineRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the 20 most recent @replies (status updates prefixed with
   * 
   * @username) for the authenticating user.
   *            <p>
   *            Example usage:
   *            </p>
   *            <p>
   *            <code>List<Status> replies = api.replies().build().get();</code>
   *            </p>
   * 
   * @return {@link RepliesRequest.Builder}
   */
  public RepliesRequest.Builder replies() {
    RepliesRequest.Builder builder = RepliesRequest.builder();
    setDefaults(builder);
    return builder;
  }

  void setDefaults(Request.Builder builder) {
    builder.httpManager(httpManager);
    builder.host(host);
    builder.port(port);
    builder.scheme(scheme);
  }

  /**
   * Returns a single status, specified by the id parameter below. The status's
   * author will be returned inline.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>Status status = api.showStatus().build().get();</code>
   * </p>
   * 
   * @param id The numerical ID of the status you're trying to retrieve.
   * @return {@link RepliesRequest.Builder}
   */
  public ShowStatusRequest.Builder showStatus(long id) {
    ShowStatusRequest.Builder builder = ShowStatusRequest.builder(id);
    setDefaults(builder);
    return builder;
  }

  /**
   * Updates the authenticating user's status. Requires the status parameter
   * specified below. Request must be a POST. A status update with text
   * identical to the authenticating user's current status will be ignored.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>Status status = api.updateStatus("Hello Twitter").build().post();</code>
   * </p>
   * 
   * @param status The text of your status update. Should not be more than 140
   *        characters.
   * @return {@link UpdateStatusRequest.Builder}
   */
  public UpdateStatusRequest.Builder updateStatus(String status) {
    UpdateStatusRequest.Builder builder = UpdateStatusRequest.builder(status);
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the 20 most recent statuses posted from the authenticating user.
   * It's also possible to request another user's timeline via the id parameter
   * below. This is the equivalent of the Web /archive page for your own user,
   * or the profile page for a third party.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>List<Status> userTimeline = api.userTimeline().build().get();</code>
   * </p>
   * 
   * @return {@link UserTimelineRequest.Builder}
   */
  public UserTimelineRequest.Builder userTimeline() {
    UserTimelineRequest.Builder builder = UserTimelineRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns extended information of a given user, specified by ID or screen
   * name as per the required id parameter below. This information includes
   * design settings, so third party developers can theme their widgets
   * according to a given user's preferences. You must be properly authenticated
   * to request the page of a protected user.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>User user = api.showUser().id("dewitt").build().get();</code>
   * </p>
   * 
   * @return {@link ShowUserRequest.Builder}
   */
  public ShowUserRequest.Builder showUser() {
    ShowUserRequest.Builder builder = ShowUserRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns a list of the 20 most recent direct messages sent to the
   * authenticating user.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>List<DirectMessages> directMessages = api.directMessages().build().get();</code>
   * </p>
   * 
   * @return {@link DirectMessagesRequest.Builder}
   */
  public DirectMessagesRequest.Builder directMessages() {
    DirectMessagesRequest.Builder builder = DirectMessagesRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns a list of the 20 most recent direct messages sent by the
   * authenticating user.
   * 
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>List<DirectMessages> directMessages = api.sentDirectMessages().build().get();</code>
   * </p>
   * 
   * @return {@link SentDirectMessagesRequest.Builder}
   */
  public SentDirectMessagesRequest.Builder sentDirectMessages() {
    SentDirectMessagesRequest.Builder builder = SentDirectMessagesRequest
        .builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Sends a new direct message to the specified user from the authenticating
   * user.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>DirectMessage directMessage = api.newDirectMessage("dewitt", "Hello DeWitt").build().post();</code>
   * </p>
   * 
   * @param status The text of your status update. Should not be more than 140
   *        characters.
   * @return {@link NewDirectMessageRequest.Builder}
   */
  public NewDirectMessageRequest.Builder newDirectMessage(String user,
      String status) {
    NewDirectMessageRequest.Builder builder = NewDirectMessageRequest.builder(
        user, status);
    setDefaults(builder);
    return builder;
  }

  /**
   * Destroys the direct message specified in the required ID parameter. The
   * authenticating user must be the recipient of the specified direct message.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>DirectMessage directMessage = api.destroyDirectMessage(12345).build().post();</code>
   * </p>
   * 
   * @param id The ID of the direct message to destroy.
   * @return {@link DestroyDirectMessageRequest.Builder}
   */
  public DestroyDirectMessageRequest.Builder destroyDirectMessage(long id) {
    DestroyDirectMessageRequest.Builder builder = DestroyDirectMessageRequest
        .builder(id);
    setDefaults(builder);
    return builder;
  }

  /**
   * Befriends the user specified in the ID parameter as the authenticating
   * user.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>User user = api.createFriendship("dewitt").build().post();</code>
   * </p>
   * 
   * @param id The ID or screen name of the user to befriend
   * @return {@link CreateFriendshipRequest.Builder}
   */
  public CreateFriendshipRequest.Builder createFriendship(String id) {
    CreateFriendshipRequest.Builder builder = CreateFriendshipRequest
        .builder(id);
    setDefaults(builder);
    return builder;
  }

  /**
   * Discontinues friendship with the user specified in the ID parameter as the
   * authenticating user
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>User user = api.destroyFriendship("dewitt").build().post();</code>
   * </p>
   * 
   * @param id The ID or screen name of the user with whom to discontinue
   *        friendship.
   * @return {@link DestroyFriendshipRequest.Builder}
   */
  public DestroyFriendshipRequest.Builder destroyFriendship(String id) {
    DestroyFriendshipRequest.Builder builder = DestroyFriendshipRequest
        .builder(id);
    setDefaults(builder);
    return builder;
  }

  /**
   * Tests if a friendship exists between two users.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>boolean friendshipExists = api.friendshipExists("ev", "biz).build().post();</code>
   * </p>
   * 
   * @param userA The ID or screen_name of the first user to test friendship
   *        for.
   * @param userB The ID or screen_name of the second user to test friendship
   *        for.
   * @return {@link FriendshipExistsRequest.Builder}
   */
  public FriendshipExistsRequest.Builder friendshipExists(String userA,
      String userB) {
    FriendshipExistsRequest.Builder builder = FriendshipExistsRequest.builder(
        userA, userB);
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns an array of numeric IDs for every user the specified user is
   * following.
   * 
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>long[] following = api.friendIds().build().get();</code>
   * </p>
   * 
   * @return {@link FriendIdsRequest.Builder}
   */
  public FriendIdsRequest.Builder friendIds() {
    FriendIdsRequest.Builder builder = FriendIdsRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns an array of numeric IDs for every user the specified user is
   * followed by.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>long[] followers = api.followerIds().build().get();</code>
   * </p>
   * 
   * @return {@link FollowerIdsRequest.Builder}
   */
  public FollowerIdsRequest.Builder followerIds() {
    FollowerIdsRequest.Builder builder = FollowerIdsRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns a representation of the requesting user if authentication was
   * successful.
   * 
   * @return {@link VerifyCredentialsRequest.Builder}
   */
  public VerifyCredentialsRequest.Builder verifyCredentials() {
    VerifyCredentialsRequest.Builder builder = VerifyCredentialsRequest
        .builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Ends the session of the authenticating user.
   * 
   * @return {@link EndSessionRequest.Builder}
   */
  public EndSessionRequest.Builder endSession() {
    EndSessionRequest.Builder builder = EndSessionRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Sets which device Twitter delivers updates to for the authenticating user.
   * Sending none as the device parameter will disable IM or SMS updates.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>User user = api.updateDeliveryDevice(Device.SMS).build().post();</code>
   * </p>
   * 
   * @param device Must be one of: sms, im, none.
   * @return {@link UpdateDeliveryDeviceRequest.Builder}
   */
  public UpdateDeliveryDeviceRequest.Builder updateDeliveryDevice(Device device) {
    UpdateDeliveryDeviceRequest.Builder builder = UpdateDeliveryDeviceRequest
        .builder(device);
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the top ten queries that are currently trending on Twitter. The
   * response includes the time of the request, the name of each trending topic,
   * and the url to the Twitter Search results page for that topic.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * 
   * <pre>
   * Trends trends = api.trends().build().get();
   * for (Trend trend : trends.getTrends()) {
   *   System.out.println(trend.getName());
   * }
   * </pre>
   * 
   * </p>
   * 
   * @return {@link TrendsRequest.Builder}
   */
  public TrendsRequest.Builder trends() {
    TrendsRequest.Builder builder = TrendsRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns tweets that match a specified query. You can use a variety of
   * search operators in your query.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * 
   * <pre>
   * Results results = api.search(&quot;@dewitt&quot;).build().get();
   * for (Result result : results.getResults()) {
   *   System.out.println(result.getText());
   * }
   * </pre>
   * 
   * </p>
   * 
   * @return {@link SearchRequest.Builder}
   */
  public SearchRequest.Builder search(String query) {
    SearchRequest.Builder builder = SearchRequest.builder(query);
    setDefaults(builder);
    return builder;
  }

  /**
   * Sets one or more hex values that control the color scheme of the
   * authenticating user's profile page on twitter.com.
   * 
   * <p>
   * Example usage:
   * </p>
   * <p>
   * 
   * <pre>
   * User user = api.updateProfileColors().profileBackgroundColor(&quot;FFFFFF&quot;).build()
   *     .post();
   * </pre>
   * 
   * @return {@link UpdateProfileColorsRequest.Builder}
   */
  public UpdateProfileColorsRequest.Builder updateProfileColors() {
    UpdateProfileColorsRequest.Builder builder = UpdateProfileColorsRequest
        .builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Updates the authenticating user's profile image. Expects raw multipart
   * data, not a URL to an image.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * 
   * <pre>
   * File file = new File(&quot;profile_image.png&quot;);
   * User user = api.updateProfileImage(FileUtils.readFileToByteArray()).build()
   *     .post();
   * </pre>
   * 
   * </p>
   * 
   * @return {@link UpdateProfileImageRequest.Builder}
   */
  public UpdateProfileImageRequest.Builder updateProfileImage(byte[] imageData) {
    UpdateProfileImageRequest.Builder builder = UpdateProfileImageRequest
        .builder(imageData);
    setDefaults(builder);
    return builder;
  }

  /**
   * Updates the authenticating user's profile background image. Expects raw
   * multipart data, not a URL to an image.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * 
   * <pre>
   * File file = new File(&quot;profile_background_image.png&quot;);
   * User user = api.updateProfileBackgroundImage(FileUtils.readFileToByteArray())
   *     .build().post();
   * </pre>
   * 
   * </p>
   * 
   * @return {@link UpdateProfileBackgroundImageRequest.Builder}
   */
  public UpdateProfileBackgroundImageRequest.Builder updateProfileBackgroundImage(
      byte[] imageData) {
    UpdateProfileBackgroundImageRequest.Builder builder = UpdateProfileBackgroundImageRequest
        .builder(imageData);
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the remaining number of API requests available to the requesting
   * user before the API limit is reached for the current hour. Calls to
   * rate_limit_status do not count against the rate limit. If authentication
   * credentials are provided, the rate limit status for the authenticating user
   * is returned. Otherwise, the rate limit status for the requester's IP
   * address is returned.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * 
   * <pre>
   * RateLimitStatus rateLimitStatus = api.rateLimitStatus().build().get();
   * System.out.println(rateLimitStatus.getRemainingHits());
   * </pre>
   * 
   * </p>
   * 
   * @return {@link RateLimitStatusRequest.Builder}
   */
  public RateLimitStatusRequest.Builder rateLimitStatus() {
    RateLimitStatusRequest.Builder builder = RateLimitStatusRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Sets values that users are able to set under the "Account" tab of their
   * settings page. Only the parameters specified will be updated; to only
   * update the "name" attribute, for example, only include that parameter in
   * your request.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * 
   * <pre>
   * User user = api.updateProfile().name(&quot;DeWitt Clinton&quot;).build().post();
   * </pre>
   * 
   * </p>
   * 
   * @return {@link UpdateProfileRequest.Builder}
   */
  public UpdateProfileRequest.Builder updateProfile() {
    UpdateProfileRequest.Builder builder = UpdateProfileRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Returns the 20 most recent favorite statuses for the authenticating user or
   * user specified by the ID parameter in the requested format.
   * <p>
   * Supports the <code>id</code> and <code>page</code> optional parameters.
   * </p>
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>List<Status> favorites = api.favorites().build().get();</code>
   * </p>
   * 
   * @return A FavoritesRequest.Builder supporting the <code>id</code> and
   *         <code>page</code> optional parameters.
   */
  public FavoritesRequest.Builder favorites() {
    FavoritesRequest.Builder builder = FavoritesRequest.builder();
    setDefaults(builder);
    return builder;
  }

  /**
   * Favorites the status specified in the ID parameter as the authenticating
   * user. Returns the favorite status when successful.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>Status favorite = api.createFavorite(12345).build().post();</code>
   * </p>
   * 
   * @param id The ID of the status to favorite.
   * @return A CreateFavoriteRequest.Builder.
   */
  public CreateFavoriteRequest.Builder createFavorite(long id) {
    CreateFavoriteRequest.Builder builder = CreateFavoriteRequest.builder(id);
    setDefaults(builder);
    return builder;
  }

  /**
   * Un-favorites the status specified in the ID parameter as the authenticating
   * user. Returns the un-favorited status in the requested format when
   * successful.
   * <p>
   * Example usage:
   * </p>
   * <p>
   * <code>Status favorite = api.destroyFavorite(12345).build().post();</code>
   * </p>
   * 
   * @param id The ID of the status to un-favorite
   * @return A DestroyFavoriteRequest.Builder.
   */
  public DestroyFavoriteRequest.Builder destroyFavorite(long id) {
    DestroyFavoriteRequest.Builder builder = DestroyFavoriteRequest.builder(id);
    setDefaults(builder);
    return builder;
  }
}
