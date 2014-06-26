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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oauth.signpost.OAuthConsumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobage.sample.commons.MobageOAuth;
import com.mobage.sample.commons.MobageOAuth.AuthType;

/**
 * Servlet implementation class PeopleGet
 */

public class PeopleGet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 0: consumer only, 2-legged, Trusted Model
        // 1: 3-legged, Proxy Model
        AuthType authType = AuthType.THREE_LEGGED;
        String typeParam = request.getParameter("type");
        if (typeParam != null) {
            int type = Integer.parseInt(typeParam);
            if (type == 0) {
                authType = AuthType.CONSUMER_ONLY;
            }
        }
        
        OAuthConsumer consumer = MobageOAuth.getOAuthConsumer();
        if (authType == AuthType.THREE_LEGGED) {
            // retrieves token and secret stored in session
            // you should store these in DB for scalability in production
            HttpSession s = request.getSession();
            String accessToken = (String)s.getAttribute(MobageOAuth.OAUTH_TOKEN);
            String accessTokenSecret = (String)s.getAttribute(MobageOAuth.OAUTH_TOKEN_SECRET);
            consumer.setTokenWithSecret(accessToken, accessTokenSecret);
        }
        
        HttpURLConnection connection = null;
        PrintWriter out = null;
        
        try {
            String endpoint = MobageOAuth.getSocialEndpoint() + getUrlFragment(request);
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();

            // sign the request
            consumer.sign(connection);
            
            // send the request
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            if (responseCode / 100 > 3) {
                response.sendError(responseCode, "People.Get Error: "+connection.getResponseMessage());
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
            response.sendError(500, "People.Get Error: "+e.getMessage());
        } finally {
            if (connection != null) try{connection.disconnect();}catch(Exception ignore){}
            if (out != null) try{out.close();}catch(Exception ignore){}
        }
    }
    
    private String getUrlFragment(HttpServletRequest request) {
        String urlFragment = "/people";
        
        // check whether optional user ids is set
        String userIds = request.getParameter("user_ids");
        if (userIds != null && userIds.length() > 0) {
            urlFragment += "/"+userIds;
        } else {
            urlFragment += "/@me";
        }

        String groupId = request.getParameter("group_id");
        if (groupId != null && groupId.length() > 0) {
            urlFragment += "/"+groupId;

            String personIds = request.getParameter("person_ids");
            if (personIds != null && personIds.length() > 0) {
                urlFragment += "/"+personIds;
            }
        } else {
            urlFragment += "/@self";
        }

        // for other query params
        boolean isFirstElem = true;
        for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements() ;) {
            String paramName = e.nextElement();
            if(!"type".equals(paramName) &&
               !"user_ids".equals(paramName) &&
               !"group_id".equals(paramName) &&
               !"person_ids".equals(paramName)) {
                if (isFirstElem) {
                    urlFragment += "?";
                    isFirstElem = false;
                } else {
                    urlFragment += "&";
                }
                urlFragment += paramName + "=" + request.getParameter(paramName);
            }
        }
        
        return urlFragment;
    }

}
