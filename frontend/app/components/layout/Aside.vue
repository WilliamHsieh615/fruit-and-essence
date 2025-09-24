<template>
    <aside>
        <ul class="menu">
            <li v-for="item in items" :key="item.label">
                <NuxtLink v-if="item.to" :to="item.to">{{ item.label }}</NuxtLink>
                <button v-else @click="item.action()">{{ item.label }}</button>
            </li>
        </ul>
    </aside>
</template>

<script setup lang="ts">

const props = defineProps({
    items: {
        type: Array,
        required: true
    }
})

</script>

<style scoped lang="scss" src="@/assets/scss/layout/_aside.scss"></style>