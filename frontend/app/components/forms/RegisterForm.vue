<template>

    <form class="register-form" @submit.prevent="register">

        <h2>REGISTER</h2>

        <EmailInput v-model:email="registerData.email" />

        <CompletePasswordInput v-model:password="registerData.password"
            v-model:confirm-password="confirmPasswordField" />

        <NameInput v-model:name="registerData.name" />

        <PhoneInput v-model:phone="registerData.phone" />

        <BirthdayInput v-model:birthday="registerData.birthday" />

        <TermsInput 
            v-model:agree-privacy="agreePrivacyField" 
            v-model:agree-terms="agreeTermsField"
            v-model:subscribe="registerData.subscribe" />

        <button type="button" :disabled="sentCodeDisabled" @click="sendVerificationCode">{{ sentCodeDisabled ? `Resend
            in ${countdown}s` : 'Send Verification Code' }}</button>

        <VerifyInput v-if="showVerify" v-model:code="verifyData.code" />

        <button v-if="showVerify" type="submit">Register</button>

    </form>

</template>

<script setup lang="ts">

import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router'

import Swal from 'sweetalert2';
import axios from 'axios';

import EmailInput from '../ui/input/EmailInput.vue';
import CompletePasswordInput from '../ui/input/CompletePasswordInput.vue';
import NameInput from '../ui/input/NameInput.vue';
import PhoneInput from '../ui/input/PhoneInput.vue';
import BirthdayInput from '../ui/input/BirthdayInput.vue';
import TermsInput from '../ui/input/TermsInput.vue';
import VerifyInput from '../ui/input/VerifyInput.vue';

const registerData = reactive({
    email: '',
    password: '',
    name: '',
    phone: '',
    birthday: '',
    subscribe: false
});

const verifyData = reactive({
    code: ''
})

const confirmPasswordField = ref('');
const agreePrivacyField = ref(false);
const agreeTermsField = ref(false);

const sentCodeDisabled = ref(false);
const showVerify = ref(false);
const countdown = ref(0);

const api = ''; // 待填寫

// 表單驗證
const validateForm = () => {

    // 檢查是否為空值
    if (!registerData.email || !registerData.password || !registerData.name || !registerData.phone || !registerData.birthday || !confirmPasswordField.value) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Please enter all fields.',
            confirmButtonColor: "#d33",
        });
        return false;
    }

    // 檢查條款是否有勾選
    if (!agreePrivacyField.value || !agreeTermsField.value) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Please agree to the Privacy Policy and the Terms of Service.',
            confirmButtonColor: "#d33",
        });
        return false;
    }

    // email 格式檢查
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(registerData.email)) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Invalid email format.',
            confirmButtonColor: "#d33",
        });
        return false;
    }

    // 密碼格式檢查
    if (!/\d/.test(registerData.password) ||
        !/[A-Za-z]/.test(registerData.password) ||
        registerData.password.length < 6 ||
        registerData.password.length > 12) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Password must be 6–12 characters, including at least 1 letter and 1 number.',
            confirmButtonColor: "#d33",
        });
        return false;
    }

    // 檢查密碼一致
    if (registerData.password !== confirmPasswordField.value) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Passwords do not match.',
            confirmButtonColor: "#d33",
        });
        return false;
    }

    // 檢查驗證碼是否有輸入
    if (showVerify.value && !verifyData.code) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Please enter the full 4-digit verification code.',
            confirmButtonColor: "#d33",
        });
        return false;
    }

    return true;
}

// 發送驗證碼
const sendVerificationCode = async () => {
    if (!validateForm()) return

    /* 接後端 API 位置 */
    try {
        const result = await axios.post(`/* 待填寫 */`, registerData, { headers: { 'Content-Type': 'application/json' } });
        console.log("寄送驗證碼成功", result);
        Swal.fire({
            icon: 'success',
            title: 'Verification code sent',
            text: `A verification code has been sent to ${registerData.email}.`
        }).then(() => {
            sentCodeDisabled.value = true
            showVerify.value = true
            countdown.value = 60
        });
    } catch (error) {
        console.error("寄送驗證碼失敗", error);
        Swal.fire({
            icon: "warning",
            title: "Failed to send verification code",
            text: "Please try again later!",
            confirmButtonColor: "#d33",
        });
    }

    const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
            clearInterval(timer)
            sentCodeDisabled.value = false
        }
    }, 1000)
}

// 註冊
const router = useRouter()
const register = async () => {

    if (!validateForm()) return

    /* 接後端 API 位置 */
    try {
        const result = await axios.post(`/* 待填寫 */`, registerData, { headers: { 'Content-Type': 'application/json' } });
        console.log("註冊成功", result);
        Swal.fire({
            icon: "success",
            title: "Registration Successful",
            text: "Welcome to Fruit and Essence!",
            timer: 2000,
            timerProgressBar: true,
        }).then(() => {
            router.push('/login');
        });
    } catch (error) {
        console.error("註冊失敗", error);
        Swal.fire({
            icon: "warning",
            title: "Registration Failed",
            text: "Please try again later!",
            confirmButtonColor: "#d33",
        }).then(() => {
            registerData.email = '';
            registerData.password = '';
            registerData.name = '';
            registerData.phone = '';
            registerData.birthday = '';
            registerData.subscribe = false;
            confirmPasswordField.value = '';
            agreePrivacyField.value = false;
            agreeTermsField.value = false;
        });
    }
}

</script>

<style scoped lang="scss" src="@/assets/scss/layout/_form.scss"></style>