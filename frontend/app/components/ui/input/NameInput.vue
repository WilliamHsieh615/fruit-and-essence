<template>

    <div class="nameInput">
        <label for="name">Name</label>
        <div class="name">
            <input id="name" placeholder="Enter your name" type="text" :value="name" @input="emit('update:name', $event.target.value)" />
            <FontAwesomeIcon icon="user" class="inputIcon" />
        </div>
    </div>

</template>

<script setup lang="ts">

const props = defineProps({
    name: {
        type: String,
        required: true
    }
});
const emit = defineEmits(['update:name']);

</script>

<style scoped lang="scss" src="@/assets/scss/components/_inputs.scss"></style>