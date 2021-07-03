package com.tapps.spotya;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    AuthorizationService authService;
    public String GoogleAPIAccessToken = "";
    public String YoutubePlaylistID = "";
    public String SpotifyAccessToken = "";

    String clientID = "02565b925cf3402db84c0f4fbf4a554f";
    String clientSecretKey = "0fa67889e4d74ee38d460b31b69f02b2";
    String clientTok = clientID + ":" + clientSecretKey;

    List<String> songNames = new ArrayList<String>();
    List<String> songVideoIDs = new ArrayList<String>();

    public int TotalSongs = 0;

    public TextView playlistUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playlistUrl = findViewById(R.id.PlaylistUrl);

    }

    public void youtubeApiTest(View view) {
        getYoutubeApiAccessToken();
    }
    public void createPlaylistOnClick(View view){createYoutubePlaylist();}
    public void getPlaylistfromspotify(View view){
        getSpotifyAccessToken();
    }


    private void getYoutubeApiAccessToken() {
        String CLIENT_ID = "871088256066-5kvc458o54fkigitn9o944508vncpibk.apps.googleusercontent.com";
        Uri REDIRECT_URI = Uri.parse("com.tapps.spotya:/oauth2redirect");
        AuthorizationServiceConfiguration serviceConfig = new AuthorizationServiceConfiguration(Uri.parse("https://accounts.google.com/o/oauth2/v2/auth"), Uri.parse("https://www.googleapis.com/oauth2/v4/token"));
        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
                serviceConfig,
                CLIENT_ID,
                ResponseTypeValues.CODE,
                REDIRECT_URI
        );
        Log.d("demo", "authbuilder set");
        AuthorizationRequest authRequest = authRequestBuilder.setScope("https://www.googleapis.com/auth/youtubepartner").build();
        authService = new AuthorizationService(this);
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        Log.d("demo", "permission grant activity gonna start");
        startActivityForResult(authIntent, 88);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("demo", "i am here inside onactivityresult of youtube");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 88) {
            Log.d("demo", "request code match");
            AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);
            Log.d("demo", "oauth token = " + resp.authorizationCode.toString());
            Log.d("demo", "got the resp and ex");
            authService.performTokenRequest(resp.createTokenExchangeRequest(),
                    new AuthorizationService.TokenResponseCallback() {
                        @Override
                        public void onTokenRequestCompleted(TokenResponse resp, AuthorizationException ex) {
                            if (resp != null) {
                                GoogleAPIAccessToken = resp.accessToken;
                                Log.d("demo", "exchange succeeded. accesstoken = " + resp.accessToken);
                            } else {
                                Log.d("demo", "exchange failed");
                            }
                        }
                    });


        } else {

        }
    }

    private void createYoutubePlaylist() {

        String endPoint = "https://youtube.googleapis.com/youtube/v3/playlists?part=snippet&key=" + "AIzaSyB_sf1bA5El8YU9m3TojHRIFnq8X8Z1qCo";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, endPoint,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("demo", response.toString());
                try {
                    YoutubePlaylistID = response.getString("id");
                    Log.d("demo", "playlist id is " + YoutubePlaylistID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseVolleyError(error);
                //Log.d("demo", error.networkResponse.data.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                Log.d("demo", "the gaccesstoken is : " + GoogleAPIAccessToken);
                headers.put("Authorization", "Bearer " + GoogleAPIAccessToken);
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                //params.put("part", "snippet, status");
                params.put("key", "AIzaSyB_sf1bA5El8YU9m3TojHRIFnq8X8Z1qCo");
                return params;
            }

            @Override
            public byte[] getBody(){
                String body;
                body = createPlaylistReqBody("Teyjus playlist", "this is a test playlist", "en");
                return body.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        try {
            String responseBody = new String(req.getBody(), "utf-8");
            Log.d("demo", "response body :" + responseBody);
            Log.d("demo", "response header : " + req.getHeaders().toString());
            Log.d("demo", "response parameter " + req.getBodyContentType());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        VolleyLog.DEBUG = true;
        queue.add(req);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String s = data.toString();
            Log.d("demo", "eroor json " + s);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            Log.d("demo", message);
        } catch (JSONException e) {
            Log.d("demo", e.getMessage());
        } catch (UnsupportedEncodingException errorr) {
            Log.d("demo", errorr.getMessage());
        }
    }

    private String createPlaylistReqBody(String title, String description, String defaultLanguage) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            //json.put("description", description);
            //json.put("defualtLanguage", defaultLanguage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject finalJSON = new JSONObject();
        try {
            finalJSON.put("snippet", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = finalJSON.toString();
        Log.d("demo", "the json string is :");
        Log.d("demo", jsonString);
        return jsonString;

        //String s = "{\"snippet\":{\"title\":\"Sample playlist created via API\",\"description\":\"This is a sample playlist description.\",\"tags\":[\"sample playlist\",\"API call\"],\"defaultLanguage\":\"en\"},\"status\":{\"privacyStatus\":\"private\"}}";
        //return s;
        //        try {
//            JSONObject j = new JSONObject(s);
//            return j;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    private void getSpotifyAccessToken() {
        String url = "https://accounts.spotify.com/api/token";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("demo", response.toString());
                try {
                    SpotifyAccessToken = response.getString("access_token").toString();
                    fetchPlaylist();
                    Log.d("demo", "the access token is :" + SpotifyAccessToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("demo", error.toString());
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(clientTok.getBytes()));
                headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                return ("grant_type=client_credentials").getBytes();
            }
        };

        queue.add(req);
    }

    public void fetchPlaylist() {
        songNames.clear();
        String shareUrl = playlistUrl.getText().toString();
        String url = "https://api.spotify.com/v1/playlists/";
        if (shareUrl.length() < 57) return;
        String playListID = shareUrl.substring(34, 56);
        url = url + playListID;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("demo", response.toString());
                Toast.makeText(getApplicationContext(),
                        "playlist found!!",
                        Toast.LENGTH_LONG)
                        .show();
                try {
                    JSONArray items = response.getJSONObject("tracks").getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        JSONObject track = item.getJSONObject("track");
                        songNames.add(track.getString("name"));
                    }
                    for (int i = 0; i < songNames.size(); i++) {
                        songNames.set(i,RemoveSpaces(songNames.get(i)));

                    }
                    for (int i = 0; i < songNames.size(); i++) {
                        Log.d("demo", "song name is "+ songNames.get(i));
                    }

                    getSongVideoIDs(songNames);
                    Log.d("demo ", "its already here!!");
//                    for(int i = 0;i < songVideoIDs.size();i++){
//                        Log.d("demo", "song video id is " + songVideoIDs.get(i));
//                    }

                    Log.d("demo", "its here");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("demo", error.toString());
                Toast.makeText(getApplicationContext(),
                        "playlist not found!!",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + SpotifyAccessToken);
                return headers;
            }
        };
        Log.d("demo", "Its here");
        queue.add(req);
        Log.d("demo", "queue command is done");
    }

    private String RemoveSpaces(String s){
        String s1 = "";
        for(int i = 0;i < s.length();i++){
            if(s.charAt(i) == ' '){
                continue;
            }else{
                s1 += s.charAt(i);
            }
        }
        return s1;
    }

    private void getSongVideoIDs(List<String> SongNames){
        for(int i = 0;i < songNames.size();i++){
            getSongID(songNames.get(i));
        }
    }

    private void getSongID(String s) {
        final String[] songID = {""};
        String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=2&q=" + s + "&key=" + "AIzaSyB_sf1bA5El8YU9m3TojHRIFnq8X8Z1qCo";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("demo", s + " song searched in google api");
                        Log.d("demo", "response : " + response.toString());
                        try {
                            JSONArray items = response.getJSONArray("items");
                            String id = items.getJSONObject(0).getJSONObject("id").getString("videoId");
                            songVideoIDs.add(id);
                            for(int i = 0;i < songVideoIDs.size();i++){
                                Log.d("demo", "sonvideo id of is " + songVideoIDs.get(i));
                            }
                            Log.d("demo", "yayyy");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("demo", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws  AuthFailureError{
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + GoogleAPIAccessToken);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        queue.add(req);
    }

    public void PopulatePlaylist(View view){
        AddVideosToPlaylist(YoutubePlaylistID, songVideoIDs);
    }

    private void AddVideosToPlaylist(String playlistID, List<String> VideoIds){
        Log.d("demo", "in Add videoS to playlist");
        TotalSongs = VideoIds.size();
        AddVideoToPlaylist(playlistID, VideoIds, 0);
//        for(int i = 0;i < VideoIds.size();i++){
//            AddVideoToPlaylist(playlistID, VideoIds.get(i));
//        }
    }

    private void AddVideoToPlaylist(String PlaylistID, List<String> VideoID, int songIndex){
        String url = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet&key=" + "AIzaSyB_sf1bA5El8YU9m3TojHRIFnq8X8Z1qCo";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("demo", response.toString());
                Log.d("demo", "added song number " + songIndex);
                if(songIndex == TotalSongs - 1){
                    Log.d("demo", "all songs finished");
                    return;
                }else{
                    AddVideoToPlaylist(PlaylistID, VideoID, songIndex + 1);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseVolleyError(error);
                if(songIndex == TotalSongs - 1){
                    Log.d("demo", "all songs finished");
                    return;
                }else{
                    AddVideoToPlaylist(PlaylistID, VideoID, songIndex + 1);
                }
                //Log.d("demo", error.networkResponse.data.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                Log.d("demo", "the gaccesstoken is : " + GoogleAPIAccessToken);
                headers.put("Authorization", "Bearer " + GoogleAPIAccessToken);
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                //params.put("part", "snippet, status");
                params.put("key", "AIzaSyB_sf1bA5El8YU9m3TojHRIFnq8X8Z1qCo");
                return params;
            }

            @Override
            public byte[] getBody(){
                String body;
                body = createPlaylistItemBody(YoutubePlaylistID, VideoID.get(songIndex));
                return body.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        try {
            String responseBody = new String(req.getBody(), "utf-8");
            Log.d("demo", "response body :" + responseBody);
            Log.d("demo", "response header : " + req.getHeaders().toString());
            Log.d("demo", "response parameter " + req.getBodyContentType());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        VolleyLog.DEBUG = true;
        queue.add(req);

    }

    private String createPlaylistItemBody(String PlaylistID,String VideoID){
        JSONObject finalBody = new JSONObject();
        JSONObject snippet = new JSONObject();
        JSONObject ResourceID = new JSONObject();
        try {
            ResourceID.put("kind", "youtube#video");
            ResourceID.put("videoId", VideoID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            snippet.put("playlistId", PlaylistID);
            snippet.put("position", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            snippet.put("resourceId", ResourceID);
            finalBody.put("snippet", snippet);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("demo", "final playlistitem body = " + finalBody.toString());
        return finalBody.toString();
    }

}