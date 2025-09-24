<template>
    <form class="change_password-form" @submit.prevent="">

        <h2>CHANGE PASSWORD</h2>

        <EmailInput v-model:email="changePasswordData.email" />

        <CompletePasswordInput v-model:password="changePasswordData.password"
            v-model:confirm-password="confirmPasswordField" />

        <button type="button" :disabled="sentCodeDisabled" @click="sendVerificationCode">{{ sentCodeDisabled ? `Resend
            in ${countdown}s` : 'Send Verification Code' }}</button>

        <VerifyInput v-if="showVerify" v-model:code="verifyData.code" />

        <button v-if="showVerify" type="submit">Save</button>

    </form>
</template>
<script setup lang="ts">

import { ref, reactive } from 'vue';

import CompletePasswordInput from '~/components/ui/input/CompletePasswordInput.vue';
import EmailInput from '~/components/ui/input/EmailInput.vue';

const changePasswordData = reactive({
    email: '',
    password: ''
});

const confirmPasswordField = ref('')
const sentCodeDisabled = ref(false);
const showVerify = ref(false);
const countdown = ref(0);

</script>
<style scoped lang="scss" src="@/assets/scss/layout/_form.scss"></style>