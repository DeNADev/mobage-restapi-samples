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
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using OAuth;
using System.Collections.Specialized;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using MyGameServer.Controllers.Commons;

namespace MyGameServer.Controllers
{
    public class RequestTokenController : Controller
    {
        //
        // GET: /RequestToken?verifier=XXXXXX

        public ActionResult Index()
        {
            string temporaryToken = (string)Session[MobageOAuth.OAUTH_TOKEN];
            string temporaryTokenSecret = (string)Session[MobageOAuth.OAUTH_TOKEN_SECRET];
            string verifier = Request["verifier"];

            if ((temporaryToken == null || temporaryToken.Length == 0) ||
                (temporaryTokenSecret == null || temporaryTokenSecret.Length == 0) ||
                (verifier == null || verifier.Length == 0))
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }

            // Create a new OAuthRequest instance
            OAuthRequest oauthRequest = MobageOAuth.GetRequestTokenOAuthRequest(temporaryToken, temporaryTokenSecret, verifier);
            // Get HTTP header authorization
            string authHeader = oauthRequest.GetAuthorizationHeader();

            HttpWebResponse response = null;
            StreamReader responseStream = null;

            string accessToken = null, accessTokenSecret = null, oauth2Token = null;

            try
            {
                // Configure HTTP request
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(oauthRequest.RequestUrl);
                request.Method = "POST";
                request.Headers.Add("Authorization", authHeader);
                request.Accept = "*/*";
                request.ContentLength = 0;

                // Send request
                response = (HttpWebResponse)request.GetResponse();

                // Get response
                responseStream = new
                    StreamReader(response.GetResponseStream(), true);
                string responseBody = responseStream.ReadToEnd();

                // Extract temporary credential
                NameValueCollection responseCollection = HttpUtility.ParseQueryString(responseBody);

                accessToken = Server.UrlDecode(responseCollection.Get("oauth_token"));
                accessTokenSecret = Server.UrlDecode(responseCollection.Get("oauth_token_secret"));
                oauth2Token = Server.UrlDecode(responseCollection.Get("oauth2_token"));

                if ((accessToken == null || accessToken.Length == 0) ||
                    (accessTokenSecret == null || accessTokenSecret.Length == 0) ||
                    (oauth2Token == null || oauth2Token.Length == 0))
                {
                    return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
                }
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

            // stores token and secret in session
            // you should store these in DB for scalability in production
            Session[MobageOAuth.OAUTH_TOKEN]  = accessToken;
            Session[MobageOAuth.OAUTH_TOKEN_SECRET] = accessTokenSecret;
            Session[MobageOAuth.OAUTH2_TOKEN] = oauth2Token;

            JObject jsonResult = new JObject();
            jsonResult["oauth_token"] = accessToken;

            return new ContentResult
            {
                Content = jsonResult.ToString(Formatting.None),
                ContentType = "application/json"
            };
        }

    }
}
