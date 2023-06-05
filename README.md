# HowAreYouService

How Are You サービス
2021年度教育コンテンツ

詳細は，wikiを参照のこと
- https://cs27.org/wiki/lab/?%B6%B5%B0%E9%A5%B3%A5%F3%A5%C6%A5%F3%A5%C4/HowAreYou%A5%B5%A1%BC%A5%D3%A5%B9

- wsappにデプロイしたサービスは [こちら](https://wsapp.cs.kobe-u.ac.jp/HowAreYou/) 。

## 実行・デプロイ方法

**まず、HowAreYouディレクトリに移動してから作業してください**

### 本番環境(仮想tomcat)

実行サーバーは仮想で、データベースは本番環境。

1. application.propertiesで`spring.profiles.active=prod`に設定。
1. コマンドラインで`./gradlew bootRun`を実行する。
1. http://localhost:8080/ で動かす。 

### 本番環境(ローカルのtomcat)

実行サーバー(ローカルのtomcat)もデータベースも本番環境。

1. application.propertiesで`spring.profiles.active=prod`に設定。
1. コマンドラインで`.\gradlew war`を実行し、warファイルを生成する。
1. build/libs内のwarファイルをC:\tomcat\webappsに配置する。
1. tomcatを起動する。(デスクトップのstartup.batショートカット等)
1. http://localhost:8080/HowAreYou/ で動かす。

### 開発環境

実行サーバーもデータベースも開発環境。

1. application.propertiesで`spring.profiles.active=dev`に設定。
1. ローカル環境等でMySQLのDBを作成する。手順は [SpringBoot/JPA](https://cs27.org/wiki/lab/?SpringBoot/JPA)、[中田/Techメモ/MySQL](https://cs27.org/wiki/lab/?%C3%E6%C5%C4/Tech%A5%E1%A5%E2#j81ae5e9) 等を参照。
1. 作成したDBの設定をapplication-dev.propertiesを編集して設定する。`spring.datasource`の`url`内のhowareyouを作成したDB名に設定し、`username`と`password`をDBの設定したものにする。
1. コマンドラインで`./gradlew bootRun`を実行する。
1. http://localhost:8080/ で動かす。

### テスト

コマンドラインで`./gradlew test`を実行する。

## 本番環境の情報

### デプロイしたサービス

https://wsapp.cs.kobe-u.ac.jp/HowAreYou/

### 本番環境データベース

- 場所 : 192.168.0.21
- データベース名: howareyou
- ユーザ名: howareyou_admin
- パスワード: howareyou

## 環境ごとのプロパティの切り替え

- 本番環境
  - src/main/resources/application.properties内で`spring.profiles.active=prod`と設定。
  - 設定内容はapplication-prod.propertiesに記述。
- 開発環境
  - src/main/resources/application.properties内で`spring.profiles.active=dev`と設定。
  - 設定内容はapplication-dev.propertiesに記述。
- テスト環境
  - src/test/resources/application.propertiesに記述。

詳細はWikiの [中田/Techメモ/SpringBoot](https://cs27.org/wiki/lab/?%C3%E6%C5%C4/Tech%A5%E1%A5%E2/SpringBoot#e065e025) を参照。
