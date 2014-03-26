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

package com.mobage.sample.commons;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class MobageOAuth {
    
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
    
    public enum AuthType {
        CONSUMER_ONLY, THREE_LEGGED
    }
    
    public static final String OAUTH_TOKEN        = "oauth_token";
    public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
    public static final String OAUTH2_TOKEN       = "oauth2_token";

    public static String getConsumerKey() {
        if ("production".equals(ENVIRONMENT)) {
            return CONSUMER_KEY_PRODUCTION;
        } else {
            return CONSUMER_KEY_SANDBOX;
        }
    }
    
    public static String getConsumerSecret() {
        if ("production".equals(ENVIRONMENT)) {
            return CONSUMER_SECRET_PRODUCTION;
        } else {
            return CONSUMER_SECRET_SANDBOX;
        }
    }
    
    public static String getAuthEndpoint() {
        if ("production".equals(ENVIRONMENT)) {
            if ("jp".equals(REGION)) {
                return "https://ssl.sp.mbga-platform.jp/social/api/oauth/v2.01";
            } else if ("west".equals(REGION)) {
                return "https://app.mobage.com/1/"+APPKEY;
            } else if ("kr".equals(REGION)) {
                return "https://ssl.sp.mobage-platform.kr/social/api/oauth/v2.01";
            } else if ("cn".equals(REGION)) {
                return "http://sp.mobage-platform.cn/social/api/oauth/v2.01";
            } else if ("tc".equals(REGION)) {
                return "http://sp.mobage-platform.tw/social/api/oauth/v2.01";
            }
        } else {
            if ("jp".equals(REGION)) {
                return "https://ssl.sb.sp.mbga-platform.jp/social/api/oauth/v2.01";
            } else if ("west".equals(REGION)) {
                return "https://app-sandbox.mobage.com/1/"+APPKEY;
            } else if ("kr".equals(REGION)) {
                return "https://ssl.sp.sb.mobage-platform.kr/social/api/oauth/v2.01";
            } else if ("cn".equals(REGION)) {
                return "http://sp.sb.mobage-platform.cn/social/api/oauth/v2.01";
            } else if ("tc".equals(REGION)) {
                return "http://sp.sb.mobage-platform.tw/social/api/oauth/v2.01";
            }
        }
        return null;
    }
    
    public static String getBankEndpoint() {
        if ("production".equals(ENVIRONMENT)) {
            if ("jp".equals(REGION)) {
                return "https://ssl.sp.mbga-platform.jp/social/api/restful/v2.02";
            } else if ("west".equals(REGION)) {
                return "https://bank.mobage.com/1/"+APPKEY;
            } else if ("kr".equals(REGION)) {
                return "https://ssl.sp.mobage-platform.kr/social/api/restful/v2.02";
            } else if ("cn".equals(REGION)) {
                return "http://sp.mobage-platform.cn/social/api/restful/v2.02";
            } else if ("tc".equals(REGION)) {
                return "http://sp.mobage-platform.tw/social/api/restful/v2.02";
            }
        } else {
            if ("jp".equals(REGION)) {
                return "https://ssl.sb.sp.mbga-platform.jp/social/api/restful/v2.02";
            } else if ("west".equals(REGION)) {
                return "https://bank-sandbox.mobage.com/1/"+APPKEY;
            } else if ("kr".equals(REGION)) {
                return "https://ssl.sp.sb.mobage-platform.kr/social/api/restful/v2.02";
            } else if ("cn".equals(REGION)) {
                return "http://sp.sb.mobage-platform.cn/social/api/restful/v2.02";
            } else if ("tc".equals(REGION)) {
                return "http://sp.sb.mobage-platform.tw/social/api/restful/v2.02";
            }
        }
        return null;
    }
    
    public static String getSocialEndpoint() {
        if ("production".equals(ENVIRONMENT)) {
            if ("jp".equals(REGION)) {
                return "http://sp.mbga-platform.jp/social/api/restful/v2";
            } else if ("west".equals(REGION)) {
                return "https://app.mobage.com/1/"+APPKEY+"/opensocial";
            } else if ("kr".equals(REGION)) {
                return "http://sp.mobage-platform.kr/social/api/restful/v2";
            } else if ("cn".equals(REGION)) {
                return "http://sp.mobage-platform.cn/social/api/restful/v2";
            } else if ("tc".equals(REGION)) {
                return "http://sp.mobage-platform.tw/social/api/restful/v2";
            }
        } else {
            if ("jp".equals(REGION)) {
                return "http://sb.sp.mbga-platform.jp/social/api/restful/v2";
            } else if ("west".equals(REGION)) {
                return "https://app-sandbox.mobage.com/1/"+APPKEY+"/opensocial";
            } else if ("kr".equals(REGION)) {
                return "http://sp.sb.mobage-platform.kr/social/api/restful/v2";
            } else if ("cn".equals(REGION)) {
                return "http://sp.sb.mobage-platform.cn/social/api/restful/v2";
            } else if ("tc".equals(REGION)) {
                return "http://sp.sb.mobage-platform.tw/social/api/restful/v2";
            }
        }
        return null;
    }
    
    public static OAuthProvider getOAuthProvider() {
        // create a new service provider object and configure it with
        // the URLs which provide temporary token and access tokens
        OAuthProvider p =  new DefaultOAuthProvider(
                MobageOAuth.getAuthEndpoint()+"/request_temporary_credential", 
                MobageOAuth.getAuthEndpoint()+"/request_token", 
                "http://oob" /* this is just a dummy URL, will be stripped off below */);
        p.setOAuth10a(true);
        return p;
    }
    
    public static OAuthConsumer getOAuthConsumer() {
        // create a consumer object and configure it with the consumer key and consumer secret
        return new DefaultOAuthConsumer(MobageOAuth.getConsumerKey(), MobageOAuth.getConsumerSecret());
    }
}
