<template>
  <div class="wrap">
    <Nav />
    <Header />
    <main class="index-main">
      <FeatureSection />
      <ArticleSection />
    </main>
    <Footer />
  </div>
</template>

<script setup lang="ts">

import Nav from '~/components/layout/Nav.vue';
import Header from '~/components/layout/Header.vue';
import Footer from '~/components/layout/Footer.vue';
import FeatureSection from '~/components/sections/index/FeatureSection.vue';
import ArticleSection from '~/components/sections/index/ArticleSection.vue';

useSeoMeta({
  title: 'INDEX｜Fruit & Essence'
})

</script>
