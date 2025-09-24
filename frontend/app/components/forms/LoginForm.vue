<template>

    <form class="login-form" @submit.prevent="login">

        <h2>LOGIN</h2>

        <EmailInput v-model:email="loginData.email" />

        <PasswordInput v-model:password="loginData.password" />

        <div class="form-link">
            <router-link to="/register">Don't have an account?</router-link>
            <router-link to="/retrieve">Forgot password?</router-link>
        </div>

        <button type="submit">Login</button>

    </form>

</template>

<script setup lang="ts">

import { reactive } from 'vue';
import { useRouter } from 'vue-router'

import Swal from 'sweetalert2';
import axios from 'axios';

import EmailInput from '../ui/input/EmailInput.vue';
import PasswordInput from '../ui/input/PasswordInput.vue';

const loginData = reactive({
    email: '',
    password: ''
});

const api = ''; // 待填寫

// 登入
const router = useRouter()
const login = async () => {

    if (!loginData.email || !loginData.password) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Please enter all fields.',
            confirmButtonColor: "#d33",
        });
        return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(loginData.email)) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Invalid email format.',
            confirmButtonColor: "#d33",
        });
        return;
    }

    if (!/\d/.test(loginData.password) ||
        !/[A-Za-z]/.test(loginData.password) ||
        loginData.password.length < 6 ||
        loginData.password.length > 12) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Password must be 6–12 characters, including at least 1 letter and 1 number.',
            confirmButtonColor: "#d33",
        });
        return;
    }

    /* 接後端 API 位置 */
    try {
        const result = await axios.post(`/* 待填寫 */`, loginData, { headers: { 'Content-Type': 'application/json' } });
        console.log("登入成功", result);
        Swal.fire({
            icon: "success",
            title: "Login Successful",
            text: `${result.data.name}, welcome back!`,
            timer: 2000,
            timerProgressBar: true,
        }).then(() => {
            router.push('/member');
        });
    } catch (error) {
        console.error("登入失敗", error);
        Swal.fire({
            icon: "warning",
            title: "Login Failed",
            text: error.response?.data?.message || "Please try again later!",
            confirmButtonColor: "#d33",
        }).then(() => {
            loginData.email = '';
            loginData.password = '';
        });
    }
}

</script>

<style scoped lang="scss" src="@/assets/scss/layout/_form.scss"></style>