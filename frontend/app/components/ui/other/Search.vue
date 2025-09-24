<template>

    <div class="search">
        <button class="searchIcon" @click="onSearch">
            <i class="bi bi-search"></i>
        </button>
        <input v-model="keyword" type="search" class="searchInput" placeholder="Search..." @input="onSearch" />
    </div>

</template>
<script setup lang="ts">

import { ref } from 'vue'

const keyword = ref("")

const emit = defineEmits(["search"])

const onSearch = () => {
  emit('search', keyword.value.trim())
}

</script>
<style scoped lang="scss" src="@/assets/scss/components/_search.scss"></style>