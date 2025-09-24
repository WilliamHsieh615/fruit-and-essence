<template>

    <div class="banner">

        <div class="title">
            <h2>{{ headline }}</h2>
            <p>{{ subHeadline }}</p>
        </div>

    </div>

</template>

<script setup lang="ts">

const props = defineProps({
    headline: {
        type: String,
        required: true
    },
    subHeadline: {
        type: String,
        required: true
    }
})

</script>

<style scoped lang="scss" src="@/assets/scss/layout/_banner.scss"></style>