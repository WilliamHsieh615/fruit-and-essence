<template>

    <div class="terms">
        <div v-if="showPrivacy" class="privacyTerms">
            <input id="privacyTerms" type="checkbox" :disabled="!privacyEnabled" :checked="agreePrivacy" @change="emit('update:agreePrivacy', $event.target.checked)" />
            <label for="privacyTerms">
                I agree to the <a href="/assets/privacy_terms.pdf" target="_blank" @click="enablePrivacy">Privacy Policy</a>
            </label>
        </div>

        <div v-if="showTerms" class="mamberTerms">
            <input id="mamberTerms" type="checkbox" :disabled="!termsEnabled" :checked="agreeTerms" @change="emit('update:agreeTerms', $event.target.checked)" />
            <label for="mamberTerms">
                I agree to the <a href="/assets/mamber_terms.pdf" target="_blank" @click="enableTerms">Terms of Service</a>
            </label>
        </div>

        <div v-if="showSubscribe" class="subscribe">
            <input id="subscribe" type="checkbox" :checked="subscribe"
                @change="emit('update:subscribe', $event.target.checked)" />
            <label for="subscribe"> Subscribe to promotional messages</label>
        </div>
    </div>

</template>

<script setup lang="ts">

import { ref } from 'vue'

const props = defineProps({
    agreePrivacy: {
        type: Boolean,
        default: false
    },
    agreeTerms: {
        type: Boolean,
        default: false
    },
    subscribe: {
        type: Boolean,
        default: false
    },
    showPrivacy: { 
        type: Boolean, 
        default: true 
    },
    showTerms: { 
        type: Boolean, 
        default: true 
    },
    showSubscribe: { 
        type: Boolean, 
        default: true 
    }
});
const emit = defineEmits(['update:agreePrivacy', 'update:agreeTerms', 'update:subscribe']);

const privacyEnabled = ref(false)
const termsEnabled = ref(false)


const enablePrivacy = () => {
    privacyEnabled.value = true
    emit('update:agreePrivacy', true)
}

const enableTerms = () => {
    termsEnabled.value = true
    emit('update:agreeTerms', true)
}

</script>

<style scoped lang="scss" src="@/assets/scss/components/_inputs.scss"></style>