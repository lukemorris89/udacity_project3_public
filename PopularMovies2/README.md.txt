This public version of the app doesn't contain an API key for the API used (http://api.themoviedb.org)and so won't load any data upon starting.

To fix this, please generate your own API key by signing up for an account at https://www.themoviedb.org/account/signup.
Once you have generated your API key, paste it into the strings.xml file (app/src/main/res/values/strings.xml) between tags of the string named "api_key".