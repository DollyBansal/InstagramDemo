# InstagramDemo
Android app making some calls to Instagram API.

Here is the overall flow and thoughts behind design decisions.

- MainActivity (inside activity folder), as the name suggests in the main activity. When the app starts, this activity is called.

- When the app starts, it checks whether the user is authenticated. If not, the user is asked to get authenticated (in class AuthenticationDialog). The auth token is saved persistently in SharedPreferences.

- After the authentication, the function "onReceived" is invoked. This function starts the process of fetching the feed. In our case, the feed consists of the images that the user posted (it also includes the profile pic of the user, apparently!).

- The "onReceived" function is abstracted out in an interface named "AuthenticationListener", which the MainActivity implements. The reason for that is that onCodeReceived is
called after Authentication is done (in class AuthenticationDialog). However, the class
AuthenticationDialog is initialized in MainActivity itself. If we don't want this interface,
then, we would need to pass a reference of MainActivity to AuthenticationDialog, which is not
as clean, because in that case, all of MainActivity methods will be exposed to
AuthenticationDialog. Therefore, I prefer to have a clean interface with one method.

- The logout option is on top right of the app. The token is removed from the SharedPreferences when it is called. In addition, webview cookies are also cleared out. 

- Retrofit library is being used to make RESTApi calls. See the details of library at http://square.github.io/retrofit/. The benefit of using Retrofit is that it turns the HTTP API into a Java interface. Therefore, any response in JSON format is converted to POJO (Plain old Java Objects). This gives us type safety and other benefits.

- To handle error, helper methods are defined in ErrorHandlingUtil class (in utils folder). It is worth noting that the method "isCorrectInstagramResponse" takes in "BaseInstagramResponse" object. All Instagram responses contain the "meta" field. The code value of 200 in the meta indicates the success. Anything else indicates some sort of error. The two classes: "FeedInstagramResponse" and "LikedByInstagramResponse" extend this class. The benefit of keeping meta field in a base class is that the error handling of the response can be done in a clean manner, irrespective of the type of response. See ErrorHandlingUtil class for implementation of error handling.

- All the errors are shown using "Toast". A toast provides simple feedback about an operation in a small popup, instead of traditional dialog boxes. Toasts automatically disappear after a timeout.

- Picasso library is used to resize user feed pic. With Picasso lib, downloading, resizing, cropping, caching is easy.

- RoundedCornersTransform class is used to make the profile picture circular in UI using picasso library.

- For efficient scrolling and performance, RecyclerView.Adapter is adapter is used, instead of ListView.

- After the data is bind to RecyclerView, the user feed appears, where user can see her recent media, like count, like status, profile picture and name. 

- User can change the status of like/unlike of the media by click. We use "https://www.instagram.com/developer/endpoints/likes/"" to Post and Delete the like status.

- User can click on like count and the app navigates to LikedByActivity. The user can see the list of users who liked this particular media. Apparently, the instagram API doesn't return the full list of names of such users. 

Updates: 

- Implementing RxJava Lib with Retrofit Lib. The network requests are built via Retrofit and the calls are made asynchronously through RxJava. 
RxJava Lib is for bringing reactive programming to the Android platform,
The main use for implementing RxJava with Retrofit is when we want to do the chaining/multiple API calls because Retrofit are optimized for single network call only. 

Note: In this project we are not using chaining/multiple API calls but still I am using RxJava with Retrofit. 


