<template>

    <div class="wrap">
        <Nav />

        <main class="retrieve-main form-main">
            <RetrieveForm />
        </main>
        
        <Footer />
    </div>

</template>

<script setup lang="ts">

import Nav from '~/components/layout/Nav.vue';
import Footer from '~/components/layout/Footer.vue';
import RetrieveForm from '~/components/forms/RetrieveForm.vue';

useSeoMeta({
  title: 'RETRIEVE｜Fruit & Essence'
})

</script>

<style scoped lang="scss" src="@/assets/scss/pages/_retrieve.scss"></style>