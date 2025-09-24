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
    </div>

</template>

<script setup lang="ts">

import { ref } from 'vue';

const props = defineProps({
    password: {
        type: String,
        required: true
    }
});
const emit = defineEmits(['update:password']);

const showPassword = ref(false);

</script>

<style scoped lang="scss" src="@/assets/scss/components/_inputs.scss"></style>