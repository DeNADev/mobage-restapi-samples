mobage-restapi-samples/dotnet
=============================

こちらは Mobage RESTful API を利用する ASP.NET MVC 4 (C#) のサンプルです。
Microsoft Visual Studio Express 2012 for Web で動作確認しました。

OAuth 認証と署名の部分は外部ライブラリを利用しています。
自前で OAuth を実装するコストが高いため、可能な限り既存 OAuth ライブラリを利用することを推奨します。  

NuGet によって簡単に取り込むことができます。
依存ライブラリの詳細とバージョンは Visual Studio の NuGet パッケージ管理ツールで確認して下さい。

下記手順でサンプルを利用できます。

プロジェクトの設定
------------

dotnet\MyGameServer\MyGameServer.sln　を Visual Studio で開きます。

Mobage RESTful API をご利用のためには、下記ファイル内の各項を記入して下さい。

dotnet\MyGameServer\MyGameServer\Controllers\Commons\MobageOAuth.cs

```
// Region: jp, kr, cn, tc, west
private static readonly String REGION = "jp";

// Environment: sandbox, production
private static readonly String ENVIRONMENT = "sandbox";

// only necessary for Mobage West
// NOTE: use the master app key, NOT the one with -iOS or -Android postfix
private static readonly String APPKEY = "YOUR_APP_KEY";

// consumer key and consumer secret for Sandbox environment
// NOTE: not the one under iOS or Android tabs, use the master one
private static readonly String CONSUMER_KEY_SANDBOX = "YOUR_SANDBOX_CONSUMER_KEY";
private static readonly String CONSUMER_SECRET_SANDBOX = "YOUR_SANDBOX_CONSUMER_SECRET";

// consumer key and consumer secret for Production environment
// NOTE: not the one under iOS or Android tabs, use the master one
private static readonly String CONSUMER_KEY_PRODUCTION = "YOUR_PRODUCTION_CONSUMER_KEY";
private static readonly String CONSUMER_SECRET_PRODUCTION = "YOUR_PRODUCTION_CONSUMER_SECRET";
```

OAuth 3-legged 認証
-------------------

上記設定をしてから、Visual Studio からデバッグ実行します。
なお、Internet ExplorerですとJSONの結果を表示するではなく、ダウンロードしようとするためGoogle Chromeを推奨します。

ウェブブラウザーから、/RequestTemporaryCredential を開き temporary token が取得できます。
例：http://localhost:50061/RequestTemporaryCredential

```
{"result":"success","payload":{"oauth_token":"temporary_credential:368302b3bef8e4aca89c0bc0bae5f3049c79f6fc"}}
```

JP プラットフォームの temporary token 例：
temporary_credential:368302b3bef8e4aca89c0bc0bae5f3049c79f6fc

この URL を iOS や Android アプリからアクセスし、取得した JSON をパースして token の値を抽出し、Mobage Native/Unity SDK の authorizeToken API に渡せば verifier が取得できます。

なお、すぐに動作確認したい場合など、この temporary token をブラウザーからコピーし、authorizeToken API に渡して verifier を取得することももちろんできます。

取得した verifier は下記の URL に渡します。
http://localhost:50061/RequestToken?verifier=1ebdc1d32a922d08123fba52ff9b805743680a16a2e2c0b3e2e2f0f62d6cc99d

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

http://localhost:50061/People

これだけで現在ログイン中のユーザ情報が取得できます。
追加で Mobage RESTful API リファレンスで定義されたクエリパラメータを指定することも可能です。

### People API を 2-legged で呼び出します

上記と同じブラウザーで、下記 URL にアクセスします。

http://localhost:50061/People?type=0&user_ids=38215

3-legged で呼び出した場合と似たような結果が得られますが、User ID を明示的に指定する必要があります。

PHP サンプルではほとんどの API を利用できますが、C# サンプルでは現在下記のものだけが実装されています。
- OAuth 認証
- People API
- Bank API

パラメータに関するメモ
-------------------
下記 2 種類のクエリパラメータをサーバーに渡すことができます。
1. Controller が直接に利用するパラメータ
2. Controller が Mobage RESTful API にそのまま渡すパラメータ

種類 (1) のパラメータについて、利用できるパラメータは PeopleController.cs の getUrlFragment() メソッドなどソースコードを直接に参照して下さい。

種類 (2) のパラメータについて、利用できるパラメータは Mobage RESTful API のドキュメントを参照して下さい。

以上です。
