<template>

    <div class="wrap">
        <Nav />

        <main class="login-main form-main">
            <LoginForm />
        </main>
        
        <Footer />
    </div>

</template>

<script setup lang="ts">

import Nav from '~/components/layout/Nav.vue';
import Footer from '~/components/layout/Footer.vue';
import LoginForm from '~/components/forms/LoginForm.vue';

useSeoMeta({
  title: 'LOGIN｜Fruit & Essence'
})

</script>

<style scoped lang="scss" src="@/assets/scss/pages/_login.scss"></style>