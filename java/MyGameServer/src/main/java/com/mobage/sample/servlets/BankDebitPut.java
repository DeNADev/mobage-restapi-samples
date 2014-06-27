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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobage.sample.commons.MobageOAuth;
import com.mobage.sample.datatype.bank.Transaction;

/**
 * Servlet implementation class BankDebitPut
 */
public class BankDebitPut extends HttpServlet {
    private static final long serialVersionUID = 1L;

    class TransactionUpdateException extends Exception {
        TransactionUpdateException(String message) {
            super(message);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getRequestURL().toString();
        if (url.endsWith("/bank_debit_put")) {
            bankDebitPut(request, response);
        } else if (url.endsWith("/bank_debit_commit_transaction")) {
            bankDebitCommitTransaction(request, response);
        }
    }

    private void bankDebitPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Bank API uses OAuth 2 so we only needs the bearer token
        HttpSession s = request.getSession();
        String oauth2Token = (String)s.getAttribute(MobageOAuth.OAUTH2_TOKEN);

        String transactionId = request.getParameter("transaction_id");
        if (transactionId == null || transactionId.length() == 0) {
            response.sendError(400, "Missing parameter");
            return;
        }

        String state = request.getParameter("state");
        if (state == null || state.length() == 0) {
            response.sendError(400, "Missing parameter");
            return;
        }

        PrintWriter out = null;
        try {
            JsonObject jsonResult = updateTransaction(transactionId, state, oauth2Token);

            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.println(jsonResult.toString());
        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        } finally {
            if (out != null) try{out.close();}catch(Exception ignore){}
        }
    }

    private void bankDebitCommitTransaction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Bank API uses OAuth 2 so we only needs the bearer token
        HttpSession s = request.getSession();
        String oauth2Token = (String)s.getAttribute(MobageOAuth.OAUTH2_TOKEN);

        String transactionId = request.getParameter("transaction_id");
        if (transactionId == null || transactionId.length() == 0) {
            response.sendError(400, "Missing parameter");
            return;
        }

        PrintWriter out = null;
        try {
            JsonObject jsonResult;

            jsonResult = updateTransaction(transactionId, Transaction.OPEN, oauth2Token);

            // TODO: Successfully opened transaction.
            // Give the purchased item to the user here, step 14 in following reference
            // https://docs.mobage.com/display/JPSA/How_to_use_the_Currency_Commerce_Services_Native

            jsonResult = updateTransaction(transactionId, Transaction.CLOSED, oauth2Token);

            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.println(jsonResult.toString());
        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        } finally {
            if (out != null) try{out.close();}catch(Exception ignore){}
        }
    }

    private JsonObject updateTransaction(String transactionId, String state, String oauth2Token) throws TransactionUpdateException {
        JsonObject stateJson = new JsonObject();
        stateJson.addProperty("state", state);

        HttpURLConnection connection = null;
        DataOutputStream wr = null;

        try {
            String endpoint = MobageOAuth.getBankEndpoint() + "/bank/debit/@app/" + transactionId + "?fields=state";
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();

            // send POST
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Bearer "+oauth2Token);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoOutput(true);

            wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(stateJson.toString());
            wr.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode / 100 > 3) {
                throw new TransactionUpdateException(connection.getResponseMessage());
            }

            // read the response from the server and parse it into JsonObject
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            JsonParser parser = new JsonParser();

            return parser.parse(rd).getAsJsonObject();
        } catch (Exception e) {
            throw new TransactionUpdateException("Bank.Put Error: "+e.getMessage());
        } finally {
            if (connection != null) try{connection.disconnect();}catch(Exception ignore){}
            if (wr != null) try{wr.close();}catch(Exception ignore){};
        }
    }

}
