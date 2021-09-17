<template>
  <v-app>
    <v-app-bar
        app
        dense
        color="dark"
        dark
    >

      <div class="d-flex align-lg-content-center">
        <a href="https://github.com/LearnDifferent/mtm" target="_blank"
           style="color: whitesmoke; text-decoration:none;">
          <span><v-icon> mdi-github </v-icon> GitHub </span>
        </a>
      </div>

      <v-spacer></v-spacer>
      <div class="d-flex justify-center">
        <v-btn-toggle
            v-model="toggle_exclusive"
            mandatory
        >
          <v-btn icon id="myHomeBtn" depressed large @click="changePage('/home')">
            <v-icon>mdi-home</v-icon>
          </v-btn>

          <v-btn icon id="myFindBtn" depressed large @click="changePage('/find')">
            <v-icon>mdi-magnify</v-icon>
          </v-btn>
          <v-btn icon id="myPageBtn" depressed large @click="changePage('/mypage')">
            <v-icon>mdi-account</v-icon>
          </v-btn>

          <v-btn icon id="myAdminBtn" depressed large @click="changePage('/admin')">
            <v-icon>mdi-head-cog</v-icon>
          </v-btn>

          <v-btn icon id="myLogoutBtn" depressed large @click="logoutNow()">
            <v-icon color="red darken-2">mdi-logout</v-icon>
          </v-btn>
        </v-btn-toggle>
      </div>

      <!--      <div class="d-flex order-last">-->
      <!--        <v-avatar color="grey lighten-2" max-width="36" max-height="36" size="34">-->
      <!--          <span class="black&#45;&#45;text headline">{{ firstName }}</span>-->
      <!--        </v-avatar>-->
      <!--      </div>-->

      <!--      <div class="d-flex order-last">-->
      <!--        <v-img-->
      <!--            alt="Logo"-->
      <!--            class="shrink mr-2"-->
      <!--            contain-->
      <!--            src="./assets/logo.png"-->
      <!--            transition="scale-transition"-->
      <!--            width="40"-->
      <!--        />-->
      <!--      </div>-->


      <div class="text-center d-flex order-last">
        <v-btn
            id="infoBtn"
            dark
            icon
            small
            color="yellow darken-2"
            @click="showNotice"
        >
          <v-icon>
            mdi-bell-ring
          </v-icon>
        </v-btn>
      </div>

      <v-spacer></v-spacer>
    </v-app-bar>
    <v-main>
      <router-view/>
      <v-snackbar
          v-model="snackbar"
          vertical="true"
      >

        <span v-html="notice"></span>

        <template v-slot:action="{ attrs }">
          <v-btn
              color="red"
              text
              v-bind="attrs"
              @click="snackbar = false"
          >
            Close
          </v-btn>
        </template>
      </v-snackbar>
    </v-main>
  </v-app>
</template>

<script>

export default {
  name: 'App',

  components: {},

  data: () => ({
    value: 1,
    toggle_exclusive: undefined,
    // 通知相关
    snackbar: false,
    notice: '',
  }),
  methods: {
    // 查看通知按钮
    showNotice() {
      this.getNotify();
      this.snackbar = true;
    },
    // 获取通知
    getNotify() {
      this.axios.get("/notify").then(res => {
        this.notice = res.data.data;
      });
    },
    // 切换页面
    changePage(page) {
      this.$router.push({path: page})
    },
    // 退出登陆
    logoutNow() {
      if (confirm("Are you sure you want to Sign Out?")) {
        this.axios.get("/log/out").then(res => {
          if (res.data.code === 200) {
            alert("Good Bye")
            this.$router.push("/login");
          }
        });
      }
    }
  },
  created() {
    this.getNotify()
  }
};
</script>

<style scoped>

</style>
