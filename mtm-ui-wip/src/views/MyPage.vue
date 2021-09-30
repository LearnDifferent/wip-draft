<template>
  <v-container>

    <template>
      <v-row no-gutters>
        <v-col order="first">
          <v-card
              class="pa-2"
              outlined
              style="margin-bottom: 2%;margin-right: 1%"
          >
            <v-list-item three-line>
              <v-list-item-content>

                <v-list-item-title class="headline mb-1">
                  <v-textarea
                      label="Username"
                      no-resize
                      readonly
                      rows="1"
                      :value="user.userName"
                  ></v-textarea>
                </v-list-item-title>

                <v-list-item-title class="headline mb-1">
                  <v-textarea
                      label="ID"
                      no-resize
                      readonly
                      rows="1"
                      :value="user.userId"
                  ></v-textarea>
                </v-list-item-title>

                <v-list-item-title class="headline mb-1">
                  <v-textarea
                      label="Signed in"
                      no-resize
                      readonly
                      rows="1"
                      :value="user.createTime"
                  ></v-textarea>
                </v-list-item-title>

                <v-list-item-title class="headline mb-1">
                  <v-textarea
                      label="Role"
                      no-resize
                      readonly
                      rows="1"
                      :value="user.role"
                  ></v-textarea>
                </v-list-item-title>

                <v-list-item-title class="headline mb-1">
                  <v-textarea
                      label="Your IP"
                      no-resize
                      readonly
                      rows="1"
                      :value="ip"
                  ></v-textarea>
                </v-list-item-title>

              </v-list-item-content>

              <v-list-item-avatar
                  size="60"
                  color="black lighten-1"
              >
                <span class="white--text headline">{{ firstOfName }}</span>
              </v-list-item-avatar>
            </v-list-item>

            <v-card-actions>
              <v-row no-gutters>
                <v-col order="first">
                  <v-btn
                      class="text-none"
                      outlined
                      rounded
                      text
                      color="pink"
                      @click="logout"
                  >
                    <v-icon left>
                      mdi-logout
                    </v-icon>
                    Sign Out
                  </v-btn>
                </v-col>
                <v-col>
                  <v-btn
                      class="text-none"
                      outlined
                      rounded
                      text
                      color="red"
                      @click="deleteAccount"
                  >
                    <v-icon left>
                      mdi-delete-outline
                    </v-icon>
                    Delete My Account
                  </v-btn>
                </v-col>
                <v-col order="last">
                  <v-btn
                      class="text-none"
                      outlined
                      rounded
                      text
                      color="orange"
                      @click="showChangePwd = !showChangePwd"
                  >
                    Change Password
                    <v-icon right>
                      {{ showChangePwd ? 'mdi-arrow-right-bold-outline' : 'mdi-lock-plus-outline' }}
                    </v-icon>
                  </v-btn>
                </v-col>
              </v-row>
            </v-card-actions>
          </v-card>
        </v-col>

        <v-col order="last" v-show="!showChangePwd">
          <v-card color="#93deff">
            <v-card-title class="text-h5">
              Export Websites Data
            </v-card-title>
            <v-card-subtitle>Press the EXPORT button to export your websites data to HTML file and DOWNLOAD it.
            </v-card-subtitle>
            <v-card-actions>
              <v-btn color="#b6d7de" @click="exportHtmlFile">
                <v-icon left>
                  mdi-export
                </v-icon>
                <span style="color: #323643">EXPORT</span>
              </v-btn>
            </v-card-actions>
          </v-card>

          <v-card color="#b6d7de" style="margin-top: 2%">
            <v-card-title class="text-h5">
              Import Websites Data
            </v-card-title>
            <v-card-subtitle>
              To import websites data from HTML file:
              <p></p>
              <p>1. Upload HTML file:</p>
              <v-file-input
                  accept="text/html"
                  label="Please Upload HTML file"
                  v-model="chosenFile"
              >
              </v-file-input>
              <p>2. Press the IMPORT button:</p>
              <v-btn color="#93deff" @click="importHtmlFile" :disabled="importingFile">
                <v-icon left>
                  mdi-import
                </v-icon>
                <span style="color: #323643">IMPORT</span>
              </v-btn>
              <span v-html="importingMsg" style="margin-left: 2%"></span>
            </v-card-subtitle>
          </v-card>
        </v-col>

        <v-col order="last" v-show="showChangePwd">
          <v-card
              outlined
              style="margin-top: 2%;margin-bottom: 2%"
          >
            <v-form
                ref="form"
                v-model="valid"
                lazy-validation
            >
              <div style="margin-left: 20px;margin-right: 20px;margin-top: 10px">
                <v-text-field
                    v-model="user.userName"
                    label="Username"
                    disabled
                ></v-text-field>
              </div>
              <div style="margin-left: 20px;margin-right: 20px">
                <v-text-field
                    v-model="oldPassword"
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    :rules="pwdRules"
                    name="input-10-1"
                    label="Old password"
                    hint="At least 8 characters"
                    :loading="isLoading"
                    counter
                    @click:append="show1 = !show1"
                ></v-text-field>
              </div>

              <div style="margin-left: 20px;margin-right: 20px">
                <v-text-field
                    v-model="newPassword"
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    :rules="pwdRules"
                    name="input-10-1"
                    label="New password"
                    hint="At least 8 characters"
                    counter
                    :loading="isLoading"
                    @click:append="show1 = !show1"
                ></v-text-field>
              </div>

              <div v-show="status" style="text-align:center;margin-left: 20px;margin-right: 20px;margin-bottom: 3px">
                <v-alert
                    v-model="alert"
                    close-text="Close Alert"
                    color="#8E2323"
                    outlined
                    dense
                >
                  {{ status }}
                </v-alert>
              </div>

              <div style="text-align:center;margin-bottom: 10px">
                <v-btn
                    :disabled="!valid"
                    color="success"
                    class="mr-4"
                    @click="validate"
                >
                  Update Password
                </v-btn>
              </div>
            </v-form>
          </v-card>
        </v-col>

      </v-row>
    </template>

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
                      @click="delWeb(item.webId);"
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
            <Comment :webId="showComment" :currentUsername="user.userName"></Comment>
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

