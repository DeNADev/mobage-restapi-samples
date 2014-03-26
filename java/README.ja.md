mobage-restapi-samples/java
===========================

こちらは Mobage RESTful API を利用する Java Servlet のサンプルです。

OAuth 認証と署名の部分は外部ライブラリを利用しています。
自前で OAuth を実装するコストが高いため、可能な限り既存 OAuth ライブラリを利用することを推奨します。  

Maven (http://maven.apache.org/) によって簡単に取り込むことができます。
依存ライブラリの詳細とバージョンは java/MyGameServer/pom.xml を確認して下さい。

下記手順でサンプルを利用できます。

Eclipse プロジェクトの作成と読み込み
--------------------------------

Eclipse で開発を行う前提で書きますが、そのほかの開発環境をご利用の場合適宜コマンドを置き換えて下さい。

  $ cd java/MyGameServer
  $ mvn eclipse:eclipse -Dwtpversion=2.0

上記コマンドを実行したあと、Eclipse で MyGameServer をインポートできます。

また、mvn コマンドで war まで作成できます。

  $ mvn package

war は java/MyGameServer/target に作成されます。

プロジェクトの設定
----------------

Mobage RESTful API をご利用のためには、下記ファイル内の各項を記入して下さい。

java/MyGameServer/src/main/java/com/mobage/sample/commons/MobageOAuth.java

```
// Region: jp, kr, cn, tc, west
private static final String REGION = "jp";

// Environment: sandbox, production
private static final String ENVIRONMENT = "sandbox";

// only necessary for Mobage West
// NOTE: use the master app key, not the one with -iOS or -Android postfix
private static final String APPKEY = "YOUR_APP_KEY";

// consumer key and consumer secret for Sandbox environment
// NOTE: not the one under iOS or Android tabs, use the master one
private static final String CONSUMER_KEY_SANDBOX    = "YOUR_SANDBOX_CONSUMER_KEY";
private static final String CONSUMER_SECRET_SANDBOX = "YOUR_SANDBOX_CONSUMER_SECRET";

// consumer key and consumer secret for Production environment
// NOTE: not the one under iOS or Android tabs, use the master one
private static final String CONSUMER_KEY_PRODUCTION    = "YOUR_PRODUCTION_CONSUMER_KEY";
private static final String CONSUMER_SECRET_PRODUCTION = "YOUR_PRODUCTION_CONSUMER_SECRET";
```

OAuth 3-legged 認証
-------------------

上記設定をしてから、Tomcat などのサーブレットコンテナに配置し実行します。

ウェブブラウザーから、/MyGameServer/request_temporary_credential から temporary token が取得できます。
例：http://localhost:8080/MyGameServer/request_temporary_credential

```
{"result":"success","payload":{"token":"temporary_credential:0b2ec2f1ef2c20e68cd3cba23cada1b46d5bea54"}}
```

JP プラットフォームの temporary token 例：
temporary_credential:0b2ec2f1ef2c20e68cd3cba23cada1b46d5bea54

この URL を iOS や Android アプリからアクセスし、取得した JSON をパースして token の値を抽出し、Mobage Native/Unity SDK の authorizeToken API に渡せば verifier が取得できます。

なお、すぐに動作確認したい場合など、この temporary token をブラウザーからコピーし、authorizeToken API に渡して verifier を取得することももちろんできます。

取得した verifier は下記の URL に渡します。
http://localhost:8080/MyGameServer/request_token?verifier=3516550505a6bca94a9e818a84b4f306fcb7ba9dd312f9247d93c7f42d94afb0

下記のような結果が帰って来たら OK です。
```
{"result":"success","payload":{}}
```

これで認証が完了するはずです。ほかの RESTful API が利用できます。

試しに People API を呼び出してみる
-------------------------------

次に、People API を呼び出してみましょう。

このサンプルで 2-legged と 3-legged の両方を利用できます。

2-legged では、ゲームとして API を呼び出しますが、3-legged ではユーザとして呼び出します。

デフォルトでは 3-legged を利用します。

### People API を 3-legged で呼び出します

上記と同じブラウザーで、下記 URL にアクセスします。

http://localhost:8080/MyGameServer/people_get

これだけで現在ログイン中のユーザ情報が取得できます。
追加で Mobage RESTful API リファレンスで定義されたクエリパラメータを指定することも可能です。

### People API を 2-legged で呼び出します

上記と同じブラウザーで、下記 URL にアクセスします。

http://localhost:8080/MyGameServer/people_get?type=0&user_ids=38215

3-legged で呼び出した場合と似たような結果が得られますが、User ID を明示的に指定する必要があります。

PHP サンプルではほとんどの API を利用できますが、Java サンプルでは下記のものに限ります。
- OAuth 認証
- People API
- Bank API

パラメータに関するメモ
-------------------
下記 2 種類のクエリパラメータをサーバーに渡すことができます。
1. サーブレットが直接に利用するパラメータ
2. サーブレットが Mobage RESTful API にそのまま渡すパラメータ

種類 (1) のパラメータについて、利用できるパラメータは PeopleGet.java の getUrlFragment() メソッドなどソースコードを直接に参照して下さい。

種類 (2) のパラメータについて、利用できるパラメータは Mobage RESTful API のドキュメントを参照して下さい。

以上です。
