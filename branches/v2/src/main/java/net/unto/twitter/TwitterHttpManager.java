package net.unto.twitter;

import net.unto.twitter.UtilProtos.Url;
import net.unto.twitter.UtilProtos.Url.Parameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * An implementation of the TwitterHttpManager interface using the Apache
 * Commons HttpClient library.
 * 
 * @author DeWitt Clinton <dewitt@unto.net>
 */
public class TwitterHttpManager implements HttpManager {
 
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {

    private Credentials credentials = null;
    private String password = null;
    private String username = null;
    private HttpConnectionManager httpConnectionManager = null;
    private AuthScope authScope = null;

    Builder() {}
    
    public TwitterHttpManager build() {
      return new TwitterHttpManager(this);
    }
    
    public Builder credentials(Credentials credentials) {
      assert(username == null);
      assert(password == null);
      this.credentials = credentials;
      return this;
    }
    
    public Builder username(String username) {
      assert(credentials == null);
      this.username = username;
      if (password != null) {
        credentials = new UsernamePasswordCredentials(username, password);
      }
      return this;
    }
    
    public Builder httpConnectionManager(HttpConnectionManager httpConnectionManager) {
      this.httpConnectionManager = httpConnectionManager;
      return this;
    }
    
    public Builder authScope(AuthScope authScope) {
      this.authScope = authScope;
      return this;
    }
    
    public Builder password(String password) {
      assert(credentials == null);
      this.password = password;
      if (username != null) {
        credentials = new UsernamePasswordCredentials(username, password);
      }
      return this;
    }
  }
  
  private AuthScope authScope = null;

  private HttpConnectionManager connectionManager = null;

  private Credentials credentials = null;

  /**
   * Construct a new {@link TwitterHttpManager} instance.
   */
  public TwitterHttpManager(Builder builder) {
    authScope = builder.authScope != null ? builder.authScope : AuthScope.ANY;
    connectionManager = builder.httpConnectionManager != null ?
        builder.httpConnectionManager :
        new MultiThreadedHttpConnectionManager();
    credentials = builder.credentials;
  }
  
  public boolean hasCredentials() {
    return credentials != null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see net.unto.twitter.TwitterHttpManager#get(java.lang.String)
   */
  public String get(Url url) {
    assert(url != null);
    String uri = UrlUtil.assemble(url);
    GetMethod method = new GetMethod(uri);
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    return execute(method);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.unto.twitter.TwitterHttpManager#post(java.lang.String)
   */
  public String post(Url url) {
    assert(url != null);
    String uri = UrlUtil.assemble(url);
    PostMethod method = new PostMethod(uri);
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    return execute(method);
  }

  private NameValuePair[] getParametersAsNamedValuePairArray(Url url) {
    List<NameValuePair> out = new ArrayList<NameValuePair>();
    for (Parameter parameter : url.getParametersList()) {
      if (parameter.hasName() && parameter.hasValue()) {
        out.add(new NameValuePair(parameter.getName(), parameter.getValue()
            .toString()));
      }
    }
    return out.toArray(new NameValuePair[out.size()]);
  }

  private String execute(HttpMethod method) {

    HttpClient httpClient = new HttpClient(connectionManager);
    if (credentials != null) {
      httpClient.getState().setCredentials(authScope, credentials);
      httpClient.getParams().setAuthenticationPreemptive(true);
    } else {
      httpClient.getParams().setAuthenticationPreemptive(false);
    }
    
    try {
      int statusCode = httpClient.executeMethod(method);
      if (statusCode != HttpStatus.SC_OK) {
        String error = String.format("Expected 200 OK. Received %d %s",
            statusCode, HttpStatus.getStatusText(statusCode));
        throw new RuntimeException(error);
      }
      String responseBody = method.getResponseBodyAsString();
      if (responseBody == null) {
        throw new RuntimeException("Expected response body, got null");
      }
      return responseBody;
    } catch (HttpException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      method.releaseConnection();
    }
  }

  public void clearCredentials() {
    this.credentials = null;
  }
}
