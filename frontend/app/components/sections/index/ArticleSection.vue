<template>
    <article>
        <div class="article-container" v-for="(article, i) in articles" :key="i">
            <div class="article-image">
                <img :src="article.imgUrl" alt="article.title">
            </div>
            <div class="article-content">
                <h2>{{ article.title }}</h2>
                <p v-for="(part, j) in article.showFull ? article.content : [article.content[0]]" :key="j">
                    {{ part }}
                </p>
                <a href="javascript:void(0)" @click="toggleReadMore(i)">
                    {{ article.showFull ? "Read Less" : "Read More..." }}
                </a>
            </div>
        </div>
    </article>
</template>

<script setup lang="ts">

import { reactive } from 'vue';

const articles = reactive([
    {
        imgUrl: "/images/banner/article1.jpg",
        title: "Discover the Freshness of Cold-Pressed Juices",
        content: ["At Fruit & Essence, we bring the orchard straight to your glass. Our cold-pressed juices are crafted from fresh, locally sourced fruits and vegetables, gently pressed at low temperatures to preserve all the natural vitamins, enzymes, and antioxidants.Unlike traditional juices, our method ensures that every sip delivers pure, vibrant flavor along with maximum nutritional benefits.", "Perfect for a healthy breakfast, a midday boost, or a refreshing treat anytime, our juices are a convenient and delicious way to enjoy the natural goodness of fruits, straight from farm to bottle."],
        showFull: false,
    },
    {
        imgUrl: "/images/banner/article2.jpg",
        title: "The Rise of Cold-Pressed Juice in Health-Conscious Living",
        content: ["In today’s fast-paced world, health-conscious individuals are turning to cold-pressed juice as a refreshing alternative to sugary drinks. Unlike traditional juicing methods that use high-speed blades, cold-pressed juice is extracted slowly with hydraulic pressure, preserving essential vitamins, minerals, and enzymes. Nutrition experts emphasize that this method helps retain antioxidants and phytonutrients, which play a vital role in boosting immunity and promoting overall well-being.", "More than just a beverage, cold-pressed juice represents a modern lifestyle choice—pure, simple, and nourishing. With every sip, consumers are not only hydrating their bodies but also embracing a philosophy of clean and mindful living."],
        showFull: false,
    }
])

const toggleReadMore = (index: number) => {
    articles[index].showFull = !articles[index].showFull;
};

</script>

<style scoped lang="scss" src="@/assets/scss/sections/index/_article.scss"></style>