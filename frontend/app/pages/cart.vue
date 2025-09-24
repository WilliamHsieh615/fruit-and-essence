<template>
    <div class="wrap">
        <Nav />

        <CartListSection />

        <Footer />
    </div>
</template>
<script setup lang="ts">

import Footer from '~/components/layout/Footer.vue';
import Nav from '~/components/layout/Nav.vue';
import CartListSection from '~/components/sections/cart/CartListSection.vue';

useSeoMeta({
  title: 'CART｜Fruit & Essence'
})

</script>
<style scoped lang="scss" src="@/assets/scss/pages/_cart.scss"></style>