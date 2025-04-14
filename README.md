
# 🍜 TokyoMap - 東京グルメマップ ポートフォリオ

Spring Boot + React を使用した東京のグルメ検索サービスのバックエンド＆フロントエンドプロジェクトです。  
現在、JWT認証、メール認証、ロールベースアクセス制御までを実装したベースバージョン(v1)が完成しています。

---

## ✅ 技術スタック

### 🖥️ バックエンド

- Spring Boot 3.x / Spring Security
- Spring Data JPA / MySQL
- JWT (jjwt)
- メール認証（Gmail SMTP）
- Swagger（springdoc-openapi）
- QueryDSL（🔧 設定済、今後使用予定）

### 💻 フロントエンド

- React (Viteベース)
- Axios による API通信
- Swagger / Postman によるAPIテスト確認済み

---

## ✨ 機能概要（v1 完成済）

| 機能 | 内容 |
|------|------|
| ユーザー登録 | メール、パスワード、ニックネームによる登録 |
| メール認証 | 認証コード送信、検証、認証フラグ更新 |
| ログイン | JWTアクセストークンの発行（accessToken + expiresAt） |
| ロール分岐 | 一般ユーザー / 管理者 専用APIを分離 |
| 認証APIテスト | `/api/auth/test` `/api/auth/admin/only` で認証確認可能 |

---

## 📄 APIドキュメント

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Bearer TokenによるJWT認証対応済

---

## 🧪 Postmanテストコレクション

🔗 [`tokyomap-api-testset-v1.json`](./postman/tokyomap-api-testset-v1.json)

- ユーザー登録 → メール認証 → ログイン → 認証API まで一連の流れをPostmanで自動テスト可能
- accessTokenは環境変数で自動保存され、他のAPIに自動適用されます

---

## 📁 プロジェクト構成

```
tokyo-map-portfolio/
├── backend/              # Spring Boot バックエンド
├── frontend/             # React フロントエンド
├── postman/              # Postman テストコレクション
│   └── tokyomap-api-testset-v1.json
└── README.md
```

---

## 🛠️ 今後の開発予定

- ✅ お気に入り機能（ログインユーザー限定）
- ✅ レビュー機能（飲食店ごとに評価とコメント）
- ✅ 飲食店検索（Google Maps API連携）
- ✅ 管理者によるユーザー/レビュー/飲食店管理
- ✅ S3を使った画像アップロード
- ✅ WebSocketによる通知機能

---

## 🗓️ 進捗ステータス

| 日付 | 進捗内容 |
|------|----------|
| 4/6〜4/14 | 認証全体（登録、メール、JWT、ロール）構築完了 |
| 4/15〜 | お気に入り、レビュー、検索機能の開発予定 |
