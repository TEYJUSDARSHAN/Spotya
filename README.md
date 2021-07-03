## Spotya
Android app that converts Spotify playlist to Youtube playlist

This is a simple Android application which demonstartes integration of various APIs together (Youtube Data API and Spotify API in this case).

# Working of the app.
* When the user enters the app, he has to give permission to the client to access his/her data from Youtube API on his/her behalf.
* Once the permission is granted, the user has to create an empty Playlist on Youtube by clicking the button.
* Once the playlist is created, he/she can paste the share URL of any playlist from SPOTIFY into the text field provided and then click find playlist button to search for the playlist and fetch all the songs.
* A message is displayed if the playlist exists.
* Then the user can use the populate playlist button to populate the empty youtube playlist created earlier with the song videos of the songs in SPOTIFY playlist.

# Volley 
Android Volley is the library module that has been used to send GET/POST request. It is simple to use the library although you need to write quite some code to implement a simple GET/POST request.
**Links/Resources related to Android Volley**
* https://developer.android.com/training/volley      //Basics of Volley
* https://stackoverflow.com/questions/17049473/how-to-set-custom-header-in-volley-request   //Setting custom Header to Volley Request
* https://stackoverflow.com/questions/33573803/how-to-send-a-post-request-using-volley-with-string-body  //Setting body of Volley request.

# OAuth 2.0 - How to get Google API authorization
* Youtube uses OAuth 2.0 flow to give app client access to user data.
* First of all, you need to create a project in Google developer console with all the details that it asks.
* Then you need create an API key and Oauth 2.0 Client ID
* ![image](https://user-images.githubusercontent.com/60425800/124350682-a8d5b800-dc13-11eb-9071-f6ed429343c2.png)
* After that the process is pretty straight forward as mentioned in the DOC : https://developers.google.com/youtube/v3/guides/auth/installed-apps
**The code for the same can be found in MAINActivity.java file under the respective functions (functions jhave been given descriptive names for clear understanding)

# Obtaining Spotify accessToken
* In this app, we are not trying to access any private information of the user from SPOTIFY API. So we dont need to go through Oauth 2.0 Flow.
* Create a project in SPOTIFY FOR DEVELOPERS to get your client ID and client secret.
* Use the client ID and Client Secret to Obtain the **Access token**
* Using the Access token, Playlists can easily be searched using playlist ID



