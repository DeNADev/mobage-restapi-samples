mobage-restapi-samples/php
======================

こちらは Mobage RESTful API を利用するPHPのサンプルです。

各ファイル名は、API名_HTTPメソッド.php になっています。
いくつこのルールに従わないファイルもありますが、ファイル名から自明になっています。
また、ファイル名に対象プラットフォームが入っている場合もあります。
たとえば、remote_notification_post_jp_kr_cn_tc.php がその例です。
この場合、プラットフォーム間で仕様が異なりますのでご注意下さい。

※ OAuth 認証の部分は下記ライブラリを利用しています。
ライセンスに関しては MyGameServer/External/LICENSE.txt をご参照下さい。
自前で OAuth を実装するコストが高いため、可能な限り既存 OAuth ライブラリを利用することを推奨します。  
プロジェクトページリンク > http://oauth.googlecode.com/  
Ouath.phpリンク > http://oauth.googlecode.com/svn/code/php/

※ 現在のところ、PHP のサンプルだけを提供しています。
ほかの言語のサンプルが必要な場合、ご相談下さい。

下記手順でサンプルを利用できます。

OAuth 3-legged 認証
-------------------

1. 全てのファイルを PHP を実行できるウェブサーバーの下に置きます。
2. config.php を編集し、$region, $environment, $appKey, $consumerKey と $consumerSecret にアプリ専用の値に設定します。
3. ウェブブラウザーから、request_temporary_credential.php をアクセスします。
例：http://localhost/php/request_temporary_credential.php
4. Temporary Token が表示されます。
5. (URLエンコードされていない) Temporary Token をコピーします。
6. コピーした Temporary Token をクライアントの authorizeToken に渡し、verifier を取得します。
7. Verifier の値をコピーし、ブラウザーで request_token.php に下記のように渡します。
例：http://localhost/php/request_token.php?verifier=PASTE_VERIFIER_STRING_HERE
8. これで認証が完了するはずです。ほかの RESTful API が利用できます。

試しに People API を呼び出してみる
-------------------------------

次に、people_get.php を実際に動かしてみましょう。

people_get.php で 2-legged と 3-legged の両方を利用できます。
2-legged では、ゲームとして API を呼び出しますが、3-legged ではユーザとして呼び出します。

デフォルトでは、people_get.php は 3-legged を利用します。

### People API を 3-legged で呼び出します

上記ブラウザーで、http://localhost/php/people_get.php にアクセスします。

これだけで現在ログイン中のユーザ情報が取得できます。
追加で Mobage RESTful API リファレンスで定義されたクエリパラメータを指定することも可能です。

### People API を 2-legged で呼び出します

上記ブラウザーで、http://localhost/php/people_get.php?type=0&user_ids=38215 にアクセスします。

3-legged で呼び出した場合と似たような結果が得られますが、User ID を明示的に指定する必要があります。


パラメータに関するメモ
-------------------
下記 2 種類のクエリパラメータを PHP スクリプトに渡すことができます。
1. スクリプトが直接に利用するパラメータ
2. スクリプトが Mobage RESTful API にそのまま渡すパラメータ

種類 (1) のパラメータについて、利用できるパラメータは PHP のソースコードを参照して下さい。
スクリプトの先頭部分に定義されます。
people_get.php を例にしますと、"type"、"user_ids"、"group_id"、"person_ids" がこれにあたります。

種類 (2) のパラメータについて、利用できるパラメータは Mobage RESTful API のドキュメントを参照して下さい。

以上です。
