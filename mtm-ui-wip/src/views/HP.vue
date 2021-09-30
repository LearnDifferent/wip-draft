<template>
  <v-container fill-height>

    <!--  <v-card-->
    <!--      max-width="80%"-->
    <!--      class="mx-auto"-->
    <!--  >-->

    <!-- 发送框 -->
    <v-container
        style="border-radius: 25px;
        border: 2px solid grey;
        padding: 20px;">
      <v-text-field
          label="Paste a Website Link (URL) here"
          v-model="newWebUrl"
          @keyup.enter="toSaveNewWeb"
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
            :prepend-icon="addToSearch ? 'mdi-sync' : 'mdi-sync-off'"
            :label="addToSearch ?
            'Mark and Synchronize Website to Search Engine':
            'Mark Only'"
        ></v-switch>
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
    <v-divider></v-divider>
    <br>
    <v-container>
      <!-- 选择按钮 -->
      <div class="d-flex justify-center" style="margin-bottom: 2%">
        <v-btn
            :outlined="!clickRecent"
            rounded
            color="black"
            dark
            @click="recent"
        >
          <v-icon left>
            mdi-home
          </v-icon>
          Home
        </v-btn>
        <v-divider vertical style="margin-left: 1%;margin-right: 1%"></v-divider>
        <v-btn
            rounded
            :outlined="!clickMost"
            color="black"
            dark
            @click="mostMark"
        >
          <v-icon left>
            mdi-fire
          </v-icon>
          Most
        </v-btn>
        <v-divider vertical style="margin-left: 1%;margin-right: 1%"></v-divider>
        <v-btn
            rounded
            :outlined="!clickFilter"
            color="black"
            dark
            @click="filter"
        >
          <v-icon left>
            mdi-filter-plus-outline
          </v-icon>
          Filter
        </v-btn>
      </div>

      <!-- 点了 recent 之后显示的按钮 -->
      <div class="text-center" v-show="clickRecent">
        <v-chip
            class="ma-2"
            label
            color="black"
            :outlined="isOut!=='all'"
            dark
            @click="findAll"
        >
          <v-icon left>
            mdi-account-circle
          </v-icon>
          All
        </v-chip>
        <v-chip
            class="ma-2"
            label
            :outlined="isOut!=='mine'"
            color="black"
            dark
            @click="findMine"
        >
          <v-icon left>
            mdi-account-circle
          </v-icon>
          Only Mine
        </v-chip>
        <v-chip
            class="ma-2"
            label
            :outlined="isOut!=='others'"
            color="black"
            dark
            @click="findOthers"
        >
          <v-icon left>
            mdi-account-circle
          </v-icon>
          Only Others
        </v-chip>
        <!-- 导出用户收藏的网页数据（点了 recent 且在 mine 和 otherOne 的情况下才会显示-->
        <v-chip
            v-show="clickRecent && (isOut === 'mine' || isOut === 'otherOne')"
            color="#b6d7de"
            @click="exportHtmlFile"
        >
          <v-icon left>
            mdi-download
          </v-icon>
          Download {{ toUserName == currentUser ? "My" : toUserName + "'s" }} Data
        </v-chip>
      </div>

      <!-- 其他选择按钮 -->
      <div
          class="text-center"
          v-show="isOut === 'otherOne' || isOut === 'dontShow'"
      >
        <v-chip
            v-show="showSelectedUser"
            class="ma-2"
            label
            :outlined="isOut !== 'otherOne' && isOut !== 'dontShow'"
            color="black"
            dark
            @click="recent"
        >
          <v-icon left>
            mdi-account-circle
          </v-icon>
          <span :style="isOut === 'otherOne' ? '':'text-decoration: line-through'">
            {{ toUserName }}
          </span>
        </v-chip>
      </div>

    </v-container>

    <br>
    <v-container class="mx-auto">

      <!-- 数据筛选模块 -->
      <div v-show="clickFilter">

        <!-- 展示筛选的数据 -->
        <div style="border-radius: 25px;border: 2px solid #8AC007;padding: 20px;">
          <!-- 数据筛选 -> 筛选出来的用户-->
          <span v-for="(sel, i) in selected" :key="i">
            <v-chip
                class="ma-2search"
                color="green"
                outlined
                label
            >
              <v-icon left>
                mdi-account-circle-outline
              </v-icon>
              {{ sel.userName }}
            </v-chip>
          </span>

          <!-- 数据筛选 -> 筛选出来的日期-->
          <v-row>
            <v-col
                cols="12"
                order-lg="6"
            >
              <v-text-field
                  v-model="dateRangeText"
                  label="Date Filter"
                  prepend-icon="mdi-calendar"
                  readonly
                  @click="clearSelectedDate"
              >
              </v-text-field>
            </v-col>
          </v-row>

          <!-- 筛选相关按钮： -->
          <!-- 发送筛选请求 -->
          <v-btn
              rounded
              class="text-none"
              color="#8AC007"
              dark
              @click="filterSend"
          >
            <v-icon left>mdi-send</v-icon>
            Find
          </v-btn>
          <v-divider vertical style="margin-left: 3px;margin-right: 3px"></v-divider>
          <!-- 打开筛选器 -->
          <v-btn
              rounded
              color="black"
              class="text-none"
              outlined
              @click="showFilter = !showFilter"
          >
            <v-icon left>{{ showFilter ? 'mdi-arrow-down-thick' : 'mdi-filter-plus-outline' }}</v-icon>
            Filter
          </v-btn>
        </div>

        <v-row>
          <v-divider style="margin-top: 20px;margin-bottom: 1%"></v-divider>
        </v-row>

        <!-- 筛选器 Filter-->
        <div v-show="showFilter">
          <v-row>
            <!-- 数据筛选 -> 筛选用户的表格-->
            <v-col
                cols="12"
                lg="6"
            >
              <v-text-field
                  background-color="#484848"
                  dark
                  v-model="search"
                  prepend-inner-icon="mdi-magnify"
                  label="Search Username"
                  single-line
                  hide-details
              ></v-text-field>
              <v-data-table
                  :search="search"
                  dark
                  calculate-widths
                  height="320px"
                  v-model="selected"
                  :headers="userHeaders"
                  :items="userToSelect"
                  item-key="userName"
                  show-select
                  class="elevation-1"
                  :single-select="singleSelect"
              >
              </v-data-table>
            </v-col>

            <!-- 数据筛选 -> 日期 -->
            <v-col
                cols="12"
                lg="6"
            >
              <v-date-picker
                  dark
                  v-model="dates"
                  range
              ></v-date-picker>
            </v-col>
          </v-row>
        </div>

      </div>

      <!-- 刷新 -->
      <v-btn v-show="refreshShow"
             block
             color="orange"
             outlined
             rounded
             @click="refreshPage">
        <v-icon>mdi-cached</v-icon>
        <span>    Something new...</span>
      </v-btn>

      <!-- 对筛选结果进行排序 -->
      <div style="text-align: center">
        <span
            v-for="(order, i) in orderBy"
            v-show="clickFilter && count > 0"
        >
         <v-chip
             class="ma-2"
             color="success"
             outlined
             @click="changeOrder(i)"
         >
          <v-icon left>
            <!-- 0：User - Ascend -->
            {{ i === 0 ? 'mdi-sort-alphabetical-ascending' : '' }}
            <!-- 1：Time - Ascend -->
            {{ i === 1 ? 'mdi-sort-clock-ascending-outline' : '' }}
            <!-- 2：User - Descend -->
            {{ i === 2 ? 'mdi-sort-alphabetical-descending' : '' }}
            <!-- 3：Time - Descend -->
            {{ i === 3 ? 'mdi-sort-clock-descending-outline' : '' }}
          </v-icon>
          {{ order }}
          </v-chip>
        </span>
      </div>

      <!-- 网页数据 -->
      <v-row dense>
        <v-col
            v-for="(item, i) in items"
            :key="i"
            cols="12"
        >
          <!-- 不显示的情况，需要满足：
          1. 在 clickRecent 下
          2. isOut 是 all 或者 mine
          3. isPublic 为 false，也就是私有
          4. 该 website 的所有者不是当前用户
          只有者四个情况同时满足，才不会显示，否则，就显示出来
          -->
          <v-card
              @mouseover="onThisWebData = item.webId"
              @mouseleave="onThisWebData = -1"
              :id="item.webId"
              v-show="!(clickRecent
              && (isOut == 'all' || isOut == 'mine')
              && !item.isPublic
              && currentUser!=item.userName)"
          >
            <div class="d-flex flex-no-wrap justify-space-between">
              <div>
                <v-card-title
                    class="headline"
                    v-text="item.title"
                    @click="jump(item.url)"
                ></v-card-title>

                <v-card-subtitle v-text="item.desc"></v-card-subtitle>

                <v-card-actions v-show="clickFilter">
                  <v-chip
                      class="ma-2"
                      color="#8AC007"
                      label
                      outlined
                  >
                    <v-icon left>
                      mdi-account-circle-outline
                    </v-icon>
                    {{ item.userName }}
                  </v-chip>
                  <v-chip
                      class="ma-2"
                      color="#8AC009"
                      label
                      outlined
                  >
                    <v-icon left>
                      mdi-clock-outline
                    </v-icon>
                    {{ item.createTime }}
                  </v-chip>
                </v-card-actions>

                <v-card-actions v-show="!clickFilter">

                  <div
                      v-show="item.userName"
                      @mouseover="showExcludeUserBtn = item.webId"
                      @mouseleave="showExcludeUserBtn = -1"
                  >
                    <v-chip
                        class="ma-2"
                        color="black"
                        label
                        outlined
                    >
                      <v-icon left @click="toUser(item.userName)">
                        {{ currentUser == item.userName ? 'mdi-account-check' : 'mdi-account' }}
                      </v-icon>
                      <span
                          @click="toUser(item.userName)"
                          :style="onThisWebData == item.webId ? 'color: black' : 'color: grey' "
                      >
                      {{ item.userName }}
                    </span>
                      <span v-show="showCloseIcon">
                      <v-btn
                          icon
                          small
                          v-show="showExcludeUserBtn == item.webId"
                          color="pink"
                          @click="dontShowUser(item.userName)"
                      >
                        <v-icon>mdi-eye-off-outline</v-icon>
                      </v-btn>
                    </span>
                    </v-chip>
                  </div>

                  <v-divider
                      v-show="clickRecent"
                      class="mx-2"
                      vertical
                  ></v-divider>

                  <v-chip
                      v-show="currentUser!=item.userName"
                      color="green"
                      outlined
                      @click="mark(item)"
                      style="margin-right: 3px"
                  >
                    <v-icon left>
                      mdi-heart-plus
                    </v-icon>
                    Mark
                  </v-chip>

                  <v-chip
                      v-show="currentUser==item.userName"
                      :color="item.isPublic ? 'green' : 'pink'"
                      :text-color="item.isPublic ? 'green' : 'pink'"
                      outlined
                      @click="changePrivacy(item.webId, item.userName, item.isPublic)"
                      style="margin-right: 3px"
                  >
                    <v-icon left>{{ item.isPublic ? "mdi-eye" : "mdi-eye-off" }}</v-icon>
                    {{ item.isPublic ? "Public" : "Private" }}
                  </v-chip>

                  <v-chip
                      color="#bf783a"
                      @click="jump(item.url)"
                      outlined
                      style="margin-right: 3px"
                  >
                    <v-icon left>
                      mdi-link-variant
                    </v-icon>
                    View
                  </v-chip>

                  <v-chip
                      v-show="clickRecent"
                      color="#84a2d4"
                      @click="openComment(item.webId)"
                      outlined
                      style="margin-right: 3px"
                  >
                    <v-icon left>
                      {{ showComment == item.webId ? 'mdi-comment-remove-outline' : 'mdi-comment-outline' }}
                    </v-icon>
                    Comment {{ item.commentCount > 0 ? '(' + item.commentCount + ')' : '' }}
                  </v-chip>

                  <v-chip
                      v-show="currentUser == item.userName && onThisWebData == item.webId"
                      color="red"
                      outlined
                      @click="delWeb(item.webId)"
                      style="margin-right: 3px"
                  >
                    <v-icon left>
                      mdi-trash-can-outline
                    </v-icon>
                    Delete
                  </v-chip>

                  <div v-show="onThisWebData == item.webId && clickRecent">
                    <v-icon>mdi-clock-outline</v-icon>
                    <span style="color: grey;" v-show="!item.count">
                      {{ item.createTime }}
                    </span>
                  </div>

                  <v-divider
                      v-show="clickMost"
                      class="mx-2"
                      vertical
                  ></v-divider>

                  <div style="color: grey" v-show="item.count">Marked by {{ item.count }} user<span
                      v-show="item.count > 1">s</span>
                  </div>

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
          <div v-show="showComment == item.webId && clickRecent">
            <Comment :webId="showComment" :currentUsername="currentUser"></Comment>
          </div>

        </v-col>
      </v-row>

      <!-- 没有内容时提示 -->
      <v-alert
          prominent
          type="warning"
          v-show="totalPage===0&&!clickFilter"
      >
        <v-row align="center">
          <v-col class="grow">
            No Data Available
          </v-col>
          <v-col class="shrink">
            <v-btn @click="refreshPage">
              <v-icon>
                mdi-home
              </v-icon>
            </v-btn>
          </v-col>
        </v-row>
      </v-alert>

      <!-- 回到顶部按钮 -->
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

      <!-- 点击加载更多数据 -->
      <v-btn
          block
          color="yellow"
          v-show="showMoreBtn"
          @click="filterSendRequest"
      >
        <v-icon left>
          mdi-refresh
        </v-icon>
        More
      </v-btn>

      <!-- 分页：总数大于 0，或者没有点击筛选的时候才显示 -->
      <v-row align-content="center" v-show="totalPage !== 0&&!clickFilter">
        <v-col>
          <v-pagination
              v-model="currentPage"
              :length="totalPage"
              circle
              @input="loadHome(currentPage)"
          ></v-pagination>
        </v-col>
      </v-row>
    </v-container>
    <!--  </v-card>-->
  </v-container>
