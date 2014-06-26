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
using System.Text;
using System.Web;
using System.Web.Mvc;
using MyGameServer.Models.Bank;
using OAuth;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using MyGameServer.Controllers.Commons;

namespace MyGameServer.Controllers
{
    public class BankDebitController : Controller
    {
        //
        // GET: /BankDebit/
        // Create a new Bank transaction

        public ActionResult Index()
        {
            HttpWebResponse response = null;
            StreamReader responseStream = null;

            string responseBody = null;

            try
            {
                // you will need to store actual items in DB and get them from there
                // here we use a 100-coin dummy item as sample
                string postData = CreateSampleItem();
                UTF8Encoding encoding = new UTF8Encoding();
                byte[] postByte = encoding.GetBytes(postData);

                // Bank API uses OAuth2 bearer token
                string authHeader = "Bearer " + Session[MobageOAuth.OAUTH2_TOKEN];

                HttpWebRequest request =
                    (HttpWebRequest)WebRequest.Create(MobageOAuth.GetBankEndpoint() + "/bank/debit/@app");
                request.Method = "POST";
                request.Headers.Add("Authorization", authHeader);
                request.Accept = "*/*";
                request.ContentType = "application/json; charset=utf-8";
                request.ContentLength = postByte.Length;

                Stream requestStream = request.GetRequestStream();
                requestStream.Write(postByte, 0, postByte.Length);

                response = (HttpWebResponse)request.GetResponse();

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

            JObject jsonResult = new JObject();
            jsonResult["result"] = "success";
            jsonResult["payload"] = JObject.Parse(responseBody);;

            return new ContentResult
            {
                Content = jsonResult.ToString(Formatting.None),
                ContentType = "application/json"
            };
        }

        //
        // GET: /BankDebit/Open?transaction=TTTTTTTT
        // Open Bank transaction after user confirmation
        
        public ActionResult Open()
        {
            return this.UpdateTransaction(Request["transaction"], "open");
        }

        //
        // GET: /BankDebit/Close?transaction=TTTTTTTT
        // Close Bank transaction after open

        public ActionResult Close()
        {
            return this.UpdateTransaction(Request["transaction"], "closed");
        }

        //
        // GET: /BankDebit/Cancel?transaction=TTTTTTTT
        // Cancel Bank transaction

        public ActionResult Cancel()
        {
            return this.UpdateTransaction(Request["transaction"], "canceled");
        }

        private ActionResult UpdateTransaction(string transactionId, string state)
        {
            HttpWebResponse response = null;
            StreamReader responseStream = null;

            string responseBody = null;

            if ((transactionId == null || transactionId.Length == 0) ||
                (state == null || state.Length == 0))
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }

            try
            {
                // prepare post data
                JObject stateObj = new JObject();
                stateObj["state"] = state;
                string postData = stateObj.ToString();
                UTF8Encoding encoding = new UTF8Encoding();
                byte[] postByte = encoding.GetBytes(postData);

                // Bank API uses OAuth2 bearer token
                string authHeader = "Bearer " + Session[MobageOAuth.OAUTH2_TOKEN];

                HttpWebRequest request =
                    (HttpWebRequest)WebRequest.Create(MobageOAuth.GetBankEndpoint() + "/bank/debit/@app/" + transactionId + "?fields=state");
                request.Method = "PUT";
                request.Headers.Add("Authorization", authHeader);
                request.Accept = "*/*";
                request.ContentType = "application/json; charset=utf-8";
                request.ContentLength = postByte.Length;

                Stream requestStream = request.GetRequestStream();
                requestStream.Write(postByte, 0, postByte.Length);

                response = (HttpWebResponse)request.GetResponse();

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

            JObject jsonResult = new JObject();
            jsonResult["result"] = "success";
            jsonResult["payload"] = JObject.Parse(responseBody); ;

            return new ContentResult
            {
                Content = jsonResult.ToString(Formatting.None),
                ContentType = "application/json"
            };
        }

        private string CreateSampleItem()
        {
            Item item = new Item();
            item.id = "item_1"; // for server Bank flow, this ID does NOT need to be specified on Mobage Developers portal
            item.name = "Sample Item";
            item.price = 100;
            item.description = "This is a sample item";
            item.imageUrl = "http://mobage.com/homepage-assets/images/icon.png";

            BillingItem billingItem = new BillingItem();
            billingItem.item = item;
            billingItem.quantity = 1;

            List<BillingItem> itemList = new List<BillingItem>();
            itemList.Add(billingItem);

            TransactionItems transactionItems = new TransactionItems();
            transactionItems.items = itemList;
            transactionItems.comment = "This is a sample transaction"; // this comment will be visible in user's 
                                                                       // Mobage bank book in Community UI
            transactionItems.state = "new";

            return JsonConvert.SerializeObject(transactionItems);
        }
    }
}
