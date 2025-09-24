// nuxt.config.ts
import { defineNuxtConfig } from 'nuxt/config'

export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  // 連接後端 API 測試使用
  vite: {
    server: {
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    }
  },

  // 全域 CSS（Material Symbols icon）
  css: [
    '~/assets/scss/main.scss',
    'bootstrap-icons/font/bootstrap-icons.css',
    'material-symbols/outlined.css' // 可換成 rounded.css 或 sharp.css
  ],

  modules: [
    '@nuxt/eslint',
    '@nuxt/fonts',
    '@nuxt/icon',
    '@nuxt/image'
  ],

  app: {
    head: {
      meta: [
        { charset: 'UTF-8' },
        { 'http-equiv': 'Content-language', content: 'en' },
        { 'http-equiv': 'X-UA-Compatible', content: 'IE=edge' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no' },
        { name: 'author', content: 'William Hsieh' },
        { name: 'copyright', content: 'Fruit & Essence' },
        { name: 'description', content: 'Fruit & Essence, USA Juice, Cold-Pressed Juice, Fresh Juice, Organic Juice, Healthy Drinks, Natural Juice, Premium Juice Brand' },
        { name: 'keywords', content: 'Fruit & Essence, USA Juice, Cold-Pressed Juice, Fresh Juice, Organic Juice, Healthy Drinks, Natural Juice, Premium Juice Brand' },
        { property: 'og:title', content: 'Fruit & Essence' },
        { property: 'og:description', content: 'Pure. Fresh. Delicious. Fruit & Essence delivers premium cold-pressed juices made from the finest fruits in the USA.' },
        { property: 'og:type', content: 'website' },
        { property: 'og:url', content: 'https://github.com/WilliamHsieh615' },
        { property: 'og:image', content: '/images/logo/logo.png' },
        { property: 'og:site_name', content: 'Fruit & Essence' },
        { property: 'og:locale', content: 'en_US' },
        { property: 'og:image:width', content: '512' },
        { property: 'og:image:height', content: '512' },
        { property: 'og:image:alt', content: 'Fruit & Essence' },
        { name: 'twitter:card', content: 'summary_large_image' },
        { name: 'twitter:title', content: 'Fruit & Essence - Premium Cold-Pressed Juice' },
        { name: 'twitter:description', content: 'Discover the freshness of American fruits with Fruit & Essence. Pure, organic, and crafted into delicious cold-pressed juices.' },
        { name: 'twitter:image', content: '/images/logo/logo.png' }
      ],
      link: [
        { rel: 'icon', href: '/images/logo/logo.ico', type: 'image/x-icon' },
        {
          rel: 'stylesheet',
          href: 'https://fonts.googleapis.com/css2?family=Staatliches&family=Anton&family=Bebas+Neue&family=Russo+One&family=Noto+Sans+TC&family=Oswald&family=Roboto&display=swap'
        }
      ]
    }
  }
})
