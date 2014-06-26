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
// this is an example of putting a bank transaction to different state
require_once("External/OAuth.php");
require_once("config.php");
require_once("datastore.php");

// configuring ...
$http_method  = "PUT";
$url_fragment = "/bank/debit/@app";

// retrieve saved values
$datastore = read_array_from_session();

// prioritized transaction id given in request
if (isset($_REQUEST['transaction_id'])) {
	$url_fragment .= "/".$_REQUEST['transaction_id']."?fields=state";
} else if (isset($datastore['id'])) {
	$url_fragment .= "/".$datastore['id']."?fields=state";
}

$body = array(
	"state" => $_REQUEST['state']
);

// generate Authentication Header
$endpoint = $bank_endpoint . $url_fragment;
$auth_header = array(
"Authorization: Bearer " . $datastore["oauth2_token"],
"Accept: */*",
"Content-Type: application/json; charset=utf-8"
);

// access to platform server
$putString = json_encode($body); 
$putData = tmpfile(); 
fwrite($putData, $putString); 
fseek($putData, 0); 

$curl = curl_init($endpoint);
curl_setopt($curl, CURLOPT_PUT, true);
curl_setopt($curl, CURLOPT_INFILE, $putData);
curl_setopt($curl, CURLOPT_INFILESIZE, strlen($putString));
curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
curl_setopt($curl, CURLOPT_FAILONERROR, false);
curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($curl, CURLOPT_ENCODING , "gzip");
curl_setopt($curl, CURLOPT_HTTPHEADER, $auth_header);
curl_setopt($curl, CURLINFO_HEADER_OUT, true);
curl_setopt($curl, CURLOPT_HEADER, true);
$response = curl_exec($curl);
fclose($putData);

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
	print("<pre>".json_encode($body)."</pre>");
	print("<h2>Response</h2>");
	print("<pre>".$response."</pre>");
} else {
	print($curl_body_res);
}

?>
