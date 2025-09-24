<template>
    <div>
        <Banner :headline="headline" :sub-headline="subHeadline" />
        <Aside :items="productMenu" />
        <main class="products-main">
            <Search @search="filterByKeyword" />

            <div class="sort">
                <select v-model="sortOption" @change="onSortChange">
                    <option value="priceAsc">Price: Low → High</option>
                    <option value="priceDesc">Price: High → Low</option>
                </select>
            </div>

            <ProductListSection :products="products" :loading="loading" />
            <Pagination :current-page="currentPage" :total-pages="totalPage"
                @update:page="(page) => filterByCategory(undefined, page)" />
        </main>
    </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { storeToRefs } from 'pinia'
import { useProductsStore } from '~/stores/products'

import Banner from '~/components/layout/Banner.vue';
import Aside from '~/components/layout/Aside.vue';
import ProductListSection from '~/components/sections/products/ProductListSection.vue';
import Pagination from '~/components/ui/other/Pagination.vue';
import Search from '~/components/ui/other/Search.vue';

useSeoMeta({
    title: 'PRODUCTS｜Fruit & Essence'
})

const headline = ref("Freshness You Can Drink");
const subHeadline = ref("Crafted from Nature’s Best Fruits");

const productsStore = useProductsStore()
const { fetchProducts } = productsStore
const { products, total, loading } = storeToRefs(productsStore)

const limit = 8
const currentPage = ref(1)

// pagination
const totalPage = computed(() => Math.max(1, Math.ceil(total.value / limit)))

// 搜尋
const filterByKeyword = (keyword: string) => {
    const orderBy = "pricePerUnit"
    const sort: "asc" | "desc" = sortOption.value === "priceAsc" ? "asc" : "desc"
    fetchProducts({
        category: currentCategory.value || undefined,
        search: keyword,
        limit,
        offset: 0,
        orderBy,
        sort
    })
    currentPage.value = 1
}

// 排序
const sortOption = ref("priceAsc")
const onSortChange = () => {
    const orderBy = "pricePerUnit"
    const sort: "asc" | "desc" = sortOption.value === "priceAsc" ? "asc" : "desc"
    filterByCategory(currentCategory.value, 1, orderBy, sort)
    window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 類別
const currentCategory = ref("")
const filterByCategory = async (
    category = currentCategory.value,
    page = 1,
    orderBy = 'pricePerUnit',
    sort: 'asc' | 'desc' = sortOption.value === 'priceAsc' ? 'asc' : 'desc'
) => {
    currentCategory.value = category
    currentPage.value = page

    await fetchProducts({
        category: category || undefined,
        limit,
        offset: (page - 1) * limit,
        orderBy,
        sort
    })
}

// 選單
const productMenu = reactive([
    {
        label: "ALL",
        action: () => filterByCategory(""),
    },
    {
        label: "POME",
        action: () => filterByCategory("POME"),
    },
    {
        label: "TROPICAL",
        action: () => filterByCategory("TROPICAL"),
    },
    {
        label: "BERRY",
        action: () => filterByCategory("BERRY"),
    },
    {
        label: "CITRUS",
        action: () => filterByCategory("CITRUS"),
    },
    {
        label: "MELON",
        action: () => filterByCategory("MELON"),
    },
    {
        label: "STONE FRUIT",
        action: () => filterByCategory("STONE_FRUIT"),
    }
])

onMounted(() => {
    filterByCategory("")
})

</script>
<style scoped lang="scss" src="@/assets/scss/pages/_products.scss"></style>
