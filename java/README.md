mobage-restapi-samples/java
===========================

This is a set of samples that shows how to use the Mobage REST APIs in Java servlets.

We use external libraries for OAuth authorization and signature.
It is more cost effective to use existing tested OAuth libraries instead of creating your own ones.

These libraries can be pulled into the project easily using Maven (http://maven.apache.org/). Please check out the pom.xml file in java/MyGameServer folder for the dependencies.

To the bare minimum, do the following to start experimenting with the samples.

Import project into Eclipse
---------------------------

We are writing for Eclipse here but it should not matter what IDE or editor you use.

Generate Eclipse project files as follows:
  $ cd java/MyGameServer
  $ mvn eclipse:eclipse -Dwtpversion=2.0

Then you can import MyGameServer into Eclipse as a web project.

Also, the mvn command can help create the war archive, if you wish.

  $ mvn package

The output war is put inside java/MyGameServer/target.

Configuration
-------------

First, you have to configure the following in 
java/MyGameServer/src/main/java/com/mobage/sample/commons/MobageOAuth.java

```
// Region: jp, kr, cn, tc, west
private static final String REGION = "jp";

// Environment: sandbox, production
private static final String ENVIRONMENT = "sandbox";

// only necessary for Mobage West
// NOTE: use the master app key, not the one with -iOS or -Android postfix
private static final String APPKEY = "YOUR_APP_KEY";

// consumer key and consumer secret for Sandbox environment
// NOTE: not the one under iOS or Android tabs, use the master one
private static final String CONSUMER_KEY_SANDBOX    = "YOUR_SANDBOX_CONSUMER_KEY";
private static final String CONSUMER_SECRET_SANDBOX = "YOUR_SANDBOX_CONSUMER_SECRET";

// consumer key and consumer secret for Production environment
// NOTE: not the one under iOS or Android tabs, use the master one
private static final String CONSUMER_KEY_PRODUCTION    = "YOUR_PRODUCTION_CONSUMER_KEY";
private static final String CONSUMER_SECRET_PRODUCTION = "YOUR_PRODUCTION_CONSUMER_SECRET";
```

Authorization
-------------

After above configuration, deploy the servlets on a servlet container such as Tomcat and run it.

Then, open a web browser and access the following URL to obtain the temporary token.

http://localhost:8080/MyGameServer/request_temporary_credential

You should see the temporary credential returned by Mobage platform. It might look different on different Mobage platforms.

```
{"result":"success","payload":{"token":"temporary_credential:0b2ec2f1ef2c20e68cd3cba23cada1b46d5bea54"}}
```

If you access the above URL from within your app, you would be able to parse the returned JSON, extract the token and pass it onto the authorizeToken API in Mobage Native/Unity SDK to get the verifier string.

Of course, to quickly test the process copy and paste the temporary token into your client Android or iOS code, passing it to the authorizeToken method to get the verifier. You would need to output the verifier using some Log functions.

Copy the verifier string and return to the browser. 

Pass the verifier string to the server like this
http://localhost:8080/MyGameServer/request_token?verifier=3516550505a6bca94a9e818a84b4f306fcb7ba9dd312f9247d93c7f42d94afb0

You would see the success message.
```
{"result":"success","payload":{}}
```

Now you are authorized to use other Mobage REST APIs.

A Starting Point (People API)
-----------------------------

We will use the People API to walk you further.

The sample implementation supports both Client Only Signing (think of it as calling REST API as a game and not tied to any particular user) and 3-legged Authorization Signing (think of it as calling REST API as a user).

By default, PeopleGet.java uses 3-legged Authorization Signing.

### Calling People API with 3-legged Authorization Signing

In the above web browser, access 
http://localhost:8080/MyGameServer/people_get

That's it! This will get the currently logged in user. You can pass additional query parameters defined in respective REST API docs to above URL to customize which fields to retrieve.

### Calling People API with Client Only Signing

In the above web browser, access
http://localhost:8080/MyGameServer/people_get?type=0&user_ids=38215

The "type" query parameter requests for a Client Only Signing request.

You will get exactly the same result as above, but this time you will notice that you have to specifically specify the user ID to retrieve. This is because you are calling the API as a game instead of a specific user, so the platform needs to know whose info you want to get.

In the bundled PHP sample, you can use almost all Mobage REST API but at the moment this Java sample is limited to the following:
- OAuth authorization
- People API
- Bank API

Finally Some Notes on Parameters
--------------------------------
There are two types of query parameters that you can pass to the sample server:
1. Parameters that are used by the servlets themselves
2. Parameters that are passed as-is to the Moabge REST APIs

You will be able to get the names of parameter type (1) from the source code. For example, in PeopleGet.java, you will find "type", "user_ids", "group_id" and "person_ids".

Please note that this is still work in progress and if you find any issue, please let us know.

Thanks.