</template>

<script>
import Comment from "../component/Comment";

export default {
  components: {
    // 评论区
    Comment: Comment
  },
  name: "HP",
  data: () => ({
    // 展示模式
    pattern: 'recent',
    // 跳转的用户
    toUserName: '',
    // 分页
    currentPage: 1,
    totalPage: 1,
    // 当前用户
    currentUser: '',
    // 需要存储的网页链接
    newWebUrl: '',
    // 存储网页链接后返回的信息
    saveWebMsg: '',
    // 返回信息的颜色
    saveWebMsgColor: 'color: orange',
    // myWebs 存放遍历的 website 的数据：webId,userName,url,img,title,desc
    // （还可能有 count 数据和是否公开 isPublic）
    items: '',
    // 显示该 webId 的评论
    showComment: -1,
    // 是否将网页公开
    publicPrivacy: true,
    // 是否在收藏的同时，加入到搜索引擎中
    addToSearch: true,
    // 是否现实刷新信息
    refreshShow: false,
    // 是否正在解析网页
    isLoading: false,
    //显示该 web id 帖子的排除用户的按钮
    showExcludeUserBtn: -1,
    // 显示该 web id 帖子的创建时间
    onThisWebData: -1,
    // recent 的一些模式，被选中的有 outline 属性
    isOut: "all",
    // 是否点击了 recent
    clickRecent: true,
    // 是否点击了 most
    clickMost: false,
    // 是否点击了 filter
    clickFilter: false,
    // Filter 相关内容：
    // 展示筛选器
    showFilter: true,
    // 默认展示 10 条，每次加 10
    filterLoad: 10,
    // 筛选出来的结果的条数，如果新加载的条数和原来保存的条数相同，说明没有新的数据
    count: 0,
    // 对筛选出来对结果进行排序
    orderBy: ["Username - Ascend", "Time - Ascend", "Username - Descend", "Time - Descend"],
    ifOrderByTime: false,
    ifDesc: false,
    // dates: ['2019-09-10', '2019-09-20'], // 日期筛选
    dates: [],
    // 用户筛选
    selected: [],
    singleSelect: false,
    userHeaders: [
      {
        text: 'Username',
        align: 'start',
        sortable: false,
        value: 'userName',
      },
      {text: 'Public Websites', value: 'webCount'},
    ],
    userToSelect: [], // 有 userName 和 webCount 的列表
    // 用于搜索：
    search: '',
  }),
  methods: {
    // 查看所有的网页
    findAll() {
      this.recent();
      this.isOut = 'all';
      this.refreshShow = false;
    },
    // 只查看自己的网页
    findMine() {
      this.isOut = 'mine';
      this.pattern = 'userPage';
      this.toUserName = this.currentUser;
      this.currentPage = 1;
      this.loadHome(this.currentPage);
      this.refreshShow = false;
    },
    // 只查看别人的收藏网页
    findOthers() {
      this.isOut = 'others';
      // 这里表示剔除当前用户之外的所有用户
      this.pattern = 'withoutUserPage';
      this.toUserName = this.currentUser;
      this.currentPage = 1;
      this.loadHome(this.currentPage);
      this.refreshShow = false;
    },
    // 不查看某人的收藏网页
    dontShowUser(userName) {
      if (userName === this.currentUser) {
        if (confirm("Don't show your websites?")) {
          this.findOthers();
        }
      } else {
        if (confirm("Don't show the websites marked by user \"" + userName + "\" ?")) {
          this.isOut = 'dontShow';
          this.pattern = 'withoutUserPage';
          this.toUserName = userName;
          this.currentPage = 1;
          this.loadHome(this.currentPage);
        }
      }
    },
    // 查找某个用户收藏的所有网页
    toUser(userName) {
      if (userName === this.currentUser) {
        if (confirm("View the Websites you've marked?")) {
          this.findMine();
        }
      } else {
        if (confirm("Check the websites marked by user \"" + userName + "\" ?")) {
          this.toUserName = userName;
          this.pattern = 'userPage';
          this.currentPage = 1;
          this.loadHome(this.currentPage);
          this.isOut = 'otherOne';
        }
      }
    },
    // 过滤相关
    filter() {
      this.clickFilter = true;
      this.clickRecent = false;
      this.clickMost = false;
      // 先清除网页数据记录
      this.items = '';
      this.totalPage = 0;
      this.refreshShow = false;
      // 再重置已有的筛选数据
      this.filterLoad = 10;
      this.count = 0;
      this.showFilter = true;
      this.selected = [];
      this.dates = [];

      // 获取可供筛选的用户信息
      this.axios.get("/home/filter").then(res => {
        this.userToSelect = res.data;
      });
    },
    // 修改过滤后的排序方式（需要记住之前的选择）
    changeOrder(i) {
      if (i === 0) {
        // 0：User - Ascend
        this.ifOrderByTime = false;
        this.ifDesc = false;
      } else if (i === 1) {
        // 1：Time - Ascend
        this.ifOrderByTime = true;
        this.ifDesc = false;
      } else if (i === 2) {
        // 2：User - Descend
        this.ifOrderByTime = false;
        this.ifDesc = true;
      } else {
        // 3：Time - Descend（3 或者其他情况）
        this.ifOrderByTime = true;
        this.ifDesc = true;
      }
      this.filterSend();
    },
    // 点击发送筛选按钮
    filterSend() {
      this.filterLoad = 10;
      this.showFilter = false;
      this.count = 0;
      this.filterSendRequest();
    },
    // 发送筛选请求
    filterSendRequest() {
      // 获取被选中的用户名
      let usernames = [];
      for (let s of this.selected) {
        usernames.push(s.userName);
      }

      let data = {
        usernames: usernames,
        dates: this.dates,
        load: this.filterLoad,
        ifOrderByTime: this.ifOrderByTime,
        ifDesc: this.ifDesc
      }

      this.axios.post("/home/filter", data).then(res => {
        // 网页数据
        let webs = res.data.data;
        this.items = webs;
        // 网页数据的数量
        let count = webs.length;
        if (count === 0) {
          alert("No Result Found...");
          // 重置数据
          this.filterLoad = 10;
          this.count = 0;
        } else if (count === this.count) {
          alert("No More Results");
        } else {
          // 统计加载的数量
          this.count = count;
          // 每次都新增 10
          this.filterLoad += 10;
        }
      });
    },
    // 清除过滤中的日期
    clearSelectedDate() {
      if (confirm("Clear the Date Filter?")) {
        this.dates = [];
      }
    },
    // 查看最近的帖子
    recent() {
      this.clickRecent = true;
      this.clickMost = false;
      this.clickFilter = false;
      this.isOut = 'all';
      this.pattern = 'recent';
      this.currentPage = 1;
      this.loadHome(1);
      this.refreshShow = false;
    },
    // 按照最多收藏排序（注意，这种情况没有用户名，而有收藏次数）
    mostMark() {
      this.clickMost = true;
      this.clickRecent = false;
      this.clickFilter = false;
      this.pattern = 'marked';
      this.currentPage = 1;
      this.loadHome(1);
      this.refreshShow = false;
    },
    // 刷新页面（返回首页）
    refreshPage() {
      // location.reload();
      this.recent();
      this.refreshShow = false;
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
            this.loadHome(this.currentPage);
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
            "userName": this.currentUser
          }
        }).then(res => {
          if (res.data.code === 3001) {
            // 3001 表示删除成功
            alert(res.data.msg);
            this.loadHome(this.currentPage);
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
    // 进入主页后，获取信息
    loadHome(currentPage) {
      this.axios.get("/home/load", {
        params: {
          "currentPage": currentPage,
          "pattern": this.pattern,
          "userName": this.toUserName
        }
      }).then(res => {
        if (res.data.code === 2005) {
          // 2005 表示没有登陆
          this.saveWebMsg = res.data.msg;
          this.saveWebMsgColor = 'color: red';
        }
        this.currentUser = res.data.data.currentUser;
        this.items = res.data.data.websInfo.webs;
        this.totalPage = res.data.data.websInfo.totalPage;
        this.toUserName = res.data.data.optUsername;

        if (this.totalPage < this.currentPage && this.totalPage !== 0) {
          this.currentPage = this.totalPage;
          this.loadHome(this.currentPage);
        }

        // 让页面返回顶部
        document.body.scrollTop = 0;
        document.documentElement.scrollTop = 0;
      }).catch((error) => {
        if (error.response.data.code === 2005) {
          this.$router.push("/login")
        }
      });
    },
    // 点击 mark 按钮
    toSaveNewWeb() {
      this.saveNewWeb();
      let btn = document.getElementById("myHomeBtn");
      btn.focus();
    },
    // 根据URL保存新的网页
    saveNewWeb() {
      this.saveWebMsg = "Saving this Web Page. Please wait......";
      this.isLoading = true;

      let data = {
        url: this.newWebUrl,
        username: this.currentUser,
        syncToElasticsearch: this.addToSearch,
        isPublic: this.publicPrivacy
      };

      this.axios.post("/web/new", data).then(res => {
            console.log(res.data);
            let toDatabase = res.data[0];
            let toElasticsearch = res.data[1];

            if (toDatabase == false) {
              this.saveWebMsg = "Fail to mark this website.";
              this.saveWebMsgColor = 'color: red';
            }

            if (toDatabase == true && toElasticsearch == true) {
              this.saveWebMsg = "Success!";
              this.saveWebMsgColor = 'color: green';
              this.refreshShow = true;
            }

            if (toDatabase == true && toElasticsearch == false) {
              this.saveWebMsg = "Mark this website successfully, but fail to synchronize data into the search-engine database";
              this.saveWebMsgColor = 'color: orange';
              this.refreshShow = true;
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
    // 下载（导出）用户的网页数据
    exportHtmlFile() {
      let username = this.toUserName;
      // 打开 3000 端口的 /file 路径，而 3000 号端口映射了后端服务器的端口
      window.open("/file?username=" + username, "_blank");
    },
    // 打开评论
    openComment(webId) {
      if (this.showComment == webId) {
        webId = -1;
      }
      this.showComment = webId;
    },
    // 跳转页面
    jump(url) {
      window.open(url, '_blank')
    },
    // 页面回到顶部
    toTop() {
      document.body.scrollTop = 0;
      document.documentElement.scrollTop = 0;
    }
    ,
    // 此用户保存已经存放在数据库内的网页
    mark(item) {
      if (confirm("Are you sure you want to MARK this one?")) {
        let website = {
          title: item.title,
          url: item.url,
          img: item.img,
          desc: item.desc
        };
        this.axios.post("/web/existing", website, {
          params: {userName: this.currentUser}
        }).then(res => {
          alert(res.data.msg);
          if (res.data.code === 200) {
            // 收藏成功就展示刷新按钮
            this.refreshShow = true;
          }
        }).catch(error => {
          if (error.response.data.code === 2010) {
            // 代码为 2010 表示已经收藏过
            alert(error.response.data.msg);
          }
        });
      }
    },

  },
  computed: {
    dateRangeText() {
      return this.dates.join(' ~ ')
    }
    ,
    showMoreBtn() {
      return this.count !== 0
          && this.clickFilter
          && this.count % 10 === 0
      // 注意，因为每次 filterLoad 都会加 10
      // ，所以结果一定是 10 的倍数，所以才能这样判断
    }
    ,
    showSelectedUser() {
      return !(this.clickFilter || this.clickMost);
    }
    ,
    showCloseIcon() {
      return !(this.clickFilter || this.clickMost || this.isOut === 'others');
    }
  }
  ,
  created() {
    window.onload = function () {
      document.getElementById("myHomeBtn").click();
    }
    this.loadHome();
  }
  ,
}
</script>

<style scoped>

</style>
