<template>

    <div class="wrap">
        <Nav />

        <main class="register-main form-main">
            <RegisterForm />
        </main>
        
        <Footer />
    </div>

</template>

<script setup lang="ts">

import Nav from '~/components/layout/Nav.vue';
import Footer from '~/components/layout/Footer.vue';
import RegisterForm from '~/components/forms/RegisterForm.vue';

useSeoMeta({
  title: 'REGISTER｜Fruit & Essence'
})

</script>

<style scoped lang="scss" src="@/assets/scss/pages/_register.scss"></style>