# FHS
farmily-harmony-system
---
# Farmily FHS API 文件

## 一、用到的技術與觀念

- **Spring Boot**：快速建構 REST API
- **Spring Security + JWT**：無狀態的使用者認證機制
- **Lombok**：簡化 Java 物件程式碼
- **BCryptPasswordEncoder**：密碼加密與驗證
- **ResponseEntity**：HTTP 狀態碼與回傳內容控制
- **Exception Handling**：例外處理，確保 API 穩定性
- **Authentication 注入**：取得目前登入使用者資訊
- **RESTful API 設計**：依照標準 HTTP 動詞進行 CRUD 操作

---

## 二、Auth 功能

### 1. 註冊帳號 (Register)

- **路徑**：`POST /api/auth/register`
- **說明**：用戶註冊新帳號
- **Request Body**：

| 欄位名稱 | 型態   | 必填 | 說明           |
| -------- | ------ | ---- | -------------- |
| username | String | 是   | 使用者帳號     |
| password | String | 是   | 使用者密碼     |
| email    | String | 是   | 電子郵件       |
| phone    | String | 是   | 電話號碼       |

- **Response Body**：

| 欄位名稱 | 型態   | 說明                 |
| -------- | ------ | -------------------- |
| username | String | 註冊成功的使用者帳號 |
| status   | int    | HTTP 狀態碼 200      |
| message  | String | 回傳訊息             |
| timestamp| String | 伺服器回應時間       |

- **HTTP 狀態碼**：

| 狀態碼 | 意義           |
| ------ | -------------- |
| 200    | 註冊成功       |
| 400    | 資料格式錯誤   |
| 409    | 帳號已存在     |

---

### 2. 登入 (Login)

- **路徑**：`POST /api/auth/login`
- **說明**：用戶登入取得 JWT
- **Request Body**：

| 欄位名稱 | 型態   | 必填 | 說明         |
| -------- | ------ | ---- | ------------ |
| username | String | 是   | 使用者帳號   |
| password | String | 是   | 使用者密碼   |

- **Response Body**：

| 欄位名稱 | 型態   | 說明           |
| -------- | ------ | -------------- |
| username | String | 登入的使用者帳號 |
| token    | String | JWT 權杖       |
| status   | int    | HTTP 狀態碼 200|
| message  | String | 回傳訊息       |
| timestamp| String | 伺服器回應時間 |

- **HTTP 狀態碼**：

| 狀態碼 | 意義         |
| ------ | ------------ |
| 200    | 登入成功     |
| 401    | 帳號或密碼錯誤 |

---

### 3. 登出 (Logout)

- **路徑**：`POST /api/auth/logout`
- **說明**：JWT 模式下，前端丟棄 token 即可
- **Headers**：

| 欄位         | 說明       |
| ------------ | ---------- |
| Authorization| Bearer token |

- **Response**：

| 狀態碼 | 意義          |
| ------ | ------------- |
| 204    | 登出成功(無內容) |

---

## 三、User 功能

### 1. 取得個人資料 (Get Profile)

- **路徑**：`GET /api/user/profile`
- **說明**：需登入，取得目前使用者資訊
- **Headers**：

| 欄位         | 說明       |
| ------------ | ---------- |
| Authorization| Bearer token |

- **Response Body**：



