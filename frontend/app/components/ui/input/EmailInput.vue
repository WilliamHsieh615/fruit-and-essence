<template>

    <div class="emailInput">
        <label for="email">Email</label>
        <div class="email">
            <input id="email" placeholder="Enter your email" autocomplete="email" type="email" :value="email" @input="emit('update:email', $event.target.value)" />
            <FontAwesomeIcon icon="envelope" class="inputIcon" />
        </div>
    </div>

</template>

<script setup lang="ts">

const props = defineProps({
    email: {
        type: String,
        required: true
    }
});
const emit = defineEmits(['update:email']);

</script>

<style scoped lang="scss" src="@/assets/scss/components/_inputs.scss"></style>