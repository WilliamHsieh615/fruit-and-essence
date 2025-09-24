<template>

    <div class="verifyInput">
        <label>Enter 4-digit verification code</label>
        <vue3-otp-input class="my-otp" v-model="localCode" :num-inputs="4" input-type="number" />
    </div>

</template>

<script setup lang="ts">
import { computed } from 'vue'

import Vue3OtpInput from "vue3-otp-input"

const props = defineProps({
    code: {
        type: String,
        required: true
    }
});
const emit = defineEmits(['update:code']);

const localCode = computed({
    get: () => props.code,
    set: (val) => emit('update:code', val)
})

</script>

<style scoped lang="scss" src="@/assets/scss/components/_inputs.scss"></style>