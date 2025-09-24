<template>

    <a v-show="showScrollTop" class="arrow_up-icon" href="#" @click.prevent="scrollToTop">
        <img src="/images/logo/arrow_up-icon.png" alt="arrow_up" />
    </a>

</template>
<script setup lang="ts">

import { ref, onMounted, onUnmounted } from 'vue'

const showScrollTop = ref(false)

const handleScroll = () => {
    showScrollTop.value = window.scrollY > 300
}

const scrollToTop = () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    })
}

onMounted(() => {
    window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll)
})

</script>
<style scoped lang="scss" src="@/assets/scss/components/_icon.scss"></style>