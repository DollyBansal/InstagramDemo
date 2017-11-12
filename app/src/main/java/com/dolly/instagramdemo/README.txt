This file contains the documentation of the code and overall data flow.

- MainActivity (inside activity folder), as the name suggests in the main activity.
  When the app starts, this activity is called.

- When the app starts, it checks whether the user is authenticated. If not, the user is asked
  to get authenticated. The auth token is saved persistently in SharedPreferences.

-


- Whenever the app starts for the first time, it asks the user for authentication.
  The reason for that is we do not save the authentication token in a persistent manner.
  For the sake of simplicity, the authentication token is saved only in memory, and if the

- Using Retrofit library. In this we don't have to deal with network level, threading.
  We just need to define the endpoints, what to expect and what to respond.
  It uses GSon to parse the JSON formatted response. (which is really, really fast).
  Provide support for concurrent background threads, network caching,
  as well as other features that clean up networking code substantially.

