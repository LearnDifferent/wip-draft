<template>
  <v-container>

    <MyPageTop
        :user="user"
        :ip="ip"
        @getMyWebsDataByCurrentPage="getMyWebsDataByCurrentPage"
    ></MyPageTop>

    <v-divider></v-divider>

    <div style="text-align: center;margin-top: 1%">
      <v-btn
          class="text-none text-center"
          color="green"
          :outlined="trueMarkedWebsFalseNotifications!==true"
          @click="getMyWebsData(1)"
      >
        <v-icon left>
          mdi-bookmark-check
        </v-icon>
        Bookmarks
      </v-btn>

      <v-divider
          class="mx-2"
          vertical
      ></v-divider>

      <v-btn
          class="text-none text-center"
          color="#ee827c"
          :outlined="trueMarkedWebsFalseNotifications!==false"
          @click="getMyNotifications"
      >
        <v-icon left>
          mdi-at
        </v-icon>
        My Notifications
      </v-btn>

      <v-divider
          class="mx-2"
          vertical
      ></v-divider>

      <v-btn
          class="text-none text-center"
          color="yellow darken-2"
          outlined
          @click="getSystemNotifications"
      >
        <v-icon left>
          mdi-bell-ring-outline
        </v-icon>
        System Notifications
      </v-btn>
    </div>

    <MyPageNotification
        ref="myPageNotification"
        v-show="trueMarkedWebsFalseNotifications===false"
        :current-username="user.userName"
        :total-notifications="totalNotifications"
    ></MyPageNotification>

    <v-container class="mx-auto" v-show="trueMarkedWebsFalseNotifications===true">
      <v-row dense>
        <v-col
            v-for="(item, i) in myWebs"
            :key="i"
            cols="12"
        >
          <v-card :id="item.webId">
            <div class="d-flex flex-no-wrap justify-space-between">
              <div>
                <v-card-title
                    class="headline"
                    v-text="item.title"
                    @click="jump(item.url)"
                ></v-card-title>

                <v-card-subtitle v-text="item.desc"></v-card-subtitle>

                <v-card-actions>

                  <v-chip
                      :color="item.isPublic ? 'green' : 'pink'"
                      :text-color="item.isPublic ? 'green' : 'pink'"
                      outlined
                      @click="changePrivacy(item.webId, item.userName, item.isPublic)"
                  >
                    <v-icon left>{{ item.isPublic ? "mdi-eye" : "mdi-eye-off" }}</v-icon>
                    {{ item.isPublic ? "Public" : "Private" }}
                  </v-chip>

                  <v-btn
                      class="ma-2"
                      outlined
                      color="red"
                      small
                      @click="delWeb(item.webId, i);"
                  >
                    <v-icon>
                      mdi-trash-can-outline
                    </v-icon>
                  </v-btn>

                  <v-btn
                      class="ma-2"
                      outlined
                      color="#bf783a"
                      small
                      @click="jump(item.url)"
                  >
                    <v-icon>
                      mdi-link-variant
                    </v-icon>
                  </v-btn>

                  <v-btn
                      class="ma-2"
                      outlined
                      color="#84a2d4"
                      small
                      @click="openComment(item.webId)"
                  >
                    <v-icon>
                      {{ showComment == item.webId ? 'mdi-comment-remove-outline' : 'mdi-comment-outline' }}
                    </v-icon>
                    {{ item.commentCount > 0 ? item.commentCount : '' }}
                  </v-btn>
                </v-card-actions>
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

          <!-- 评论区 -->
          <div v-show="showComment == item.webId">
            <Comment :webId="showComment"
                     :currentUsername="user.userName"
                     :totalComments="item.commentCount"
            ></Comment>
          </div>
        </v-col>
      </v-row>

      <v-row align-content="center" v-show="totalPage !== 0">
        <v-col>
          <v-pagination
              v-model="currentPage"
              :length="totalPage"
              circle
              @input="getMyWebsData(currentPage)"
          ></v-pagination>
        </v-col>
      </v-row>
    </v-container>
    <v-btn
        fab
        large
        dark
        bottom
        right
        absolute
        @click="toTop"
    >
      <v-icon>mdi-rocket</v-icon>
    </v-btn>

  </v-container>
</template>

<script>
import Comment from "../component/Comment";
import MyPageTop from "../component/MyPageTop";
import MyPageNotification from "../component/MyPageNotification";

