<template>
    <div class="wrap">
        <Nav />
        <Banner :headline="headline" :sub-headline="subHeadline" />
        <Aside :items="memberMenu" />
        <main class="help_center-main">
            <NuxtPage />
        </main>
        <Footer />
    </div>
</template>
<script setup lang="ts">

import Nav from '~/components/layout/Nav.vue';
import Footer from '~/components/layout/Footer.vue';
import Banner from '~/components/layout/Banner.vue';
import Aside from '~/components/layout/Aside.vue';

definePageMeta({
  redirect: '/help-center/faq'
})

useSeoMeta({
    title: 'HELP CENTER｜Fruit & Essence'
})
const headline = ref("From Farm to Bottle");
const subHeadline = ref("Freshly Pressed Juices Delivered Straight from Local Orchards");

const memberMenu = reactive([
    {
        label: "FAQ",
        to: "/help-center/faq"
    },
    // {
    //     label: "Tutorial",
    //     to: "/help-center/tutorial"
    // },
    {
        label: "Contact Support",
        to: "/help-center/contact-support"
    }
])

</script>
<style scoped lang="scss" src="@/assets/scss/pages/_helpCenter.scss"></style>