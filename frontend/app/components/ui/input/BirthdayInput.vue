<template>

    <div class="birthdayInput">
        <label for="birthday">Birthday</label>
        <div class="birthday">
            <input id="birthday" placeholder="Select your birthday" type="text" :value="birthday"
                @input="emit('update:birthday', $event.target.value)" />
            <FontAwesomeIcon icon="cake-candles" class="inputIcon" />
        </div>
    </div>

</template>

<script setup lang="ts">

import { onMounted } from 'vue';
import flatpickr from 'flatpickr';
import 'flatpickr/dist/flatpickr.css';

const props = defineProps({
    birthday: {
        type: String,
        required: true
    }
});
const emit = defineEmits(['update:birthday']);

onMounted(() => {
    flatpickr('#birthday', {
        dateFormat: 'Y/m/d',
        maxDate: 'today',
        allowInput: true,
        disableMobile: true,
        onChange: (_, dateStr) => {
            emit('update:birthday', dateStr);
        }
    })
})

</script>

<style scoped lang="scss" src="@/assets/scss/components/_inputs.scss"></style>