<template>
    <section v-if="loading" class="loading">Loading...</section>
    <section v-else class="product-detail">
        <div class="product-container">

            <div class="product-image-wrapper">
                <img :src="product.imageUrl" :alt="product.productName" class="product-image" />
            </div>

            <div class="product-info">

                <div class="product-content">
                    <p class="category">Category: {{ product.category }}</p>
                    <h1 class="product-name">{{ product.productName }}</h1>
                    <p class="description">{{ product.description }}</p>
                    <div class="unit-price">Unit Price: <p class="peice"><span>$ {{
                        Number(product.pricePerUnit).toFixed(2) }}</span> / {{ product.unit }}</p>
                    </div>
                    <p class="stock">Stock:<br /> {{ product.stock }} {{ product.unit }}</p>
                    <p class="last-modified">Restock Date:<br /> {{ product.lastModifiedDate }}</p>
                </div>

                <div class="quantity-control">
                    <button :disabled="disableDecrease" @click="decrease">
                        <i class="bi bi-dash"></i>
                    </button>
                    <span>{{ product.unitType === 'COUNT' ? count : count.toFixed(1) }}</span>
                    <button :disabled="disableIncrease" @click="increase">
                        <i class="bi bi-plus"></i>
                    </button>
                </div>

                <p class="subtotal">Subtotal: <span>$ {{ subtotal.toFixed(2) }}</span></p>

                <button class="add-to-cart" @click="addToCart">Add to Cart</button>
                <nuxt-link class="go-to-cart" to="/cart">Checkout</nuxt-link>
            </div>

            <HeartCheckbox />

        </div>
    </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'

import HeartCheckbox from '~/components/ui/other/HeartCheckbox.vue'

import { useProductsStore } from '~/stores/products'
import { useCartStore } from '~/stores/cart'

import Swal from 'sweetalert2'

const route = useRoute()
const productsStore = useProductsStore()
const { fetchProductById } = productsStore
const { product, loading } = storeToRefs(productsStore)
const cart = useCartStore()

const count = ref(1)

onMounted(async () => {
    await fetchProductById(Number(route.params.productId))

    useSeoMeta({
        title: `${product.value.productName}｜Fruit & Essence`,
        description: `${product.value.description}`
    })
})

// 數量控制
const increase = () => {
    if (product.value?.unitType === 'COUNT') {
        if (count.value < Math.floor(product.value.stock)) {
            count.value += 1
        }
    } else if (product.value?.unitType === 'WEIGHT') {
        if (count.value + 0.5 <= product.value.stock) {
            count.value = parseFloat((count.value + 0.5).toFixed(1))
        }
    }
}

const decrease = () => {
    if (product.value?.unitType === 'COUNT') {
        if (count.value > 1) {
            count.value -= 1
        }
    } else if (product.value?.unitType === 'WEIGHT') {
        if (count.value > 0.5) {
            count.value = parseFloat((count.value - 0.5).toFixed(1))
        }
    }
}

const disableDecrease = computed(() => {
    if (product.value?.unitType === 'COUNT') {
        return count.value <= 1
    }
    if (product.value?.unitType === 'WEIGHT') {
        return count.value <= 0.5
    }
    return true
})

const disableIncrease = computed(() => {
    if (product.value?.unitType === 'COUNT') {
        return count.value >= Math.floor(product.value.stock)
    }
    if (product.value?.unitType === 'WEIGHT') {
        return count.value + 0.5 > product.value.stock
    }
    return true
})

// 小計
const subtotal = computed(() => Number(product.value?.pricePerUnit ?? 0) * count.value)

// 加入購物車
const addToCart = () => {
    if (!product.value) return
    cart.addItem({
        productId: product.value.productId,
        name: product.value.productName,
        price: product.value.pricePerUnit,
        unit: product.value.unit,
        count: product.value.unitType === 'COUNT' ? Math.floor(count.value) : count.value,
        imageUrl: product.value.imageUrl
    })
    Swal.fire({
        icon: "success",
        title: "Added to Cart",
        text: `${product.value.productName}, has been added to your cart`,
        timer: 2000,
        timerProgressBar: true,
    });
}

</script>

<style scoped lang="scss" src="@/assets/scss/pages/_productDetail.scss"></style>
