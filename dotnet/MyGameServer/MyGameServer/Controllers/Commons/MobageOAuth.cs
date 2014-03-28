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

using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using OAuth;

namespace MyGameServer.Controllers.Commons
{
    public class MobageOAuth
    {
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

        public enum AuthType
        {
            CONSUMER_ONLY, THREE_LEGGED
        }

        public static readonly String OAUTH_TOKEN = "oauth_token";
        public static readonly String OAUTH_TOKEN_SECRET = "oauth_token_secret";
        public static readonly String OAUTH2_TOKEN = "oauth2_token";

        public static String GetConsumerKey()
        {
            if ("production".Equals(ENVIRONMENT))
            {
                return CONSUMER_KEY_PRODUCTION;
            }
            else
            {
                return CONSUMER_KEY_SANDBOX;
            }
        }

        public static String GetConsumerSecret()
        {
            if ("production".Equals(ENVIRONMENT))
            {
                return CONSUMER_SECRET_PRODUCTION;
            }
            else
            {
                return CONSUMER_SECRET_SANDBOX;
            }
        }

        public static String GetAuthEndpoint()
        {
            if ("production".Equals(ENVIRONMENT))
            {
                if ("jp".Equals(REGION))
                {
                    return "https://ssl.sp.mbga-platform.jp/social/api/oauth/v2.01";
                }
                else if ("west".Equals(REGION))
                {
                    return "https://app.mobage.com/1/" + APPKEY;
                }
                else if ("kr".Equals(REGION))
                {
                    return "https://ssl.sp.mobage-platform.kr/social/api/oauth/v2.01";
                }
                else if ("cn".Equals(REGION))
                {
                    return "http://sp.mobage-platform.cn/social/api/oauth/v2.01";
                }
                else if ("tc".Equals(REGION))
                {
                    return "http://sp.mobage-platform.tw/social/api/oauth/v2.01";
                }
            }
            else
            {
                if ("jp".Equals(REGION))
                {
                    return "https://ssl.sb.sp.mbga-platform.jp/social/api/oauth/v2.01";
                }
                else if ("west".Equals(REGION))
                {
                    return "https://app-sandbox.mobage.com/1/" + APPKEY;
                }
                else if ("kr".Equals(REGION))
                {
                    return "https://ssl.sp.sb.mobage-platform.kr/social/api/oauth/v2.01";
                }
                else if ("cn".Equals(REGION))
                {
                    return "http://sp.sb.mobage-platform.cn/social/api/oauth/v2.01";
                }
                else if ("tc".Equals(REGION))
                {
                    return "http://sp.sb.mobage-platform.tw/social/api/oauth/v2.01";
                }
            }
            return null;
        }

        public static String GetBankEndpoint()
        {
            if ("production".Equals(ENVIRONMENT))
            {
                if ("jp".Equals(REGION))
                {
                    return "https://ssl.sp.mbga-platform.jp/social/api/restful/v2.02";
                }
                else if ("west".Equals(REGION))
                {
                    return "https://bank.mobage.com/1/" + APPKEY;
                }
                else if ("kr".Equals(REGION))
                {
                    return "https://ssl.sp.mobage-platform.kr/social/api/restful/v2.02";
                }
                else if ("cn".Equals(REGION))
                {
                    return "http://sp.mobage-platform.cn/social/api/restful/v2.02";
                }
                else if ("tc".Equals(REGION))
                {
                    return "http://sp.mobage-platform.tw/social/api/restful/v2.02";
                }
            }
            else
            {
                if ("jp".Equals(REGION))
                {
                    return "https://ssl.sb.sp.mbga-platform.jp/social/api/restful/v2.02";
                }
                else if ("west".Equals(REGION))
                {
                    return "https://bank-sandbox.mobage.com/1/" + APPKEY;
                }
                else if ("kr".Equals(REGION))
                {
                    return "https://ssl.sp.sb.mobage-platform.kr/social/api/restful/v2.02";
                }
                else if ("cn".Equals(REGION))
                {
                    return "http://sp.sb.mobage-platform.cn/social/api/restful/v2.02";
                }
                else if ("tc".Equals(REGION))
                {
                    return "http://sp.sb.mobage-platform.tw/social/api/restful/v2.02";
                }
            }
            return null;
        }

