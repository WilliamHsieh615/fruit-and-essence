<template>

    <section id="products" class="product-list">
        <div v-if="loading" class="loading">Loading...</div>
        <template v-else>
            <div v-if="products.length === 0" class="noProduct">No products available</div>
            <div v-else class="product-card-container">
                <NuxtLink v-for="product in products" :key="product.productId" class="product-card"
                    :to="`/products/${product.category}/${product.productId}`">
                    <div class="window">
                        <img :src='product.imageUrl' :alt="product.productName" />
                    </div>
                    <div class="product-card-content">
                        <h3>{{ product.productName }}</h3>
                        <p><span class="price">$ {{ Number(product.pricePerUnit).toFixed(2) }}</span> / {{ product.unit }}</p>
                    </div>
                </NuxtLink>
            </div>
        </template>
    </section>

</template>

<script setup lang="ts">

const props = defineProps({
    products: {
        type: Array,
        required: true
    },
    loading: {
        type: Boolean,
        required: true
    }
})

</script>

<style scoped lang="scss" src="@/assets/scss/sections/products/_productList.scss"></style>