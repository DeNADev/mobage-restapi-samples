mobage-restapi-samples/nodejs
======================

こちらは Mobage RESTful API を利用するNode.jsのサンプルです。

含まれている内容は下記のようです。

1. OAuth 3-legged 認証
2. RESTful APIの確認用の簡単サンプル


※ OAuth 認証の部分は下記ライブラリを利用しています。
* ライブラリ名 : OAuth Core 1.0
* プロジェクトページ : http://oauth.net/core/1.0a/
* ライセンス : http://oauth.net/license/core/1.0/



下記手順でサンプルを利用できます。

OAuth 3-legged 認証
-------------------

1. node.js の実行は、npm install してから node app.jsを実行します。
2. config.json を編集し、YOUR_SANDBOX_CONSUMER_KEY, YOUR_SANDBOX_CONSUMER_SECRETにアプリ専用の値に設定します。
　＊本番環境の場合はYOUR_PRODUCTION_CONSUMER_KEY, YOUR_PRODUCTION_CONSUMER_SECRETを設定します。
3. ウェブブラウザーから、/nodejs/request_temporary_credential をアクセスします。
例：http://localhost:3000/nodejs/request_temporary_credential
4. Temporary Token が表示されます。
5. (URLエンコードされていない) Temporary Token をコピーします。
6. コピーした Temporary Token をクライアントの authorizeToken に渡し、verifier を取得します。
7. Verifier の値をコピーし、ブラウザーで /nodejs/request_token に下記のように渡します。
例：http://localhost:3000/nodejs/request_token?verifier=PASTE_VERIFIER_STRING_HERE
8. これで認証が完了するはずです。ほかの RESTful API が利用できます。

＊もちろん、3-8 は手動ではなく iOS や Android といったクライアントから HTTP でアクセスしても動作します。



簡単な People API を呼び出してみる
-------------------------------

### People API を 3-legged で呼び出します

上記ブラウザーで、http://localhost:3000/nodejs/people_get にアクセスします。

これだけで現在ログイン中のユーザのNickNameが取得できます。

Mobage RESTful APIを利用してみる
-------------------
下記のMobage RESTful APIドキュメントを参考し、app.jsにWeb Methodを追加し結果を確認します。
https://docs.mobage.com/display/JPSA/REST_API_Reference_JP

