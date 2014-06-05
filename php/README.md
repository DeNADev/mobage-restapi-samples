mobage-restapi-samples/php
======================

This is a set of samples that shows how to use the Mobage REST APIs in PHP.

Each file is named such that it follows the pattern [API NAME]_[HTTP Method].php with a few exceptions that is self-explained. Also, file names might indicate the platform that it supports, for example remote_notification_post_jp_kr_cn_tc.php when implementations differ largely between platforms.

NOTE: We use the OAuth library (OAuth.php) from the following location. Please see MyGameServer/External/LICENSE.txt. We encourage developers to use existing and well established OAuth libraries to authenticate with Mobage because it is very time consuming and error prone to brew your own.  
Project Page Link > http://oauth.googlecode.com/  
Ouath.php Link > http://oauth.googlecode.com/svn/code/php/


NOTE: Currently only PHP samples are provided, samples for other languages might be added in the future depending on developer demand. Please contact your point of connection if you have such needs.

To the bare minimum, do the following to start experimenting with the samples.

Authorization
-------------

1. Copy all files under a PHP-enabled web server directory.
2. Edit config.php, change the values for $region, $environment, $appKey, $consumerKey's and $consumerSecret's.
3. Open a web browser and access http://your_host/and/path/to/php/request_temporary_credential.php.
4. You should see the temporary credential returned by Mobage platform. 
5. Copy the value for "oatuh_token". Please note that the token might be URL encoded, in that case, copy the one before URL encoding.
6. Paste the temporary token into your client Android or iOS code, passing it to the authorizeToken method to get the verifier. You would need to output the verifier using some Log functions.
7. Copy the verifier string and return to the browser. Access http://your_host/and/path/to/php/request_token.php?verifier=PASTE_VERIFIER_STRING_HERE.
8. Now you should be authorized to use other Mobage REST APIs.

A Starting Point (People API)
-----------------------------

We will use the People API (people_get.php) to walk you further.

people_get.php supports both Client Only Signing (think of it as calling REST API as a game and not tied to any particular user) and 3-legged Authorization Signing (think of it as calling REST API as a user).

By default, people_get.php uses 3-legged Authorization Signing.

### Calling People API with 3-legged Authorization Signing

In the above web browser, access http://your_host/and/path/to/php/people_get.php.

That's it! This will get the currently logged in user. You can pass additional query parameters defined in respective REST API docs to above URL to customize which fields to retrieve.

### Calling People API with Client Only Signing

In the above web browser, access http://your_host/and/path/to/php/people_get.php?type=0&user_ids=38215

You will get exactly the same result as above, but this time you will notice that you have to specifically specify the user ID to retrieve. This is because you are calling the API as a game instead of a specific user, so the platform needs to know whose info you want to get.

Finally Some Notes on Parameters
--------------------------------
There are two types of query parameters that you can pass to the sample PHP scripts:
1. Parameters that are used by the script itself
2. Parameters that are passed as-is to the Moabge REST APIs

You will be able to get the names of parameter type (1) from the script itself. They are usually defined near the beginning of the script. For example, in people_get.php, you will find "type", "user_ids", "group_id" and "person_ids".

Please note that this is still work in progress and if you find any issue, please contact your Mobage technical point of contact.

Thanks.
