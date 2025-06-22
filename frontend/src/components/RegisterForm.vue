<template>
    <div class="register-form">
      <h2>註冊新帳號</h2>
      <form @submit.prevent="handleRegister">
        <div>
          <label for="username">帳號：</label>
          <input id="username" v-model="username" type="text" required />
        </div>
        <div>
          <label for="password">密碼：</label>
          <input id="password" v-model="password" type="password" required />
        </div>
        <div>
          <label for="email">Email：</label>
          <input id="email" v-model="email" type="email" required />
        </div>
        <div>
          <label for="phone">電話：</label>
          <input id="phone" v-model="phone" type="tel" required />
        </div>
        <button type="submit">註冊</button>
      </form>
      <p v-if="message" :style="{ color: messageColor }">{{ message }}</p>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import axios from 'axios'
  
  const username = ref('')
  const password = ref('')
  const email = ref('')
  const phone = ref('')
  const message = ref('')
  const messageColor = ref('black')
  
  const handleRegister = async () => {
    message.value = ''
    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', {
        username: username.value,
        password: password.value,
        email: email.value,
        phone: phone.value
      })
      console.log('註冊成功:', response.data)
      message.value = '註冊成功！請登入。'
      messageColor.value = 'green'
      
      // 清空欄位（可選）
      username.value = ''
      password.value = ''
      email.value = ''
      phone.value = ''
    } catch (error) {
      console.error(error)
      message.value = error.response?.data?.message || '註冊失敗，請稍後再試'
      messageColor.value = 'red'
    }
  }
  </script>
  
  <style scoped>
  .register-form {
    max-width: 400px;
    margin: auto;
  }
  .register-form div {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  }

  .register-form label {
  width: 80px; /* 統一 label 寬度 */
  font-weight: bold;
  }

  .register-form input {
  flex: 1; /* 輸入框撐滿剩餘空間 */
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  }
  </style>
  