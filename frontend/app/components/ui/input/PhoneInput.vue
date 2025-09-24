<template>

    <div class="phoneInput">
        <label for="phone">Phone Number</label>
        <div class="phone">
            <input id="phone" placeholder="Enter your phone number" type="tel" :value="phone" @input="emit('update:phone', $event.target.value)" />
            <FontAwesomeIcon icon="phone" class="inputIcon" />
        </div>
    </div>

</template>

<script setup lang="ts">

const props = defineProps({
    phone: {
        type: String,
        required: true
    }
});
const emit = defineEmits(['update:phone']);

</script>

<style scoped lang="scss" src="@/assets/scss/components/_inputs.scss"></style>