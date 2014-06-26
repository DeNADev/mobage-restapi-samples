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
using System.Collections.Specialized;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using MyGameServer.Controllers.Commons;
using OAuth;
using System.IO;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;

namespace MyGameServer.Controllers
{
    public class PeopleController : Controller
    {
        //
        // GET: /People

        public ActionResult Index()
        {
            // 0: consumer only, 2-legged, Trusted Model
            // 1: 3-legged, Proxy Model
            MobageOAuth.AuthType authType = MobageOAuth.AuthType.THREE_LEGGED;
            String typeParam = Request["type"];
            if (typeParam != null)
            {
                int type = Convert.ToInt32(typeParam);
                if (type == 0)
                {
                    authType = MobageOAuth.AuthType.CONSUMER_ONLY;
                }
            }

            OAuthRequest oauthRequest = null;
            string requestUrl = MobageOAuth.GetSocialEndpoint() + getUrlFragment();

            if (authType == MobageOAuth.AuthType.CONSUMER_ONLY)
            {
                // Create a new OAuthRequest instance
                oauthRequest = MobageOAuth.GetSocialApiGetOAuthRequest(requestUrl);
            }
            else
            {
                string accessToken = (string)Session[MobageOAuth.OAUTH_TOKEN];
                string accessTokenSecret = (string)Session[MobageOAuth.OAUTH_TOKEN_SECRET];

                if ((accessToken == null || accessToken.Length == 0) ||
                    (accessTokenSecret == null || accessTokenSecret.Length == 0))
                {
                    return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
                }

                // Create a new OAuthRequest instance
                oauthRequest = MobageOAuth.GetSocialApiGetOAuthRequest(accessToken, accessTokenSecret, requestUrl);
            }

            HttpWebResponse response = null;
            StreamReader responseStream = null;

            string responseBody = null;

            try
            {
                // Get HTTP header authorization
                string authHeader = oauthRequest.GetAuthorizationHeader(GetParameters());

                // Configure HTTP request
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(oauthRequest.RequestUrl);
                request.Method = "GET";
                request.Headers.Add("Authorization", authHeader);
                request.Accept = "*/*";
                request.ContentLength = 0;

                // Send request
                response = (HttpWebResponse)request.GetResponse();

                // Get response
                responseStream = new
                    StreamReader(response.GetResponseStream(), true);
                responseBody = responseStream.ReadToEnd();
            }
            catch (WebException wex)
            {
                if (wex.Status == WebExceptionStatus.ProtocolError)
                {
                    HttpWebResponse wr = (HttpWebResponse)wex.Response;
                    if (wr != null)
                    {
                        return new HttpStatusCodeResult(wr.StatusCode);
                    }
                }
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError, wex.Message);
            }
            catch (Exception e)
            {
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError, e.Message);
            }
            finally
            {
                if (responseStream != null) { responseStream.Close(); }
                if (response != null) { response.Close(); }
            }

            JObject jsonResult = JObject.Parse(responseBody);

            return new ContentResult
            {
                Content = jsonResult.ToString(Formatting.None),
                ContentType = "application/json"
            };
        }

        private String getUrlFragment()
        {
            String urlFragment = "/people";

            // check whether optional user ids is set
            String userIds = Request["user_ids"];
            if (userIds != null && userIds.Length > 0)
            {
                urlFragment += "/" + userIds;
            }
            else
            {
                urlFragment += "/@me";
            }

            String groupId = Request["group_id"];
            if (groupId != null && groupId.Length > 0)
            {
                urlFragment += "/" + groupId;

                String personIds = Request["person_ids"];
                if (personIds != null && personIds.Length > 0)
                {
                    urlFragment += "/" + personIds;
                }
            }
            else
            {
                urlFragment += "/@self";
            }

            // for other query params
            bool isFirstElem = true;
            NameValueCollection queryParams = Request.QueryString;
            foreach (string key in queryParams)
            {
                if (!"type".Equals(key) &&
                   !"user_ids".Equals(key) &&
                   !"group_id".Equals(key) &&
                   !"person_ids".Equals(key))
                {
                    if (isFirstElem)
                    {
                        urlFragment += "?";
                        isFirstElem = false;
                    }
                    else
                    {
                        urlFragment += "&";
                    }
                    urlFragment += key + "=" + queryParams[key];
                }
            }

            return urlFragment;
        }

        private NameValueCollection GetParameters()
        {
            NameValueCollection result = new NameValueCollection();

            NameValueCollection queryParams = Request.QueryString;
            foreach (string key in queryParams)
            {
                if (!"type".Equals(key) &&
                   !"user_ids".Equals(key) &&
                   !"group_id".Equals(key) &&
                   !"person_ids".Equals(key))
                {
                    result[key] = queryParams[key];
                }
            }

            return result;
        }

    }
}
