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
          :label="addToSearch ? 'Mark & Make it searchable' : 'Mark Only'"
      ></v-switch>
    </div>
    <div v-show="addToSearch" style="margin-bottom: 1%">
      <a @click="goToSearchPage">
        <v-icon left>mdi-alert-circle-outline</v-icon>
        Go to search page</a>
    </div>
    <div>
      <v-btn
          color="success"
          class="mr-4"
          @click="saveNewWeb"
      >
        <v-icon left>
          mdi-plus-box-multiple
        </v-icon>
        mark
      </v-btn>
      <span style="color: slategray" v-show="!saveWebMsg">⬅ Press the MARK button</span>
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

  data:()=>({
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

  methods:{
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
            console.log(res.data);
            let toDatabase = res.data[0];
            let toElasticsearch = res.data[1];

            if (toDatabase == false) {
              this.saveWebMsg = "Fail to save this website.";
              this.saveWebMsgColor = 'color: red';
            }

            if (toDatabase == true && toElasticsearch == true) {
              this.saveWebMsg = "Success!";
              this.saveWebMsgColor = 'color: green';
              this.$emit("showRefresh");
            }

            if (toDatabase == true && toElasticsearch == false) {
              this.saveWebMsg = "Save successfully, but fail to make it searchable";
              this.saveWebMsgColor = 'color: orange';
              this.$emit("showRefresh");
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
