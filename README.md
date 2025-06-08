# FHS
farmily-harmony-system
---

````markdown
# 🛡️ 認證 API 文件 (`/api/auth`)

提供使用者註冊、登入、登出功能。回應皆為 JSON 格式。

---

## 📌 註冊使用者

- **Endpoint:** `POST /api/auth/register`
- **用途：** 註冊新帳號

### ✅ Request Body
```json
{
  "username": "user123",
  "password": "securePassword",
  "email": "user@example.com",
  "phone": "0912345678"
}
````

| 欄位       | 類型     | 說明    | 必填 |
| -------- | ------ | ----- | -- |
| username | string | 使用者名稱 | ✅  |
| password | string | 密碼    | ✅  |
| email    | string | 電子郵件  | ✅  |
| phone    | string | 手機號碼  | ✅  |

### ✅ Response Body

```json
{
  "username": "user123",
  "email": "user@example.com",
  "status": 200,
  "message": "Register success",
  "timestamp": "2025-06-08T15:34:20.123"
}
```

| 欄位        | 類型     | 說明            |
| --------- | ------ | ------------- |
| username  | string | 註冊成功的使用者名稱    |
| email     | string | 使用者信箱         |
| status    | int    | HTTP 狀態碼（200） |
| message   | string | 操作訊息          |
| timestamp | string | 回應時間戳記        |

---

## 📌 使用者登入

* **Endpoint:** `POST /api/auth/login`
* **用途：** 登入並取得 JWT

### ✅ Request Body

```json
{
  "username": "user123",
  "password": "securePassword"
}
```

| 欄位       | 類型     | 說明    | 必填 |
| -------- | ------ | ----- | -- |
| username | string | 使用者名稱 | ✅  |
| password | string | 密碼    | ✅  |

### ✅ Response Body

```json
{
  "username": "user123",
  "token": "jwt-token-string",
  "status": 200,
  "message": "Login success",
  "timestamp": "2025-06-08T15:40:01.456"
}
```

| 欄位        | 類型     | 說明             |
| --------- | ------ | -------------- |
| username  | string | 登入成功的使用者名稱     |
| token     | string | JWT Token（需保存） |
| status    | int    | HTTP 狀態碼（200）  |
| message   | string | 操作訊息           |
| timestamp | string | 回應時間戳記         |

---

## 📌 使用者登出

* **Endpoint:** `POST /api/auth/logout`
* **用途：** 登出（JWT 模式，前端清除 Token 即可）

### ✅ Request Header

```
Authorization: Bearer <your_token>
```

### ⬅️ Response

* **HTTP Status:** `204 No Content`
* 無內容，表示成功登出

---

## 📎 備註

* 請在登入成功後保存 JWT Token，並於所有需驗證之 API 請求中加上：

```
Authorization: Bearer <your_token>
```

* 時間格式為 ISO 8601，例如：`2025-06-08T15:40:01.456`

---

---

```markdown
# 👤 使用者帳戶 API 文件 (`/api/user`)

提供登入使用者的資料存取與帳號刪除功能。JWT 驗證必要，請在 `Authorization` Header 中附上：

```

Authorization: Bearer \<your\_token>

```

---

## 📌 取得使用者個人資料

- **Endpoint:** `GET /api/user/profile`
- **用途：** 取得目前登入使用者的基本資訊（測試用途）

### ✅ Request Header
```

Authorization: Bearer \<your\_token>

````

### ✅ Response Body
```json
"Hello, user123! This is your profile."
````

| 回傳格式    | 類型     | 說明         |
| ------- | ------ | ---------- |
| message | string | 簡單的個人化歡迎訊息 |

> 📘 備註：未來可以擴充為回傳完整使用者資料物件（如 email、phone 等）

---

## 🗑️ 刪除目前登入使用者帳號

* **Endpoint:** `DELETE /api/user/delete`
* **用途：** 刪除目前登入的帳號

### ✅ Request Header

```
Authorization: Bearer <your_token>
```

### ✅ Response Body

```json
"帳號已刪除"
```

| 回傳格式    | 類型     | 說明   |
| ------- | ------ | ---- |
| message | string | 成功訊息 |

---

## ⚠️ 錯誤處理（通用）

若 Token 錯誤、未授權或已過期，將收到如下回應：

```json
{
  "timestamp": "2025-06-08T16:00:00.000",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "path": "/api/user/profile"
}
```

---
