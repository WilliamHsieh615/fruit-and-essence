<template>
    <router-link class="cart-icon" to="/cart">
        <img src="/images/logo/shopping_cart-icon.png" alt="Cart" />
        <span v-if="cartCount > 0" class="badge">{{ cartCount }}</span>
    </router-link>
</template>
<script setup lang="ts">
// import { storeToRefs } from 'pinia'
// import { useCartStore } from '~/stores/cart'

// const cartStore = useCartStore()
// const { cartCount } = storeToRefs(cartStore)
</script>
<style scoped lang="scss" src="@/assets/scss/components/_icon.scss"></style>
