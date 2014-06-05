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

$region      = "jp";    // Region: jp, kr, cn, tc, west
$environment = "sandbox"; // Environment: sandbox, production

$appKey = "YOUR_APP_KEY"; // only necessary for Mobage West
                          // NOTE: use the master app key, not the one with -iOS or -Android postfix

// consumer key and consumer secret for Sandbox environment
// NOTE: not the one under iOS or Android tabs, use the master one
$consumerKey_sandbox    = "YOUR_SANDBOX_CONSUMER_KEY";
$consumerSecret_sandbox = "YOUR_SANDBOX_CONSUMER_SECRET";

// consumer key and consumer secret for Production environment
// NOTE: not the one under iOS or Android tabs, use the master one
$consumerKey_production    = "YOUR_PRODUCTION_CONSUMER_KEY";
$consumerSecret_production = "YOUR_PRODUCTION_CONSUMER_SECRET";

$verbose = false; // set this to true for debugging

//
// Nothing you should edit below this line
//
//

$consumerKey    = "";
$consumerSecret = "";

if ($environment == "production") {
	$consumerKey    = $consumerKey_production;
	$consumerSecret = $consumerSecret_production;
} else {
	$consumerKey    = $consumerKey_sandbox;
	$consumerSecret = $consumerSecret_sandbox;
}

$auth_endpoint   = "";
$bank_endpoint   = "";
$social_endpoint = "";

if ($region == "jp") {
	if ($environment == "sandbox") {
		$auth_endpoint   = "https://ssl.sb.sp.mbga-platform.jp/social/api/oauth/v2.01";
		$bank_endpoint   = "https://ssl.sb.sp.mbga-platform.jp/social/api/restful/v2.02";
		$social_endpoint = "http://sb.sp.mbga-platform.jp/social/api/restful/v2";
	} else if ($environment == "production") {
		$auth_endpoint   = "https://ssl.sp.mbga-platform.jp/social/api/oauth/v2.01";
		$bank_endpoint   = "https://ssl.sp.mbga-platform.jp/social/api/restful/v2.02";
		$social_endpoint = "http://sp.mbga-platform.jp/social/api/restful/v2";
	}
} else if ($region == "kr") {
	if ($environment == "sandbox") {
		$auth_endpoint   = "https://ssl.sp.sb.mobage-platform.kr/social/api/oauth/v2.01";
		$bank_endpoint   = "https://ssl.sp.sb.mobage-platform.kr/social/api/restful/v2.02";
		$social_endpoint = "http://sp.sb.mobage-platform.kr/social/api/restful/v2";
	} else if ($environment == "production") {
		$auth_endpoint   = "https://ssl.sp.mobage-platform.kr/social/api/oauth/v2.01";
		$bank_endpoint   = "https://ssl.sp.mobage-platform.kr/social/api/restful/v2.02";
		$social_endpoint = "http://sp.mobage-platform.kr/social/api/restful/v2";
	}
} else if ($region == "cn") {
	if ($environment == "sandbox") {
		$auth_endpoint   = "http://sp.sb.mobage-platform.cn/social/api/oauth/v2.01";
		$bank_endpoint   = "http://sp.sb.mobage-platform.cn/social/api/restful/v2.02";
		$social_endpoint = "http://sp.sb.mobage-platform.cn/social/api/restful/v2";
	} else if ($environment == "production") {
		$auth_endpoint   = "http://sp.mobage-platform.cn/social/api/oauth/v2.01";
		$bank_endpoint   = "http://sp.mobage-platform.cn/social/api/restful/v2.02";
		$social_endpoint = "http://sp.mobage-platform.cn/social/api/restful/v2";
	}
} else if ($region == "tc") {
	if ($environment == "sandbox") {
		$auth_endpoint   = "http://sp.sb.mobage-platform.tw/social/api/oauth/v2.01";
		$bank_endpoint   = "http://sp.sb.mobage-platform.tw/social/api/restful/v2.02";
		$social_endpoint = "http://sp.sb.mobage-platform.tw/social/api/restful/v2";
	} else if ($environment == "production") {
		$auth_endpoint   = "http://sp.mobage-platform.tw/social/api/oauth/v2.01";
		$bank_endpoint   = "http://sp.mobage-platform.tw/social/api/restful/v2.02";
		$social_endpoint = "http://sp.mobage-platform.tw/social/api/restful/v2";
	}
} else if ($region == "west") {
	if ($environment == "sandbox") {
		$auth_endpoint   = "https://app-sandbox.mobage.com/1/".$appKey;
		$bank_endpoint   = "https://bank-sandbox.mobage.com/1/".$appKey;
		$social_endpoint = "https://app-sandbox.mobage.com/1/".$appKey."/opensocial";
	} else if ($environment == "production") {
		$auth_endpoint   = "https://app.mobage.com/1/".$appKey;
		$bank_endpoint   = "https://bank.mobage.com/1/".$appKey;
		$social_endpoint = "https://app.mobage.com/1/".$appKey."/opensocial";
	}
}

?>