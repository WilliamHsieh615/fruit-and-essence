<template>

    <TermsInput :showPrivacy="false" :showTerms="false" v-model:subscribe="profileData.subscribe" />

</template>
<script setup lang="ts">

import TermsInput from '~/components/ui/input/TermsInput.vue';

</script>