<template>
  <v-container>

    <MyPageTop :user="user" :ip="ip" :first-of-name="firstOfName"></MyPageTop>

    <v-divider></v-divider>

    <v-container class="mx-auto">
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
                      color="green"
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
              @input="loadMyPage(currentPage)"
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

export default {
  components: {
    MyPageTop,
    // 评论区
    Comment: Comment
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
    // 头像字母
    firstOfName: '',

    // 信息
    status: '',
    // 显示该 webId 的评论
    showComment: -1,
  }),

  methods: {
    // 打开评论
    openComment(webId) {
      if (this.showComment == webId) {
        webId = -1;
      }
      this.showComment = webId;
    },

    // 加载页面信息
    loadMyPage(currentPage) {
      this.axios.get("/mypage", {
        params: {
          "currentPage": currentPage,
        }
      }).then(res => {
        this.totalPage = res.data.data.totalPage;
        if (this.totalPage < this.currentPage && this.totalPage !== 0) {
          this.currentPage = this.totalPage;
          this.loadMyPage(this.currentPage);
        }

        this.user = res.data.data.user;
        this.myWebs = res.data.data.myWebs;
        this.firstOfName = res.data.data.firstCharOfName;
        this.ip = res.data.data.ip

        // 让页面返回顶部
        document.body.scrollTop = 0;
        document.documentElement.scrollTop = 0;
      }).catch((error) => {
        if (error.response.data.code === 2005) {
          this.$router.push("/login")
        }
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
            this.loadMyPage(this.currentPage);
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
      if (confirm("Are you sure you want to delete this one?")) {
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
              this.loadMyPage(this.currentPage);
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

  created() {
    window.onload = function () {
      document.getElementById("myPageBtn").click();
    }
    this.loadMyPage();
  }
}
</script>

<style scoped>

</style>
