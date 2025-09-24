import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useProductsStore = defineStore('products',() => {

    const products = ref([])
    const total = ref(0)
    const loading = ref(false)
    const product = ref({})

    const api = "/api/products";

    // 取得商品列表
    const fetchProducts = async (params:
        {
            category?: string
            search?: string
            limit?: number
            offset?: number
            orderBy?: string
            sort?: string
        } = {}
    ) => {
        loading.value = true
        try {
            const result = await axios.get(`${api}`, { params })
            products.value = result.data.results
            total.value = result.data.total
        } catch (error) {
            console.error("取得商品列表失敗:", error)
        } finally {
            loading.value = false
        }
    }

    // 取得單一商品
    const fetchProductById = async (productId: number) => {
        loading.value = true
        try {
            const result = await axios.get(`${api}/${productId}`)
            product.value = result.data
        } catch (err) {
            console.error(`取得商品 ${productId} 失敗:`, err)
            product.value = {}
        } finally {
            loading.value = false
        }
    }

    return {
        products,
        total,
        product,
        loading,
        fetchProducts,
        fetchProductById
    }

});