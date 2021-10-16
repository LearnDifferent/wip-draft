<template>
  <v-container>

    <!--    <v-alert-->
    <!--        v-show="isAdmin===false"-->
    <!--        class="mx-auto"-->
    <!--        outlined-->
    <!--        color="grey"-->
    <!--        max-width="60%"-->
    <!--    >-->
    <!--      <div class="title">-->
    <!--        普通用户可以做什么？-->
    <!--      </div>-->
    <!--      <div>发送通知（查看通知的按钮在右上角）</div>-->
    <!--      <div class="title">-->
    <!--        管理员可以做什么？-->
    <!--      </div>-->
    <!--      <div>查看所有用户和系统日志</div>-->
    <!--      <div class="title">-->
    <!--        怎么注册管理员？-->
    <!--      </div>-->
    <!--      <div>如果使用非管理员账号查看此页面，下方会出现注册窗口</div>-->
    <!--      <div>*邀请码为<span style="color: black"> 1234</span>（发送邀请码的功能请参照 GitHub 上的源码）</div>-->
    <!--    </v-alert>-->


    <v-expansion-panels style="margin-top: 1%">

      <v-expansion-panel>
        <v-expansion-panel-header>
          <h3>Send System Notifications</h3>
        </v-expansion-panel-header>
        <v-expansion-panel-content style="font-size: small">
          Note: The limit for system notifications is 20
        </v-expansion-panel-content>
        <v-expansion-panel-content>
          <v-textarea
              name="input-7-1"
              filled
              color="grey"
              label="Notification Editor"
              auto-grow
              v-model="noticeCon"
              @keyup.enter="toSendNotify"
          ></v-textarea>
          <v-btn
              color="success"
              class="mr-4 text-none"
              @click="sendNotify"
          >
            <v-icon left>
              mdi-send
            </v-icon>
            Send
          </v-btn>
          <v-btn
              class="mr-4 text-none"
              @click="clickInfo"
          >
            <v-icon left>
              mdi-email-open-outline
            </v-icon>
            View System Notifications
          </v-btn>
          <v-btn
              color="error"
              class="mr-4 text-none"
              @click="delNotify"
          >
            <v-icon left>
              mdi-delete
            </v-icon>
            Delete All System Notifications
          </v-btn>
        </v-expansion-panel-content>
      </v-expansion-panel>


      <v-expansion-panel>
        <v-expansion-panel-header>
          <h3>List All User {{ isAdmin ? "" : " (Please Login as Admin)" }}</h3>
        </v-expansion-panel-header>
        <v-expansion-panel-content style="font-size: small" v-if="isAdmin===true">
          Cache will be refreshed every hour on the hour automatically, or you can
          <v-btn class="text-none" color="#93ca76" @click="refreshAllUsers" x-small>
            <v-icon left x-small>
              mdi-refresh
            </v-icon>
            Refresh Cache Now
          </v-btn>
        </v-expansion-panel-content>
        <v-expansion-panel-content v-if="isAdmin===true">
          <v-card>
            <v-card-title>
              Users
              <v-spacer></v-spacer>
              <v-spacer></v-spacer>
              <v-text-field
                  v-model="search"
                  append-icon="mdi-magnify"
                  label="Search"
                  single-line
                  hide-details
              ></v-text-field>
            </v-card-title>
            <v-data-table
                :headers="headers"
                :items="users"
                :search="search"
            ></v-data-table>
          </v-card>
        </v-expansion-panel-content>
        <v-expansion-panel-content v-if="isAdmin===false">
          Please Login as Admin
        </v-expansion-panel-content>
      </v-expansion-panel>

      <v-expansion-panel>
        <v-expansion-panel-header>
          <h3>View Logs {{ isAdmin ? "" : " (Please Login as Admin)" }}</h3>
        </v-expansion-panel-header>
        <v-expansion-panel-content v-if="isAdmin===true">
          <v-card>
            <v-card-title>
              Logs
              <v-spacer></v-spacer>
              <v-text-field
                  v-model="sysSearch"
                  append-icon="mdi-magnify"
                  label="Search"
                  single-line
                  hide-details
              ></v-text-field>
            </v-card-title>
            <v-data-table
                :headers="sysHeaders"
                :items="logs"
                :search="sysSearch"
            ></v-data-table>
          </v-card>
        </v-expansion-panel-content>
        <v-expansion-panel-content v-if="isAdmin===false">
          Please Login as Admin
        </v-expansion-panel-content>
      </v-expansion-panel>

    </v-expansion-panels>

    <v-card
        style="margin-top: 1%"
        class="mx-auto"
        max-width="50%"
        outlined
        v-if="isAdmin===false"
        :loading="isLoading"
    >
      <v-form
          ref="form"
          v-model="valid"
          lazy-validation
      >
        <div style="margin-left: 20px;margin-right: 20px;margin-top: 10px">
          <v-text-field
              v-model="name"
              :counter="30"
              :rules="nameRules"
              label="Username"
              required
              :loading="isLoading"
              @keyup.enter="validate"
          ></v-text-field>
        </div>
        <div style="margin-left: 20px;margin-right: 20px">
          <v-text-field
              v-model="password"
              :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
              :type="show1 ? 'text' : 'password'"
              :rules="pwdRules"
              name="input-10-1"
              label="Password"
              hint="At least 8 characters"
              counter
              :loading="isLoading"
              @click:append="show1 = !show1"
              @keyup.enter="validate"
          ></v-text-field>
        </div>
        <div style="margin-left: 20px;margin-right: 20px">
          <v-text-field
              v-model="adminCode"
              label="Invitation Code"
              :rules="adminRule"
          >
            <template v-slot:append>
              <template>
                <v-row justify="center" style="margin-right: 1px">
                  <v-dialog
                      v-model="dialog"
                      persistent
                      max-width="600px"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                          class="text-none"
                          color="grey darken-1"
                          dark
                          v-bind="attrs"
                          v-on="on"
                      >
                        Get Code
                      </v-btn>
                    </template>
                    <v-card>
                      <v-card-title>
                        <span class="headline">Send invitation code to Email</span>
                      </v-card-title>
                      <v-card-text>
                        <v-container>
                          <v-row>
                            <v-col>
                              <v-text-field
                                  label="Email"
                                  v-model="email"
                              ></v-text-field>
                            </v-col>
                          </v-row>
                        </v-container>
                        <div>
                          <span>
                            <b>
                            *Click the <a @click="sendEmail">SEND</a> button and you will get the invitation code.
                            </b>
                          </span>
                          <br><br>
                          <span style="color: grey">
                            *There are limits for sending too many emails, so I don't set up email account to send the invitation code.
                          </span>
                          <br>
                          <span style="color: grey">
                            *That means <b>you will not get any email because I didn't set the mail sending function up</b>.
                          </span>
                          <br><br>
                          <span style="color: grey">
                            *If you want to know how this application send invitation code via email, checkout the
                            <a href="https://github.com/LearnDifferent/mtm/blob/master/src/main/java/com/github/learndifferent/mtm/manager/InvitationCodeManager.java"
                               target="_blank">source code on GitHub</a>.
                          </span>
                          <br>
                          <span style="color: grey">
                            *If you are a developer, you can clone this
                            <a href="https://github.com/LearnDifferent/mtm" target="_blank">
                              repository</a> , set up your own email account and run the project to test the mail sending function.
                          </span>
                        </div>
                      </v-card-text>
                      <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn
                            color="red"
                            text
                            @click="dialog = false"
                        >
                          <span style="font-weight: lighter">Close</span>
                        </v-btn>
                        <v-btn
                            color="grey darken-1"
                            text
                            @click="sendEmail"
                        >
                          <span style="font-weight: bolder"><b>Send</b></span>
                        </v-btn>
                      </v-card-actions>
                    </v-card>
                  </v-dialog>
                </v-row>
              </template>
            </template>
          </v-text-field>
        </div>
        <div style="margin-left: 20px;margin-right: 20px">
          <v-text-field
              v-model="code"
              :rules="verification"
              label="Verification Code"
              required
              :loading="isLoading"
              @keyup.enter="validate"
          >
            <template v-slot:append>
              <a href="javascript:void(0);" @click="getVCode">
                <v-fade-transition leave-absolute>
                  <v-progress-circular
                      v-if="loading"
                      size="24"
                      color="info"
                      indeterminate
                  ></v-progress-circular>
                  <img
                      v-else
                      width="80px"
                      height="30px"
                      :src="verCode"
                      alt="verification code"
                      style="margin-bottom: 3px"
                  >
                </v-fade-transition>
              </a>
            </template>
          </v-text-field>
        </div>
        <br>
        <div v-show="status"
             style="text-align:center;margin-left: 20px;margin-right: 20px;margin-bottom: 3px;margin-top: 3px">
          <v-alert
              v-model="alert"
              close-text="Close Alert"
              v-bind:color="alertColor"
              outlined
              dense
          >
            <span v-html="status"></span>
          </v-alert>
          <v-chip
              class="ma-2"
              color="red"
              outlined
              pill
              @click="clickToLogout"
              v-show="showClickToLogout"
          >
            <v-icon left>
              mdi-account-outline
            </v-icon>
            You can click here to Log Out Current Account
          </v-chip>
        </div>

        <div style="text-align:center;margin-bottom: 10px">
          <v-btn
              :disabled="!valid || isLoading"
              color="success"
              class="mr-4 text-none"
              @click="validate"
              v-show="!showClickToLogout"
          >
            Create new Admin account
          </v-btn>
        </div>

      </v-form>
      <v-progress-linear
          v-show="isLoading"
          color="primary"
          indeterminate
          rounded
          height="6"
          style="margin-top: 4px"
      ></v-progress-linear>
    </v-card>

  </v-container>

