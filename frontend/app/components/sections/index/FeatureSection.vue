<template>

    <section class="feature">
        <ul>
            <li v-for="(item, i) in data" :key="i">
                <h3>{{ item.title }}</h3>
                <img :src="item.imgUrl" :alt="item.title" />
                <p>{{ item.decription }}</p>
            </li>
        </ul>
    </section>

</template>

<script setup lang="ts">

import { reactive } from 'vue';

const data = reactive([
    {
    title: 'Support Local Farmers',
    imgUrl: '/images/feature/Farmer-sm.png',
    decription: 'Grown with care by local farmers, ensuring that every product fairly rewards them and strengthens the local market.'
    },
    {
    title: 'Fresh fromthe Farm',
    imgUrl: '/images/feature/ITruck-sm.png',
    decription: 'From field to table, we guarantee direct delivery from the origin, preserving the freshest natural flavors in every shipment.'
    },
    {
    title: 'Sustainable & Eco-Friendly',
    imgUrl: '/images/feature/Earth-sm.png',
    decription: 'Strictly monitored by experts, with responsible pesticide use and reduced carbon emissions, ensuring no harm to the environment or your health.'
    }
])

</script>

<style scoped lang="scss" src="@/assets/scss/sections/index/_feature.scss"></style>