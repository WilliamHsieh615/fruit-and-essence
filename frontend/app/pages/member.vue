<template>
    <div class="wrap">
        <Nav />
        <Banner :headline="headline" :sub-headline="subHeadline" />
        <Aside :items="memberMenu" />
        <main class="member-main">
            <NuxtPage />
        </main>
        <Footer />
    </div>
</template>
<script setup lang="ts">
import { ref, reactive } from 'vue';

import Nav from '~/components/layout/Nav.vue';
import Footer from '~/components/layout/Footer.vue';
import Banner from '~/components/layout/Banner.vue';
import Aside from '~/components/layout/Aside.vue';

definePageMeta({
  redirect: '/member/profile'
})

useSeoMeta({
    title: 'MEMBER｜Fruit & Essence'
})

const headline = ref("Pure. Fresh. Delicious.");
const subHeadline = ref("Fruit & Essence, Straight from Nature");

const memberMenu = reactive([
    {
        label: "PROFILE",
        to: "/member/profile"
    },
    {
        label: "CHANGE PASSWORD",
        to: "/member/changePassword"
    },
    {
        label: "SUBSCRIPTION PREFERENCES",
        to: "/member/subscriptionPreferences.vue"
    },
    {
        label: "ORDER",
        to: "/member/order"
    }
])

</script>
<style scoped lang="scss" src="@/assets/scss/pages/_member.scss"></style>