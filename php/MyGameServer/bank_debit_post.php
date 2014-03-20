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
// this is an example of create bank transaction
require_once("External/OAuth.php");
require_once("config.php");
require_once("datastore.php");

// configuring ...
$http_method  = "POST";
$url_fragment = "/bank/debit/@app";

$quantity = 2;
$body = array(
	// an array of item, currently only one item is allowed
	"items" => array(
		array(
			"item" => array(
				"id" => "item_1", // for server Bank flow, this ID does NOT need to be specified on Mobage Developers portal
				"name" => "Sample Item",
				"price" => 100,
				"description" => "This is a sample item",
				"imageUrl" => "http://mobage.com/homepage-assets/images/icon.png"
			),
			"quantity" => $quantity
		)
	),
	"comment" => "Purchase of " . $quantity . " Sample Item(s)",
	"state" => "new" // must be "new" since we are just creating it
);

// retrieve saved values
$datastore = read_array_from_session();

// generate Authentication Header
$endpoint = $bank_endpoint . $url_fragment;
$auth_header = array(
"Authorization: bearer " . $datastore["oauth2_token"],
"Accept: */*",
"Content-Type: application/json; charset=utf-8"
);

// access to platform server
$curl = curl_init($endpoint);
curl_setopt($curl, CURLOPT_POST, true);
curl_setopt($curl, CURLOPT_POSTFIELDS,json_encode($body));
curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
curl_setopt($curl, CURLOPT_FAILONERROR, false);
curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($curl, CURLOPT_ENCODING , "gzip");
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

// parse response
$decoded_response  = OAuthUtil::urldecode_rfc3986($curl_body_res);
$parsed_parameters = json_decode($decoded_response, true);

// store to session
store_array_to_session($parsed_parameters);

if ($verbose) {
	print("<h2>Session</h2>");
	print("<pre>");
	var_dump($_SESSION);
	print("</pre>");

	print("<h2>Request</h2>");
	print("<pre>".$curl_header_req."</pre>");
	print("<pre>".json_encode($body)."</pre>");
	print("<h2>Response</h2>");
	print("<pre>".$response."</pre>");
} else {
	print($curl_body_res);
}

?>