        public static String GetSocialEndpoint()
        {
            if ("production".Equals(ENVIRONMENT))
            {
                if ("jp".Equals(REGION))
                {
                    return "http://sp.mbga-platform.jp/social/api/restful/v2";
                }
                else if ("west".Equals(REGION))
                {
                    return "https://app.mobage.com/1/" + APPKEY + "/opensocial";
                }
                else if ("kr".Equals(REGION))
                {
                    return "http://sp.mobage-platform.kr/social/api/restful/v2";
                }
                else if ("cn".Equals(REGION))
                {
                    return "http://sp.mobage-platform.cn/social/api/restful/v2";
                }
                else if ("tc".Equals(REGION))
                {
                    return "http://sp.mobage-platform.tw/social/api/restful/v2";
                }
            }
            else
            {
                if ("jp".Equals(REGION))
                {
                    return "http://sb.sp.mbga-platform.jp/social/api/restful/v2";
                }
                else if ("west".Equals(REGION))
                {
                    return "https://app-sandbox.mobage.com/1/" + APPKEY + "/opensocial";
                }
                else if ("kr".Equals(REGION))
                {
                    return "http://sp.sb.mobage-platform.kr/social/api/restful/v2";
                }
                else if ("cn".Equals(REGION))
                {
                    return "http://sp.sb.mobage-platform.cn/social/api/restful/v2";
                }
                else if ("tc".Equals(REGION))
                {
                    return "http://sp.sb.mobage-platform.tw/social/api/restful/v2";
                }
            }
            return null;
        }

        // Create OAuthRequest for requesting temporary credential
        public static OAuthRequest GetRequestTemporaryCredentialOAuthRequest()
        {
            // Creating a new instance directly
            return new OAuthRequest
            {
                Method = "POST",
                Type = OAuthRequestType.RequestToken,
                SignatureMethod = OAuthSignatureMethod.HmacSha1,
                ConsumerKey = MobageOAuth.GetConsumerKey(),
                ConsumerSecret = MobageOAuth.GetConsumerSecret(),
                RequestUrl = MobageOAuth.GetAuthEndpoint()+"/request_temporary_credential",
                Version = "1.0"
            };
        }

        // Create OAuthRequest for requesting access token
        public static OAuthRequest GetRequestTokenOAuthRequest(string temporaryToken, string temporaryTokenSecret, string verifier)
        {
            // Creating a new instance directly
            return new OAuthRequest
            {
                Method = "POST",
                Type = OAuthRequestType.AccessToken,
                SignatureMethod = OAuthSignatureMethod.HmacSha1,
                ConsumerKey = MobageOAuth.GetConsumerKey(),
                ConsumerSecret = MobageOAuth.GetConsumerSecret(),
                Token = temporaryToken,
                TokenSecret = temporaryTokenSecret,
                Verifier = verifier,
                RequestUrl = MobageOAuth.GetAuthEndpoint() + "/request_token",
                Version = "1.0"
            };
        }

        // Create OAuthRequest for Social API call with GET method and 3-legged signing
        public static OAuthRequest GetSocialApiGetOAuthRequest(string accessToken, string accessTokenSecret, string requestUrl)
        {
            return new OAuthRequest
            {
                Method = "GET",
                Type = OAuthRequestType.ProtectedResource,
                SignatureMethod = OAuthSignatureMethod.HmacSha1,
                ConsumerKey = MobageOAuth.GetConsumerKey(),
                ConsumerSecret = MobageOAuth.GetConsumerSecret(),
                Token = accessToken,
                TokenSecret = accessTokenSecret,
                RequestUrl = requestUrl,
                Version = "1.0"
            };
        }

        // Create OAuthRequest for Social API call with GET method and Consumer Only (2-legged) signing
        public static OAuthRequest GetSocialApiGetOAuthRequest(string requestUrl)
        {
            return new OAuthRequest
            {
                Method = "GET",
                Type = OAuthRequestType.ProtectedResource,
                SignatureMethod = OAuthSignatureMethod.HmacSha1,
                ConsumerKey = MobageOAuth.GetConsumerKey(),
                ConsumerSecret = MobageOAuth.GetConsumerSecret(),
                Token = "",
                TokenSecret = "",
                RequestUrl = requestUrl,
                Version = "1.0"
            };
        }

    }
}