<template>
    <div class="faq">
        <div v-for="(faq, i) in faqData" :key="i" class="faq-container">
            <h3><span v-html="faq.icon"></span>{{ faq.title }}</h3>

            <ul>
                <li v-for="(item, j) in faq.items" :key="j" class="faq-item">
                    <button class="question" @click="toggleAnswer(i, j)">
                        {{ item.question }}
                        <span class="arrow" :class="{ open: item.show }">▼</span>
                    </button>
                    <p v-if="item.show" class="answer">
                        {{ item.answer }}
                    </p>
                </li>
            </ul>
        </div>
    </div>
</template>
<script setup lang="ts">

import axios from 'axios';
import { ref, onMounted } from 'vue';

const faqData = ref([])

onMounted(async () => {
    try {
        const result = await axios.get('/data/faq.json')
        faqData.value = result.data.faqs.map((category) => ({
            ...category,
            items: category.items.map(item => ({ ...item, show: false }))
        }))
    } catch (error) {
        console.error("取得資料失敗:", error)
    }
})

const toggleAnswer = (categoryIndex, itemIndex) => {
    faqData.value[categoryIndex].items[itemIndex].show = !faqData.value[categoryIndex].items[itemIndex].show
}

</script>
<style scoped lang="scss" src="@/assets/scss/sections/helpCenter/_faq.scss"></style>