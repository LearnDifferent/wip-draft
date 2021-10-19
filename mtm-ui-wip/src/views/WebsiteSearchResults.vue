<template>
  <v-card>
    <div class="d-flex flex-no-wrap justify-space-between">
      <div>
        <v-card-title
            class="headline"
            v-html="item.title"
            @click="jump(item.url)"
        ></v-card-title>

        <v-card-subtitle v-html="item.desc" @click="jump(item.url)"></v-card-subtitle>
      </div>

      <v-avatar
          class="ma-3"
          size="125"
          tile
          @click="jump(item.url)"
      >
        <v-img :src="item.img"></v-img>
      </v-avatar>
    </div>
  </v-card>
</template>
<script>
export default {
  name: 'WebsiteSearchResults',

  methods: {
    // 跳转页面
    jump(url) {
      window.open(url, '_blank')
    },
  },

  props: {
    item: {},
  }
}
</script>