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

var express = require('express');
var OAuth = require('oauth');


// Mode for sandbox
var env = process.env.NODE_ENV || 'sandbox';
// Mode for Production
//var env = process.env.NODE_ENV || 'production';

var config = require('./config')[env];

// initialize oauth client
var oauth = new OAuth.OAuth(
    config.mobage.authApiBaseUri + '/request_temporary_credential', // temporary credential request endpoint
    config.mobage.authApiBaseUri + '/request_token',                // token request endpoint
    config.mobage.clientKey,    // consumer key
    config.mobage.clientSecret, // consumer secret
    '1.0A',     // oauth_version
    'oob',      // oauth_callback
    'HMAC-SHA1' // oauth_signature_method
);

var app = express();

app.use(express.logger());
app.use(express.cookieParser());
app.use(express.urlencoded());

// Memory store (Session management)
app.use(express.session({ secret: 'SET-YOUR-SECRET-HERE' }));

app.get('/nodejs/request_temporary_credential', function (req, res, next) {
    oauth.getOAuthRequestToken(function (err, tempToken, tempTokenSecret, results) {
        if (err) {
            return next(err);
        }

        req.session.mobageTempToken = tempToken;
        req.session.mobageTempTokenSecret = tempTokenSecret;
        
        res.send({
            oauth_token: tempToken
        });
    });
});

app.get('/nodejs/request_token', function (req, res, next) {
    var tempToken = req.session.mobageTempToken;
    var tempTokenSecret = req.session.mobageTempTokenSecret;

    if (!tempToken || !tempTokenSecret) {
        return res.send(401);
    }

    var verifier = req.param('verifier');

    if (!verifier) {
        return res.send(401);
    }

    oauth.getOAuthAccessToken(
        tempToken,
        tempTokenSecret,
        verifier,
        function (err, accessToken, accessTokenSecret, results) {
            if (err) {
                return next(err);
            }
            // To save AccessToken to Session
            req.session.mobageAccessToken = accessToken;
            req.session.mobageAccessTokenSecret = accessTokenSecret;
            req.session.mobageOAuth2Token = results.oauth2_token;

            res.send({
                oauth_token: accessToken
            });
        }
    );
});

app.get('/nodejs/people_get', function (req, res, next) {
        config.mobage.socialApiBaseUri + '/people/@me/@self',
        req.session.mobageAccessToken,
        req.session.mobageAccessTokenSecret,
        function (err, data, response) {
            if (err) {
                return next(err);
            }

            req.session.mobageCurrentUser = data;

            res.send({
                id: data.id,
                nickname: data.nickname
                // some other game specific data
            });
        }
});

app.listen(process.env.PORT || 3000);
