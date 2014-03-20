<?php
/**
 * The MIT License (MIT)
 * Copyright (c) 2014 DeNA Co., Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
// this is an example of calling RemoteNotification API to send user-to-user or game-to-user notification
require_once("External/OAuth.php");
require_once("config.php");
require_once("datastore.php");

// configuring ...
$http_method  = "POST";
$url_fragment = "/remote_notification/@app";
$req_params   = array();

$send_type = 0; // 0: game-to-user, consumer only
                // 1: user-to-user, 3-legged
if (isset($_REQUEST["type"])) {
	$send_type = $_REQUEST["type"];
}

if (isset($_REQUEST["group_id"])) {
	$url_fragment .= "/" . $_REQUEST["group_id"];
} else {
	$url_fragment .= "/@all";
}

if (isset($_REQUEST["user_ids"])) {
	$url_fragment .= "/" . $_REQUEST["user_ids"];
}

$body_urlencoded = "";
if (isset($_REQUEST["payload"])) {
	$req_params  = array("payload"=>$_REQUEST["payload"]);
	$body_urlencoded = "payload=".urlencode($_REQUEST["payload"]);
}

// retrieve saved values
$datastore = read_array_from_session();

// creating consumer
$sig_method = new OAuthSignatureMethod_HMAC_SHA1(); // signature method
$consumer   = new OAuthConsumer($consumerKey, $consumerSecret, NULL); // Consumer
$token      = new OAuthToken($datastore["oauth_token"], $datastore["oauth_token_secret"]); // Token

// generate Authentication Header
$endpoint = $social_endpoint . $url_fragment;
$enc_params = $req_params;
$request = NULL;
if ($send_type == 1) {
	$request = OAuthRequest::from_consumer_and_token($consumer, $token, $http_method, $endpoint, $enc_params);
	$request->sign_request($sig_method, $consumer, $token);
} else {
	$request = OAuthRequest::from_consumer_and_token($consumer, NULL, $http_method, $endpoint, $enc_params);
	$request->sign_request($sig_method, $consumer, NULL);
}
$auth_header =  array(
	$request->to_header(""),
	"Content-Type: application/x-www-form-urlencoded",
	"Accept:application/json" // without this server returns error 500
);

// Access to platform server
$curl = curl_init($endpoint);
curl_setopt($curl, CURLOPT_POST, true);
curl_setopt($curl, CURLOPT_POSTFIELDS, $body_urlencoded);
curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
curl_setopt($curl, CURLOPT_FAILONERROR, false);
curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($curl, CURLOPT_ENCODING, "gzip");
curl_setopt($curl, CURLOPT_HTTPHEADER, $auth_header);
curl_setopt($curl, CURLINFO_HEADER_OUT, true);
curl_setopt($curl, CURLOPT_HEADER, true);
$response = curl_exec($curl);

// request header
$curl_header_req      = curl_getinfo($curl, CURLINFO_HEADER_OUT);
// response header and body
$curl_header_res_size = curl_getinfo($curl, CURLINFO_HEADER_SIZE);
$curl_header_res      = substr($response, 0, $curl_header_res_size);
$curl_body_res        = substr($response, $curl_header_res_size);

curl_close($curl);

if ($verbose) {
	print("<h2>Session</h2>");
	print("<pre>");
	var_dump($_SESSION);
	print("</pre>");
	
	print("<h2>Request</h2>");
	print("<pre>".$curl_header_req."</pre>");
	print("<pre>".$body_urlencoded."</pre>");
	print("<h3>OAuth Signature Base String:</h3>");
	print("<pre>".$request->base_string."</pre>");
	
	print("<h2>Response</h2>");
	print("<pre>".$response."</pre>");
} else {
	print($curl_body_res);
}

?>
