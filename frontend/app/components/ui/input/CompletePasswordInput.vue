<template>

    <div class="passwordInput">
        <label for="password">Password</label>
        <div class="password">
            <input id="password" placeholder="Enter your password" :type="showPassword ? 'text' : 'password'" :value="password" @input="emit('update:password', $event.target.value)" />
            <FontAwesomeIcon icon="lock" class="inputIcon" />
            <a href="#" @click.prevent="showPassword = !showPassword">
                <FontAwesomeIcon :icon="showPassword ? 'eye' : 'eye-slash'" class="eyeIcon" />
            </a>
        </div>
        <div class="passwordConfirm">
            <input id="confirm-password" placeholder="Confirm your password" :type="showConfirmPassword ? 'text' : 'password'" :value="confirmPassword" @input="emit('update:confirmPassword', $event.target.value)" />
            <FontAwesomeIcon icon="lock" class="inputIcon" />
            <a href="#" @click.prevent="showConfirmPassword = !showConfirmPassword">
                <FontAwesomeIcon :icon="showConfirmPassword ? 'eye' : 'eye-slash'" class="eyeIcon" />
            </a>
        </div>
        <ul class="matchTip">
            <li><FontAwesomeIcon :icon="passwordLengthTip" /> 長度需為 6~12 碼</li>
            <li><FontAwesomeIcon :icon="passwordContainLetterTip" /> 至少 1 個英文字母</li>
            <li><FontAwesomeIcon :icon="passwordContainDigitTip" /> 至少 1 個數字</li>
            <li><FontAwesomeIcon :icon="passwordMatchTip" /> 密碼是否一致</li>
        </ul>
    </div>

</template>

<script setup lang="ts">

import { ref, computed } from 'vue';

const props = defineProps({
    password: {
        type: String,
        required: true
    },
    confirmPassword: {
        type: String,
        required: true
    }
});
const emit = defineEmits(['update:password', 'update:confirmPassword']);

const showPassword = ref(false);
const showConfirmPassword = ref(false);

const exclamationTip = 'circle-exclamation';
const checkTip = 'circle-check';
const xmarkTip = 'circle-xmark';

// 密碼長度檢查
const passwordLengthTip = computed(() => {
  if (props.password.length === 0) {
    return exclamationTip;
  } else if (props.password.length >= 6 && props.password.length <= 12) {
    return checkTip;
  } else {
    return xmarkTip;
  }
});

// 至少一個字母
const passwordContainLetterTip = computed(() => {
  if (props.password.length === 0) {
    return exclamationTip;
  } else if (/[A-Za-z]/.test(props.password)) {
    return checkTip;
  } else {
    return xmarkTip;
  }
});

// 至少一個數字
const passwordContainDigitTip = computed(() => {
  if (props.password.length === 0) {
    return exclamationTip;
  } else if (/\d/.test(props.password)) {
    return checkTip;
  } else {
    return xmarkTip;
  }
})

// 密碼是否一致
const passwordMatchTip = computed(() => {
  if (props.confirmPassword.length === 0) {
    return exclamationTip;
  } else if (props.password === props.confirmPassword) {
    return checkTip;
  } else {
    return xmarkTip;
  }
})

</script>

<style scoped lang="scss" src="@/assets/scss/components/_inputs.scss"></style>