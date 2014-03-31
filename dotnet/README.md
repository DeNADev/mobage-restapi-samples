mobage-restapi-samples/dotnet
=============================

This is a set of samples that shows how to use the Mobage REST APIs in ASP.NET MVC 4 project.
The sample project has been tested on Microsoft Visual Studio Express 2012 for Web.

We use external libraries for OAuth authorization and signature.
It is more cost effective to use existing tested OAuth libraries instead of creating your own ones.

These libraries can be pulled into the project easily using the NuGet package manager. 
Please check out the NuGet package management tool for the dependencies.

To the bare minimum, do the following to start experimenting with the samples.

Configuration
-------------

Open dotnet\MyGameServer\MyGameServer.sln solution file in Visual Studio.

First, you have to configure the following in 
dotnet\MyGameServer\MyGameServer\Controllers\Commons\MobageOAuth.cs

```
// Region: jp, kr, cn, tc, west
private static readonly String REGION = "jp";

// Environment: sandbox, production
private static readonly String ENVIRONMENT = "sandbox";

// only necessary for Mobage West
// NOTE: use the master app key, NOT the one with -iOS or -Android postfix
private static readonly String APPKEY = "YOUR_APP_KEY";

// consumer key and consumer secret for Sandbox environment
// NOTE: not the one under iOS or Android tabs, use the master one
private static readonly String CONSUMER_KEY_SANDBOX = "YOUR_SANDBOX_CONSUMER_KEY";
private static readonly String CONSUMER_SECRET_SANDBOX = "YOUR_SANDBOX_CONSUMER_SECRET";

// consumer key and consumer secret for Production environment
// NOTE: not the one under iOS or Android tabs, use the master one
private static readonly String CONSUMER_KEY_PRODUCTION = "YOUR_PRODUCTION_CONSUMER_KEY";
private static readonly String CONSUMER_SECRET_PRODUCTION = "YOUR_PRODUCTION_CONSUMER_SECRET";
```

Authorization
-------------

After above configuration, debug run the project in Visual Studio.
However, Internet Explorer will try to download the JSON result on each request, we recommend using Google Chrome for testing out this sample.

In the web browser, access the following URL to obtain the temporary token.

http://localhost:50061/RequestTemporaryCredential

You should see the temporary credential returned by Mobage platform. It might look different on different Mobage platforms.

```
{"result":"success","payload":{"oauth_token":"temporary_credential:368302b3bef8e4aca89c0bc0bae5f3049c79f6fc"}}
```

If you access the above URL from within your app, you would be able to parse the returned JSON, extract the token and pass it onto the authorizeToken API in Mobage Native/Unity SDK to get the verifier string.

Of course, to quickly test the process copy and paste the temporary token into your client Android or iOS code, passing it to the authorizeToken method to get the verifier. You would need to output the verifier using some Log functions.

Copy the verifier string and return to the browser. 

Pass the verifier string to the server like this
http://localhost:50061/RequestToken?verifier=1ebdc1d32a922d08123fba52ff9b805743680a16a2e2c0b3e2e2f0f62d6cc99d

You would see the success message.
```
{"result":"success","payload":{}}
```

Now you are authorized to use other Mobage REST APIs.

A Starting Point (People API)
-----------------------------

We will use the People API to walk you further.

The sample implementation supports both Client Only Signing (think of it as calling REST API as a game and not tied to any particular user) and 3-legged Authorization Signing (think of it as calling REST API as a user).

By default, PeopleController.cs uses 3-legged Authorization Signing.

### Calling People API with 3-legged Authorization Signing

In the above web browser, access 
http://localhost:50061/People

That's it! This will get the currently logged in user. You can pass additional query parameters defined in respective REST API docs to above URL to customize which fields to retrieve.

### Calling People API with Client Only Signing

In the above web browser, access
http://localhost:50061/People?type=0&user_ids=38215

The "type" query parameter requests for a Client Only Signing request.

You will get a similar result as above, but this time you will notice that you have to specifically specify the user ID to retrieve. This is because you are calling the API as a game instead of a specific user, so the platform needs to know whose info you want to get.

In the bundled PHP sample, you can use almost all Mobage REST API but at the moment only the following are implemented in this .NET sample:
- OAuth authorization
- People API
- Bank API

Finally Some Notes on Parameters
--------------------------------
There are two types of query parameters that you can pass to the sample server:
1. Parameters that are used by the controllers themselves
2. Parameters that are passed as-is to the Moabge REST APIs

You will be able to get the names of parameter type (1) from the source code. For example, in PeopleController.cs, you will find "type", "user_ids", "group_id" and "person_ids".

Please note that this is still work in progress and if you find any issue, please let us know.

Thanks.
