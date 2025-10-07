<template>
    <form class="profile-form" @submit.prevent="profile">

        <h2>PROFILE</h2>

        <NameInput v-model:email="profileData.name" />

        <PhoneInput />

        <BirthdayInput />

        <button type="button">修改</button>

        <button type="submit">儲存</button>

    </form>
</template>
<script setup lang="ts">

import { reactive } from 'vue';

import BirthdayInput from '~/components/ui/input/BirthdayInput.vue';
import NameInput from '~/components/ui/input/NameInput.vue';
import PhoneInput from '~/components/ui/input/PhoneInput.vue';

const profileData = reactive({
    name: '',
    phone: '',
    birthday: '',
    subscribe: false
});

</script>
<style scoped lang="scss" src="@/assets/scss/layout/_form.scss"></style>