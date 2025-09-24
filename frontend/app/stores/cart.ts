import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
    const items = ref<{ productId: number, name: string, price: number, unit: string, count: number, imageUrl: string }[]>([])

    const totalCount = computed(() => items.value.reduce((sum, item) => sum + item.count, 0))
    const totalPrice = computed(() => items.value.reduce((sum, item) => sum + item.price * item.count, 0))

    const addItem = (newItem: typeof items.value[0]) => {
        const existing = items.value.find(i => i.productId === newItem.productId)
        if (existing) {
            existing.count += newItem.count
        } else {
            items.value.push({ ...newItem })
        }
    }

    return { items, totalCount, totalPrice, addItem }
})