</template>

<script>

export default {
  name: "Admin",
  data: () => ({
    // 通知相关
    noticeCon: '',
    // 弹窗相关
    dialog: false,
    // 管理员相关
    isAdmin: false,
    // Invitation Code
    invitationCodeValue: '',
    adminRule: [v => !!v || "Click 'Get Code' button to get your invitation code"],
    adminCode: '',
    // 邮箱
    email: '',
    // emailRules: [
    //   v => !!v || 'E-mail is required',
    //   v => /.+@.+\..+/.test(v) || 'E-mail must be valid',
    // ],
    // 注册提示条
    isLoading: false,
    // 验证
    valid: true,
    // 用户名相关：
    name: '',
    nameRules: [
      v => !!v || 'Username is required',
      v => (v && v.length <= 30) || 'Username must be less than 30 characters',
      v => /^[a-z0-9A-Z]+$/.test(v) || 'Username must contain ONLY letters and numbers',
    ],
    // 密码相关：
    show1: false,
    password: '', //默认密码
    pwdRules: [
      v => !!v || 'Password is required',
      v => (v && v.length >= 8) || 'Password must be greater than 8 characters',
      v => v.length <= 50 || 'Password must be less than 50 characters',
    ],
    // 验证码相关：
    verification: [
      v => !!v || 'Enter the verification code shown in the image',
      v => (v && v.length === 4) || 'Enter 4 characters verification code',
    ],
    // 获取输入的验证码
    code: '',
    // 图片
    verCode: '',
    // checkbox: false,
    // 信息
    status: '',
    success: '',
    alertColor: '#8E2323',
    // 系统记录相关
    sysSearch: '',
    sysHeaders: [
      {
        text: 'Time',
        align: 'start',
        value: 'optTime',
      },
      {text: 'Title', value: 'title'},
      {text: 'Method', value: 'method'},
      {text: 'Option Type', value: 'optType'},
      {text: 'Status', value: 'status'},
      {text: 'Message', value: 'msg'},
    ],
    logs: '',
    // 所有用户数据相关：
    search: '',
    headers: [
      {
        text: 'ID',
        align: 'start',
        value: 'userId',
      },
      {text: 'Username', value: 'userName'},
      {text: 'Creation Date', value: 'createTime'},
      {text: 'Role', value: 'role'},
    ],
    users: '',
    // 是否显示退出登陆按钮
    showClickToLogout: false
  }),

  methods: {
    // 打开通知
    clickInfo() {
      document.getElementById("infoBtn").click();
    },
    // 删除所有通知
    delNotify() {
      if (confirm("Remove All System Notifications?")) {
        this.axios.delete("/notify").then(res => {
          if (res.data.code == 200) {
            alert("Deleted");
          } else {
            alert("Please wait a minute before you try again");
          }
        });
      }
    },
    // 更新所有用户信息
    refreshAllUsers() {
      this.axios.get("/user").then(res => {
        if (res.data.code === 200) {
          this.users = res.data.data;
          alert("Success");
        } else {
          alert("Something went wrong...");
        }
      }).catch(error => {
        alert("Something went wrong...");
      })
    },
    // 获取日志信息、所有用户、"是否为管理员"以及注册管理员的验证码
    getInfo() {
      let verifyToken = this.getRandomStr();
      localStorage.setItem("verifyToken", verifyToken);

      this.axios.get("/admin").then(res => {
        this.isAdmin = res.data.data.admin;
        this.logs = res.data.data.logs;
        this.users = res.data.data.users;
      }).catch((error) => {
        if (error.response.data.code === 2009) {
          // 代码 2009 表示没有权限，此时获取验证码来注册管理员
          this.getVCode();
        }
        if (error.response.data.code === 2005) {
          // 2005 表示没有登陆
          this.$router.push("/login")
        }
      });
    },
    // 获取随机字符串
    getRandomStr() {
      let text = "";
      let pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      for (let i = 0; i < 10; i++)
        text += pool.charAt(Math.floor(Math.random() * pool.length));
      return text;
    },
    // 发送通知
    sendNotify() {
      if (this.noticeCon.trim() == '') {
        alert("Please enter something..");
      } else {
        this.axios.get("/notify/send/" + this.noticeCon).then(res => {
          alert(res.data.msg);
        });
        this.noticeCon = '';
      }
    },
    // 回车发送通知
    toSendNotify() {
      this.sendNotify();
      let btn = document.getElementById("myAdminBtn");
      btn.focus();
    },
    // 发送邮件
    sendEmail() {
      let invitationToken = this.getRandomStr();
      localStorage.setItem("invitationToken", invitationToken);

      this.axios.get("/invitation", {
        params: {
          email: this.email,
          invitationToken: invitationToken
        }
      }).then(res => {
        alert("Sent. Please check your Email.");
      }).catch((error) => {
        if (error.response.data.code == 3008) {
          let invitationCode = error.response.data.data;
          alert("Your Invitation Code is: " + invitationCode);
          this.adminCode = invitationCode;
        } else {
          alert(error.response.data.msg);
        }
      }).finally(() => {
        this.dialog = false;
      });
    },
    // 获取验证码
    getVCode() {
      let verifyToken = this.getRandomStr();
      localStorage.setItem("verifyToken", verifyToken);

      this.axios.get("/verify/getVerImg?time=" + new Date().getTime(), {
        params: {verifyToken: verifyToken}
      }).then(resp => {
        this.verCode = resp.data;
      });
    },
    // 提交注册相关
    validate() {
      this.$refs.form.validate();
      this.isLoading = true;
      let submitData = {
        userName: this.name,
        password: this.password,
        role: "admin"
      }
      this.axios.post("/user/create", submitData, {
        params: {
          code: this.code,
          role: "admin",
          invitationCode: this.adminCode,
          verifyToken: localStorage.getItem("verifyToken"),
          invitationToken: localStorage.getItem("invitationToken")
        }
      }).then(res => {
        if (res.data.code === 500) {
          // 500 表示错误
          this.status = res.data.msg;
        } else if (res.data.code === 200) {
          // 200 表示成功
          this.status = '<b>Success!</b><br>' +
              'New Admin account has been registered.<br>' +
              'You can Login as Admin after logging out current account.';
          this.alertColor = 'green';
          this.showClickToLogout = true;
        } else {
          // 其他情况，打印信息
          this.status = res.data.msg;
        }
      }).catch(error => {
        // 2004 表示用户已存在
        // 2007 表示验证码错误
        // 2008 表示邀请码错误
        // 3014 表示没有传入角色
        // 3003 表示用户名只能为英文和数字，出现其他的字符就报错
        // 3004 和 3005 表示用户名太长和密码太长
        // 3006 和 3007 表示用户名和密码为空
        let code = error.response.data.code;
        if (code === 2004 || 2007 || 2008 || 3014
            || 3003 || 3004 || 3005 || 3006 || 3007) {
          this.status = error.response.data.msg;
        }
      }).finally(() => {
        this.isLoading = false;
      });
    },
    // 点击此处退出登陆
    clickToLogout() {
      if (confirm("Are you sure you want to Sign Out?")) {
        this.axios.get("/log/out").then(res => {
          if (res.data.code === 200) {
            alert("Bye~")
            this.showClickToLogout = false;
            this.$router.push("/login");
          }
        });
      }
    }
  },

  created() {
    window.onload = function () {
      document.getElementById("myAdminBtn").click();
    }
    this.getInfo();
  }
}
</script>

<style scoped>

</style>
