<template>
    <div class="product-gallery">
        <!-- 主輪播 -->
        <Swiper :modules="modules" navigation :autoplay="{ delay: 3000, disableOnInteraction: false }" loop
            class="main-swiper">
            <SwiperSlide v-for="(img, index) in images" :key="index">
                <img :src="img" alt="Product Image" class="cursor-pointer rounded-lg" @click="openLightbox(index)" />
            </SwiperSlide>
        </Swiper>

        <!-- 縮圖輪播 -->
        <div class="mt-4">
            <Swiper :slides-per-view="4" space-between="10" class="thumbs-swiper">
                <SwiperSlide v-for="(img, index) in images" :key="index">
                    <img :src="img" alt="Thumbnail"
                        class="cursor-pointer rounded border border-gray-300 hover:border-primary"
                        @click="openLightbox(index)" />
                </SwiperSlide>
            </Swiper>
        </div>

        <!-- Lightbox -->
        <VueEasyLightbox :visible="visible" :imgs="images" :index="currentIndex" @hide="visible = false" />
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/thumbs'
import 'swiper/css/autoplay'
import { Navigation, Thumbs, Autoplay } from 'swiper/modules'
import VueEasyLightbox from 'vue-easy-lightbox'

const modules = [Navigation, Thumbs, Autoplay]

// 假資料：產品圖片
const images = [
    '/images/products/juice1.jpg',
    '/images/products/juice2.jpg',
    '/images/products/juice3.jpg',
    '/images/products/juice4.jpg'
]

const visible = ref(false)
const currentIndex = ref(0)

const openLightbox = (index: number) => {
    currentIndex.value = index
    visible.value = true
}
</script>



<style scoped>
.main-swiper img {
    width: 100%;
    max-height: 400px;
    object-fit: contain;
}

.thumbs-swiper img {
    width: 100%;
    height: 80px;
    object-fit: cover;
}
</style>
