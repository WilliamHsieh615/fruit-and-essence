<template>

    <div class="pagination">
        <button :disabled="currentPage === 1" @click="prevPage">
            <i class="bi bi-caret-left-fill"></i>
        </button>

        <button v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }"
            @click="goToPage(page)">
            {{ page }}
        </button>

        <button :disabled="currentPage === totalPages" @click="nextPage">
            <i class="bi bi-caret-right-fill"></i>
        </button>
    </div>

</template>
<script setup lang="ts">

const props = defineProps({
    currentPage: {
        type: Number,
        required: true
    },
    totalPages: {
        type: Number,
        required: true
    }
})

const emit = defineEmits(['update:page'])

const scrollTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
}


const prevPage = () => {
    if (props.currentPage > 1) {
        emit('update:page', props.currentPage - 1)
        scrollTop()
    }
}

const nextPage = () => {
    if (props.currentPage < props.totalPages) {
        emit('update:page', props.currentPage + 1)
        scrollTop()
    }
}

const goToPage = (page: number) => {
    emit('update:page', page)
    scrollTop()
}

</script>
<style scoped lang="scss" src="@/assets/scss/components/_pagination.scss"></style>