export default {
  components: {
    // 评论区
    Comment: Comment
  },
  name: "MyPage",
  data: () => ({
    // 是否正在处理上传的文件
    importingFile: false,
    // 上传文件时显示的内容
    importingMsg: '',
    // 需要上传的 HTML 文件
    chosenFile: null,
    // 修改密码的进度提示
    isLoading: false,
    // 头像字母
    firstOfName: '',
    // 是否显示修改密码的框
    showChangePwd: false,
    // 收藏的网页
    myWebs: '',
    // 分页
    currentPage: 1,
    totalPage: 1,
    // 验证表单
    valid: true,
    // 当前用户
    user: '',
    // 当前 IP
    ip: '',
    // 密码相关：
    show1: false,
    oldPassword: '',
    newPassword: '',
    pwdRules: [
      v => !!v || 'Password is required',
      v => (v && v.length >= 8) || 'Password must be greater than 8 characters',
    ],
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
    // 导入数据
    importHtmlFile() {
      if (!this.chosenFile) {
        alert("Please Upload HTML File.");
      } else {
        this.importingFile = true;
        this.importingMsg = "<span style='color: #004a7c; font-size: 18px'>Importing... Please wait a minute.</span>"

        let data = new FormData();
        data.append("htmlFile", this.chosenFile);
        this.axios.post('/file', data, {
          headers: {'Content-Type': 'multipart/form-data'}
        }).then(res => {
          // 注意，这里的提示信息是存在 res.data.data 里面的，而不是 res.data.msg
          let msg = res.data.data;
          if (200 == res.data.code) {
            this.importingMsg = "<span style='color: #fff5a5; font-size: 18px'>" + msg + "</span>";
            this.loadMyPage(this.currentPage);
          } else if (500 == res.data.code) {
            this.importingMsg = "<span style='color: red; font-size: 15px'>" + msg + "</span>";
          } else {
            this.importingMsg = msg;
          }
        }).catch((error) => {
          this.importingFile = "<span style='color: red; font-size: 15px'>" +
              error.response.data.msg +
              "</span>";
        }).finally(() => {
          this.importingFile = false;
        });
      }
    },
    // 导出数据
    exportHtmlFile() {
      let username = this.user.userName;
      // 打开 3000 端口的 /file 路径，而 3000 号端口映射了后端服务器的端口
      window.open("/file?username=" + username, "_blank");
    },
    // 退出登陆
    logout() {
      if (confirm("Are you sure you want to Sign Out?")) {
        let userName = this.user.userName;

        this.axios.get("/log/out").then(res => {
          if (res.data.code === 200) {
            alert("Good Bye " + userName)
            this.$router.push("/login");
          }
        });
      }
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
    delWeb(webId) {
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
            this.loadMyPage(this.currentPage);
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
    // 删除用户确认框
    deleteAccount() {
      if (confirm("Once you delete your account, there is no going back. Please be certain.\n\n"
          + "Are you absolutely sure?")) {
        let username = this.user.userName;
        this.$router.push({
          path: `/delete/${username}`,
        })
      }
    },
    // 验证登陆信息
    validate() {
      this.$refs.form.validate();
      this.isLoading = true;
      let submitData = {
        userName: this.user.userName,
        oldPassword: this.oldPassword,
        newPassword: this.newPassword
      }
      this.axios.post("/user/changePwd", submitData).then(res => {
        alert(res.data.msg);
        this.oldPassword = '';
        this.newPassword = '';
        this.showChangePwd = false;
        // 成功修改密码会返回 2014
        if (res.data.code == 2014) {
          this.axios.get("/log/out").then(res => {
            if (res.data.code === 200) {
              this.$router.push("/login");
              alert("Please Login again with the new password.");
            }
          });
        }
      }).catch(error => {
        if (error.response.data.code === 3002) {
          // 3002 表示密码错误
          alert(error.response.data.msg);
        }
        if (error.response.data.code === 2009) {
          // 2009 表示没有权限，这里指 Guest 用户无法修改密码
          alert("Guest don't have permission to change password");
        }
      }).finally(() => {
        this.isLoading = false
      });
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
