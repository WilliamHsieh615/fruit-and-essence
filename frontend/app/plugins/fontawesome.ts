import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faEnvelope, faLock, faEye, faEyeSlash, faCircleExclamation, faCircleCheck, faCircleXmark, faUser, faPhone, faCakeCandles } from '@fortawesome/free-solid-svg-icons'

library.add(faEnvelope, faLock, faEye, faEyeSlash, faCircleExclamation, faCircleCheck, faCircleXmark, faUser, faPhone, faCakeCandles)

export default defineNuxtPlugin((nuxtApp) => {
    nuxtApp.vueApp.component('FontAwesomeIcon', FontAwesomeIcon)
})
