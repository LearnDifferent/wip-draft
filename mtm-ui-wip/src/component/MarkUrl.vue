<template>
  <v-container
      style="border-radius: 25px;
        border: 2px solid grey;
        padding: 20px;">
    <v-text-field
        label="Paste a Website Link (URL) here"
        v-model="newWebUrl"
        @keyup.enter="saveNewWeb"
    ></v-text-field>
    <div>
      <v-switch
          v-model="publicPrivacy"
          color="green"
          :label="publicPrivacy ? 'Public: Anyone on this website': 'Private: Only Me'"
          :prepend-icon="publicPrivacy ? 'mdi-earth' : 'mdi-lock'"
      ></v-switch>
    </div>
    <div v-show="publicPrivacy">
      <v-switch
          v-model="addToSearch"
          color="green"
          :prepend-icon="addToSearch ? 'mdi-magnify' : 'mdi-magnify-minus'"
          :label="addToSearch ? 'Make it searchable' : 'Don\'t make it searchable'"
      ></v-switch>
    </div>
    <div v-show="addToSearch" style="margin-bottom: 1%">
      <a @click="goToSearchPage">
        <v-icon left>mdi-alert-circle-outline</v-icon>
        This Website will be searchable on Search Page
      </a>
    </div>
    <div>
      <v-btn
          color="success"
          class="mr-4 text-none"
          @click="saveNewWeb"
      >
        <v-icon left>
          mdi-plus-box-multiple
        </v-icon>
        Bookmark
      </v-btn>
      <span style="color: slategray" v-show="!saveWebMsg">⬅ Press this button</span>
      <span v-bind:style="saveWebMsgColor" v-show="saveWebMsg">{{ saveWebMsg }}</span>
      <v-progress-linear
          v-show="isLoading"
          color="orange accent-4"
          indeterminate
          rounded
          height="6"
          style="margin-top: 5px"
      ></v-progress-linear>
    </div>
  </v-container>
</template>
<script>
export default {
  name: 'MarkUrl',

  data: () => ({
    // 当前用户
    username: '',
    // 是否在收藏的同时，加入到搜索引擎中
    addToSearch: false,
    // 是否正在解析网页
    isLoading: false,
    // 需要存储的网页链接
    newWebUrl: '',
    // 是否将网页公开
    publicPrivacy: true,
    // 存储网页链接后返回的信息
    saveWebMsg: '',
    // 返回信息的颜色
    saveWebMsgColor: 'color: orange',
  }),

  props: {
    username: {
      type: String,
      required: true
    }
  },

  methods: {
    // 跳转到搜索页面
    goToSearchPage() {
      document.getElementById("myFindBtn").click();
    },

    // 点击 mark 按钮，根据URL保存新的网页
    saveNewWeb() {
      this.saveWebMsg = "Saving this Web Page. Please wait......";
      this.isLoading = true;

      let data = {
        url: this.newWebUrl,
        username: this.username,
        syncToElasticsearch: this.addToSearch,
        isPublic: this.publicPrivacy
      };

      this.axios.post("/web/new", data).then(res => {

            let hasSavedToDatabase = res.data.data.hasSavedToDatabase;
            let hasSavedToElasticsearch = res.data.data.hasSavedToElasticsearch;

            if (hasSavedToElasticsearch === null) {
              // 无需同步到 Elasticsearch 的情况
              if (hasSavedToDatabase === true) {
                this.saveWebMsg = "Success!";
                this.saveWebMsgColor = 'color: green';
                this.$emit("showRefresh");
              } else {
                this.saveWebMsg = "Fail to save this website.";
                this.saveWebMsgColor = 'color: red';
              }
            } else {
              // 需要同步到数据库的情况
              if (hasSavedToDatabase === true && hasSavedToElasticsearch === true) {
                this.saveWebMsg = "Success!";
                this.saveWebMsgColor = 'color: green';
                this.$emit("showRefresh");
              } else if (hasSavedToDatabase === true && hasSavedToElasticsearch === false) {
                this.saveWebMsg = "Saved successfully, but fail to make it searchable";
                this.saveWebMsgColor = 'color: orange';
                this.$emit("showRefresh");
              } else {
                // 只要没有存入数据库，就当作失败处理
                this.saveWebMsg = "Fail to save this website.";
                this.saveWebMsgColor = 'color: red';
              }
            }

          }
      ).catch(error => {
        this.saveWebMsg = error.response.data.msg;
        this.saveWebMsgColor = 'color: red';
      }).finally(() => {
        this.isLoading = false;
      });

      this.newWebUrl = '';
    },
  },
}
</script>
