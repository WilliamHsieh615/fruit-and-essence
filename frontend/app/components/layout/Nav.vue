<template>
    <nav>
        <h1>
            <NuxtLink to="/">Fruit & Essence</NuxtLink>
        </h1>
        <ul :class="['menu', { showmenu: showMenu }]">
            <li>
                <NuxtLink to="/products">Products</NuxtLink>
            </li>
            <li>
                <NuxtLink to="/help-center">Help Center</NuxtLink>
            </li>
            <li>
                <NuxtLink to="/member">Member</NuxtLink>
            </li>
            <li>
                <NuxtLink to="/login">Login</NuxtLink>
            </li>
            <li><a href="#">Logout</a></li>
        </ul>
        <a href="#" class="hamburgerMenu" @click.prevent="toggleMenu">
            <span v-if="showMenu==false" class="material-symbols-outlined">
                menu
            </span>
            <span v-else class="material-symbols-outlined">
                menu_open
            </span>
        </a>

    </nav>
</template>

<script setup lang="ts">

import { ref } from 'vue';

const showMenu = ref(false)

function toggleMenu() {
    showMenu.value = !showMenu.value
}

</script>

<style scoped lang="scss" src="@/assets/scss/layout/_nav.scss"></style>