export default {
  components: {
    MyPageTop,
    MyPageNotification,
    // 评论区
    Comment
  },
  name: "MyPage",
  data: () => ({
    // 收藏的网页
    myWebs: '',
    // 分页
    currentPage: 1,
    totalPage: 1,

    // 当前用户
    user: '',
    // 当前 IP
    ip: '',
    // response 信息
    status: '',
    // 显示该 webId 的评论
    showComment: -1,

    // true 显示网页数据，false 显示回复的通知
    trueMarkedWebsFalseNotifications: '',
    // 回复的通知的数据
    notificationList: '',
    // 消息提醒的总数
    totalNotifications: 0,
  }),

  methods: {
    // 打开评论
    openComment(webId) {
      if (this.showComment == webId) {
        webId = -1;
      }
      this.showComment = webId;
    },

    // 加载用户信息
    getPersonalInfo() {
      this.axios.get("mypage").then(res => {
        this.user = res.data.user;
        this.ip = res.data.ip;
        this.totalNotifications = res.data.totalReplyNotifications;
      }).catch((error) => {
        if (error.response.data.code === 2005) {
          this.$router.push("/login")
        }
      });
    },

    // 获取回复我的通知
    getMyNotifications() {
      this.trueMarkedWebsFalseNotifications = false;
      this.$refs.myPageNotification.resetDataAndGetNotifications();
    },

    // 加载当前页面的网页
    getMyWebsDataByCurrentPage() {
      this.getMyWebsData(this.currentPage);
    },
    // 加载收藏的网页
    getMyWebsData(currentPage) {
      this.axios.get("/mypage/webs", {
        params: {
          "currentPage": currentPage,
        }
      }).then(res => {
        // total page
        this.totalPage = res.data.totalPages;

        if (this.totalPage < this.currentPage && this.totalPage !== 0) {
          this.currentPage = this.totalPage;
          this.getMyWebsData(this.currentPage);
        }

        // 网页数据
        this.myWebs = res.data.myWebsiteData;

        if (this.myWebs.length === 0) {
          alert("No Bookmarks");
        }

        // 让页面返回顶部
        document.body.scrollTop = 0;
        document.documentElement.scrollTop = 0;
      }).catch((error) => {
        if (error.response.data.code === 2005) {
          this.$router.push("/login")
        }
      }).finally(() => {
        this.trueMarkedWebsFalseNotifications = true;
      });
    },
    // 更新网页数据的隐私设置
    changePrivacy(webId, userName, isPublic) {
      let publicOrPrivate = isPublic ? "private" : "public";
      if (confirm("Are you sure you want to make it "
          + publicOrPrivate + " ?")) {
        this.axios.get("/web", {
          params: {
            "webId": webId,
            "userName": userName
          }
        }).then(res => {
          if (res.data.code === 200 || res.data.code === 500) {
            alert(res.data.msg);
            this.getMyWebsData(this.currentPage);
          } else {
            alert("Something went wrong... Please try again later.")
          }
        }).catch(error => {
          if (error.response.data.code === 2009
              || error.response.data.code === 2001) {
            // 2009 表示没有权限，2001 表示网页不存在
            alert(error.response.data.msg);
          } else {
            alert("Something went wrong... Please try again later.")
          }
        });
      }
    },
    // 删除收藏的网页
    delWeb(webId, arrayIndex) {
      if (confirm("Are you sure you want to delete it?")) {
        this.axios.delete("/web", {
          params: {
            "webId": webId,
            "userName": this.user.userName
          }
        }).then(res => {
          if (res.data.code === 3001) {
            // 3001 表示删除成功
            alert(res.data.msg);
            this.myWebs.splice(arrayIndex, 1);
            if (this.myWebs.length === 0) {
              this.getMyWebsData(this.currentPage);
            }
          } else {
            alert(res.data.msg);
          }
        }).catch(error => {
          if (error.response.data.code === 2009) {
            // 2009 表示没有权限
            alert(error.response.data.msg);
          } else {
            alert("Something went wrong... Please try again later.")
          }
        });
      }
    },
    // 跳转页面
    jump(url) {
      window.open(url, '_blank')
    },
    // 页面回到顶部
    toTop() {
      document.body.scrollTop = 0;
      document.documentElement.scrollTop = 0;
    },


  },

  props:{
    getSystemNotifications: {},
  },

  created() {
    this.getPersonalInfo();
  }
}
</script>

<style scoped>

</style>
