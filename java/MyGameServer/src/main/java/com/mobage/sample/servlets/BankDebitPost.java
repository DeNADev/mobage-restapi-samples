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

package com.mobage.sample.servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobage.sample.commons.MobageOAuth;
import com.mobage.sample.datatype.bank.BillingItem;
import com.mobage.sample.datatype.bank.Item;
import com.mobage.sample.datatype.bank.Transaction;

/**
 * Servlet implementation class BankDebitPost
 */
public class BankDebitPost extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // you will need to store actual items in DB and get them from there
        // here we use a 100-coin dummy item as sample
        Transaction trx = generateDummyTransaction();
        Gson gson = new Gson();
        String transactionJson = gson.toJson(trx);
        
        // Bank API uses OAuth 2 so we only needs the bearer token
        HttpSession s = request.getSession();
        String oauth2Token = (String)s.getAttribute(MobageOAuth.OAUTH2_TOKEN);
        
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        PrintWriter out = null;
        
        try {
            String endpoint = MobageOAuth.getBankEndpoint() + "/bank/debit/@app";
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            
            // send POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer "+oauth2Token);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoOutput(true);
            
            wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(transactionJson);
            wr.flush();
            
            int responseCode = connection.getResponseCode();
            if (responseCode / 100 > 3) {
                response.sendError(responseCode, "Bank.Post Error: "+connection.getResponseMessage());
                return;
            }
            
            // read the response from the server and parse it into JsonObject
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            JsonParser parser = new JsonParser();
            JsonObject jsonResult = parser.parse(rd).getAsJsonObject();
            
            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.println(jsonResult.toString());
        } catch (Exception e) {
            response.sendError(500, "Bank.Post Error: "+e.getMessage());
        } finally {
        	if (connection != null) try{connection.disconnect();}catch(Exception ignore){}
            if (out != null) try{out.close();}catch(Exception ignore){}
            if (wr != null) try{wr.close();}catch(Exception ignore){};
        }
    }
    
    private Transaction generateDummyTransaction() {
        Item item = new Item(
                "item_1", // for server Bank flow, this ID does NOT need to be specified on Mobage Developers portal 
                "Sample Item", 
                100, 
                "This is a sample item",
                "http://mobage.com/homepage-assets/images/icon.png");
        
        BillingItem billingItem = new BillingItem(item, 1);
        ArrayList<BillingItem> itemList = new ArrayList<BillingItem>();
        itemList.add(billingItem);
        
        return new Transaction(itemList, 
                "Purchase of Sample Item(s)"); // this comment will be visible in user's Mobage bank book in Community UI  
    }

}
