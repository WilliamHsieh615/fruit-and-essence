<template>

    <form class="retrieve-form" @submit.prevent="retrieve">

        <h2>RETRIEVE</h2>

        <template v-if="!showEnterPasswordInput">

            <EmailInput v-model:email="sentVerifyData.email" />

            <button type="button" :disabled="sentCodeDisabled" @click="sendVerificationCode">{{ sentCodeDisabled ?
                `Resend in ${countdown}s` : 'Send Verification Code' }}</button>

            <VerifyInput v-if="showVerify" v-model:code="verifyData.code" />

            <button v-if="showVerify" type="button" @click="verifyCode">Verify</button>

        </template>

        <template v-if="showEnterPasswordInput">

            <CompletePasswordInput v-model:password="retrieveData.password"
                v-model:confirm-password="confirmPasswordField" />

            <button type="submit">Retrieve</button>
        </template>

    </form>

</template>

<script setup lang="ts">

import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router'

import Swal from 'sweetalert2';
import axios from 'axios';

import EmailInput from '../ui/input/EmailInput.vue';
import CompletePasswordInput from '../ui/input/CompletePasswordInput.vue';
import VerifyInput from '../ui/input/VerifyInput.vue';

const sentVerifyData = reactive({
    email: ''
});

const verifyData = reactive({
    code: ''
})

const retrieveData = reactive({
    password: ''
});

const confirmPasswordField = ref('');

const sentCodeDisabled = ref(false);
const showVerify = ref(false);
const showEnterPasswordInput = ref(false);
const countdown = ref(0);

const api = ''; // 待填寫

// 發送驗證碼
const sendVerificationCode = async () => {

    if (!sentVerifyData.email) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Please enter your email address.',
            confirmButtonColor: "#d33",
        });
        return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(sentVerifyData.email)) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Invalid email format.',
            confirmButtonColor: "#d33",
        });
        return;
    }

    /* 接後端 API 位置 */
    try {
        const result = await axios.post(`/* 待填寫 */`, sentVerifyData, { headers: { 'Content-Type': 'application/json' } });
        console.log("寄送驗證碼成功", result);
        Swal.fire({
            icon: 'success',
            title: 'Verification code sent',
            text: `A verification code has been sent to ${sentVerifyData.email}.`
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

const verifyCode = async () => {

    if (showVerify.value && !verifyData.code) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Please enter the full 4-digit verification code.',
            confirmButtonColor: "#d33",
        });
        return false;
    }

    try {
        const result = await axios.post(`/* 待填寫 */`, verifyData, { headers: { 'Content-Type': 'application/json' } });
        console.log("驗證成功", result);
        Swal.fire({
            icon: "success",
            title: "Verified!",
            text: "Verification successful."
        }).then(() => {
            showEnterPasswordInput.value = true
        });
    } catch (error) {
        console.error("驗證碼失敗", error);
        Swal.fire({
            icon: "warning",
            title: "Incorrect verification code or system error",
            text: "Please try again later.",
            confirmButtonColor: "#d33",
        });
    }
}

// 重設
const router = useRouter()
const retrieve = async () => {

    if (!retrieveData.password || !confirmPasswordField.value) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Please enter password.',
            confirmButtonColor: "#d33",
        });
        return;
    }

    if (!/\d/.test(retrieveData.password) ||
        !/[A-Za-z]/.test(retrieveData.password) ||
        retrieveData.password.length < 6 ||
        retrieveData.password.length > 12) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Password must be 6–12 characters, including at least 1 letter and 1 number.',
            confirmButtonColor: "#d33",
        });
        return;
    }

    if (retrieveData.password !== confirmPasswordField.value) {
        Swal.fire({
            icon: 'warning',
            title: 'Oops!',
            text: 'Passwords do not match.',
            confirmButtonColor: "#d33",
        });
        return;
    }

    /* 接後端 API 位置 */
    try {
        const result = await axios.post(`/* 待填寫 */`, retrieveData, { headers: { 'Content-Type': 'application/json' } });
        console.log("重設成功", result);
        Swal.fire({
            icon: "success",
            title: "Success",
            text: "Password has been reset successfully.",
            timer: 2000,
            timerProgressBar: true,
        }).then(() => {
            router.push('/login');
        });
    } catch (error) {
        console.error("重設失敗", error);
        Swal.fire({
            icon: "warning",
            title: "Failed",
            text: "Please try again later!",
            confirmButtonColor: "#d33",
        }).then(() => {
            retrieveData.password = '';
            confirmPasswordField.value = '';
        });
    }

}

</script>

<style scoped lang="scss" src="@/assets/scss/layout/_form.scss"></style>