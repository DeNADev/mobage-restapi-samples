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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.mobage.sample.commons.MobageOAuth;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.http.HttpParameters;

/**
 * Servlet implementation class UserAuth
 */

public class UserAuth extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private OAuthProvider provider = null;
    private OAuthConsumer consumer = null;
    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        provider = MobageOAuth.getOAuthProvider();
        consumer = MobageOAuth.getOAuthConsumer();
        
        String url = request.getRequestURL().toString();
        if (url.endsWith("/request_temporary_credential")) {
            requestTemporaryCredential(request, response);
        } else if (url.endsWith("/request_token")) {
            requestToken(request, response);
        }
    }

    private void requestTemporaryCredential(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	PrintWriter out = null;
    	
        try {
            // retrieves temporary token
            provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
            
            String temporaryToken = consumer.getToken();
            String temporaryTokenSecret = consumer.getTokenSecret();
            
            if ((temporaryToken == null || temporaryToken.length() == 0) ||
                (temporaryTokenSecret == null || temporaryTokenSecret.length() == 0)) {
                response.sendError(500, "Failed to get temporary token: unknown error");
                return;
            }
            
            // stores token and secret in session
            // you should store these in DB for scalability in production
            HttpSession s = request.getSession();
            s.setAttribute(MobageOAuth.OAUTH_TOKEN, temporaryToken);
            s.setAttribute(MobageOAuth.OAUTH_TOKEN_SECRET, temporaryTokenSecret);
            
            // prepares result
            JsonObject jsonResult = new JsonObject();
            jsonResult.addProperty("oauth_token", temporaryToken);
            
            // returns only the temporary token to the client
            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.println(jsonResult.toString());
        } catch (Exception e) {
            response.sendError(500, "Failed to get temporary token: "+e.getMessage());
        } finally {
        	if (out != null) try{out.close();}catch(Exception ignore){}
        }
    }

    private void requestToken(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String verifier = request.getParameter("verifier");
        if (verifier == null || verifier.length() == 0) {
            response.sendError(400, "Missing parameters");
            return;
        }
        
        // retrieves token and secret stored in session
        // you should store these in DB for scalability in production
        HttpSession s = request.getSession();
        String temporaryToken = (String)s.getAttribute(MobageOAuth.OAUTH_TOKEN);
        String temporaryTokenSecret = (String)s.getAttribute(MobageOAuth.OAUTH_TOKEN_SECRET);
        
        consumer.setTokenWithSecret(temporaryToken, temporaryTokenSecret);
        
        PrintWriter out = null;
        
        try {
            provider.retrieveAccessToken(consumer, verifier);
            
            String accessToken = consumer.getToken();
            String accessTokenSecret = consumer.getTokenSecret();
            
            HttpParameters responseParams = provider.getResponseParameters();
            String oauth2Token = responseParams.getFirst("oauth2_token");
            
            if ((accessToken == null || accessToken.length() == 0) ||
                (accessTokenSecret == null || accessTokenSecret.length() == 0) ||
                (oauth2Token == null || oauth2Token.length() == 0)) {
                response.sendError(500, "Failed to get access token: unknown error");
                return;
            }
            
            // stores token and secret in session
            // you should store these in DB for scalability in production
            s.setAttribute(MobageOAuth.OAUTH_TOKEN, accessToken);
            s.setAttribute(MobageOAuth.OAUTH_TOKEN_SECRET, accessTokenSecret);
            s.setAttribute(MobageOAuth.OAUTH2_TOKEN, oauth2Token);
            
            // prepares result
            // client does not need to know about access token
            // we pass it here just for easy testing
            // !!! Never pass secret or oauth2_token back to client !!!
            // you can pass back session ID instead as a key to retrieve the access token later
            JsonObject jsonResult = new JsonObject();
            jsonResult.addProperty("oauth_token", accessToken);
            
            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.println(jsonResult.toString());
        } catch (Exception e) {
            response.sendError(500, "Failed to get access token: "+e.getMessage());
        } finally {
        	if (out != null) try{out.close();}catch(Exception ignore){}
        }
    }

}
