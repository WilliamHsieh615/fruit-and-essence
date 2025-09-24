<script setup lang="ts">
const router = useRouter()

onMounted(() => {
    router.replace({
        path: '/products'
    })
})
</script>