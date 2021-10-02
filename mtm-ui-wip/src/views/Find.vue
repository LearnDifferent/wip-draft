<template>

  <v-container fill-height>
    <v-btn v-show="hasNewUpdate && hasDb"
           class="text-none"
           block
           color="orange"
           outlined
           rounded
           @click="genDb">
      <v-icon>mdi-cached</v-icon>
      <span>Update Search Engine</span>
    </v-btn>

    <div v-show="!hasDb">
      <v-alert
          prominent
          type="error"
      >
        <v-row align="center">
          <v-col class="grow">
            The Search-Engine Database has no data for searching.
            You will NOT get any search results unless you
            Generate the Database.
            Please <a href="javascript:void(0)" @click="genDb" style="color: aliceblue">click here</a> or the right
            button to Generate the Database.
          </v-col>
          <v-col class="shrink">
            <v-btn class="text-none" @click="genDb">Generate</v-btn>
          </v-col>
        </v-row>
      </v-alert>
    </div>

    <!-- 更多选项（目前使用这个） -->
    <v-btn
        class="mx-2"
        color="#808080"
        dark
        small
        fab
        @click="hidden = !hidden"
    >
      <v-icon dark>
        {{ hidden ? 'mdi-cog' : 'mdi-close' }}
      </v-icon>
    </v-btn>
    <v-fab-transition
        v-for="tile in tiles"
    >
      <v-btn
          class="text-none"
          :key="tile.title"
          v-show="!hidden"
          rounded
          outlined
          @click="moreOptions(tile.tId)"
      >
        <v-icon
            left
            :color="tile.color"
        >
          {{ tile.icon }}
        </v-icon>
        {{ tile.title }}
      </v-btn>
    </v-fab-transition>

    <v-btn
        class="mx-2"
        color="red"
        dark
        small
        fab
        @click="showTrending =! showTrending"
    >
      <v-icon dark>
        {{ showTrending ? 'mdi-fire' : 'mdi-close' }}
      </v-icon>
    </v-btn>

    <!--  更多选项（有 BUG，展示关闭）  -->
    <!--    <template>-->
    <!--      <div class="text-center" v-show="hasDb">-->
    <!--        <v-bottom-sheet v-model="sheet">-->
    <!--          <template v-slot:activator="{ on, attrs }">-->
    <!--            <v-btn-->
    <!--                id="moreOpsBtn"-->
    <!--                class="mx-2"-->
    <!--                color="#808080"-->
    <!--                dark-->
    <!--                small-->
    <!--                fab-->
    <!--                v-bind="attrs"-->
    <!--                v-on="on"-->
    <!--            >-->
    <!--              <v-icon dark>-->
    <!--                mdi-cog-->
    <!--              </v-icon>-->
    <!--            </v-btn>-->
    <!--          </template>-->
    <!--          <v-list>-->
    <!--            <v-subheader>More Options</v-subheader>-->
    <!--            <v-list-item-->
    <!--                v-for="tile in tiles"-->
    <!--                :key="tile.title"-->
    <!--                @click="sheet = false"-->
    <!--            >-->
    <!--              <v-list-item-avatar>-->
    <!--                <v-avatar-->
    <!--                    size="32px"-->
    <!--                    tile-->
    <!--                >-->
    <!--                  <v-icon-->
    <!--                      large-->
    <!--                      :color="tile.color"-->
    <!--                  >-->
    <!--                    {{ tile.icon }}-->
    <!--                  </v-icon>-->
    <!--                </v-avatar>-->
    <!--              </v-list-item-avatar>-->
    <!--              <v-list-item-title @click="moreOptions(tile.tId)">{{ tile.title }}</v-list-item-title>-->
    <!--            </v-list-item>-->
    <!--          </v-list>-->
    <!--        </v-bottom-sheet>-->
    <!--      </div>-->
    <!--    </template>-->

    <v-progress-linear
        v-show="processing"
        color="#b35c44"
        indeterminate
        rounded
        height="30px"
    >
      <div style="size: 20px">{{ processing }}</div>
    </v-progress-linear>

    <!-- 更多选项（有 Bug 展示关闭） -->
    <!--    <v-container>-->
    <!--      <div class="d-flex justify-center">-->
    <!--        &lt;!&ndash; 生成搜索数据库 &ndash;&gt;-->
    <!--        <template>-->
    <!--          <v-row justify="center">-->
    <!--            <v-dialog-->
    <!--                v-model="dialog"-->
    <!--                persistent-->
    <!--                max-width="50%"-->
    <!--            >-->
    <!--              <template v-slot:activator="{ on, attrs }">-->
    <!--                <v-btn-->
    <!--                    v-show="false"-->
    <!--                    id="generateBtn"-->
    <!--                    class="ma-2 text-none"-->
    <!--                    color="indigo"-->
    <!--                    dark-->
    <!--                    outlined-->
    <!--                    v-bind="attrs"-->
    <!--                    v-on="on"-->
    <!--                >-->
    <!--                  Generate Search-Engine Database-->
    <!--                </v-btn>-->
    <!--              </template>-->
    <!--              <v-card>-->
    <!--                <v-card-title style="color: red;">-->
    <!--                  This process will take some time-->
    <!--                </v-card-title>-->
    <!--                <v-card-text class="headline">Are you sure you want to Generate (Update) Search-Engine database?-->
    <!--                </v-card-text>-->
    <!--                <v-card-actions>-->
    <!--                  <v-spacer></v-spacer>-->
    <!--                  <v-btn-->
    <!--                      color="grey darken-1"-->
    <!--                      text-->
    <!--                      @click="closeDialog"-->
    <!--                  >-->
    <!--                    No-->
    <!--                  </v-btn>-->
    <!--                  <v-btn-->
    <!--                      color="orange darken-1"-->
    <!--                      text-->
    <!--                      @click="genDb"-->
    <!--                  >-->
    <!--                    Yes-->
    <!--                  </v-btn>-->
    <!--                </v-card-actions>-->
    <!--              </v-card>-->
    <!--            </v-dialog>-->
    <!--          </v-row>-->
    <!--        </template>-->

    <!--        &lt;!&ndash; 删除搜索数据库 &ndash;&gt;-->
    <!--        <template>-->
    <!--          <v-row justify="center">-->
    <!--            <v-dialog-->
    <!--                v-model="dialog1"-->
    <!--                persistent-->
    <!--                max-width="50%"-->
    <!--            >-->
    <!--              <template v-slot:activator="{ on, attrs }">-->
    <!--                <v-btn-->
    <!--                    v-show="false"-->
    <!--                    id="deleteBtn"-->
    <!--                    class="ma-2 text-none"-->
    <!--                    color="red"-->
    <!--                    dark-->
    <!--                    outlined-->
    <!--                    v-bind="attrs"-->
    <!--                    v-on="on"-->
    <!--                >-->
    <!--                  Delete Search-Engine database-->
    <!--                </v-btn>-->
    <!--              </template>-->
    <!--              <v-card>-->
    <!--                <v-card-title style="color: red;">-->
    <!--                  This process will take some time-->
    <!--                </v-card-title>-->
    <!--                <v-card-text class="headline">Are you sure you want to delete search-engine database?</v-card-text>-->
    <!--                <v-card-actions>-->
    <!--                  <v-spacer></v-spacer>-->
    <!--                  <v-btn-->
    <!--                      color="grey darken-1"-->
    <!--                      text-->
    <!--                      @click="closeDialog1"-->
    <!--                  >-->
    <!--                    No-->
    <!--                  </v-btn>-->
    <!--                  <v-btn-->
    <!--                      color="orange darken-1"-->
    <!--                      text-->
    <!--                      @click="delDb"-->
    <!--                  >-->
    <!--                    Yes-->
    <!--                  </v-btn>-->
    <!--                </v-card-actions>-->
    <!--              </v-card>-->
    <!--            </v-dialog>-->
    <!--          </v-row>-->
    <!--        </template>-->

    <!--        &lt;!&ndash; 删除所有热搜词&ndash;&gt;-->
    <!--        <template>-->
    <!--          <v-row justify="center">-->
    <!--            <v-dialog-->
    <!--                v-model="dialog3"-->
    <!--                persistent-->
    <!--                max-width="50%"-->
    <!--            >-->
    <!--              <template v-slot:activator="{ on, attrs }">-->
    <!--                <v-btn-->
    <!--                    v-show="false"-->
    <!--                    id="delTagsBtn"-->
    <!--                    class="ma-2"-->
    <!--                    color="indigo"-->
    <!--                    dark-->
    <!--                    outlined-->
    <!--                    v-bind="attrs"-->
    <!--                    v-on="on"-->
    <!--                >-->
    <!--                  delAllTrending-->
    <!--                </v-btn>-->
    <!--              </template>-->
    <!--              <v-card>-->
    <!--                <v-card-title style="color: red;">-->
    <!--                  This process will take some time-->
    <!--                </v-card-title>-->
    <!--                <v-card-text class="headline">Are you sure you want to Delete All Trending Tags?</v-card-text>-->
    <!--                <v-card-actions>-->
    <!--                  <v-spacer></v-spacer>-->
    <!--                  <v-btn-->
    <!--                      color="grey darken-1"-->
    <!--                      text-->
    <!--                      @click="closeDialog3"-->
    <!--                  >-->
    <!--                    No-->
    <!--                  </v-btn>-->
    <!--                  <v-btn-->
    <!--                      color="orange darken-1"-->
    <!--                      text-->
    <!--                      @click="delAllTrending"-->
    <!--                  >-->
    <!--                    Yes-->
    <!--                  </v-btn>-->
    <!--                </v-card-actions>-->
    <!--              </v-card>-->
    <!--            </v-dialog>-->
    <!--          </v-row>-->
    <!--        </template>-->

    <!--      </div>-->
    <!--    </v-container>-->


    <!--  <v-card-->
    <!--      max-width="80%"-->
    <!--      class="mx-auto"-->
    <!--  >-->


    <!--搜索框-->
    <v-row style="margin-top: 1%">
      <v-col>
        <v-text-field
            id="inputField"
            v-model="inputMsg"
            :append-icon="inputMsg ? 'mdi-magnify' : ''"
            :label="placeholderMsg ? '正在搜索：'+placeholderMsg:''"
            single-line
            outlined
            clearable
            @keyup.enter="searchRequest(inputMsg, currentPage)"
            @click:append="searchRequest(inputMsg, currentPage)"
        ></v-text-field>
        <p v-show="totalCount !== -1 && !errorMsg">搜索到{{ totalCount >= 1 ? totalCount : 0 }}条结果</p>
        <p v-show="errorMsg" style="color: red">{{ errorMsg }}</p>
      </v-col>
    </v-row>

    <v-progress-linear
        v-show="isSearching"
        color="orange"
        indeterminate
        rounded
        height="30px"
    >
      <div style="size: 20px">Searching....</div>
    </v-progress-linear>

    <!-- 热搜数据 -->
    <div class="text-center" v-show="this.showTrending">
      <v-row>
        <v-col
            class="text-center"
            v-for="(trend, i) in trending"
            :key="i"
            cols="20"
            v-show="trending"
        >
          <v-chip
              v-show="i < 6"
              class="ma-2"
              color="#dc3023"
              text-color="white"
              @click="searchRequest(trend, currentPage)"
          >
            {{ trend }}
            <v-icon right @click.stop="delTrend(trend, i)">
              mdi-close
            </v-icon>
          </v-chip>

          <v-chip
              v-show="6 <= i && i <= 12"
              class="ma-2"
              color="#f35336"
              text-color="white"
              @click="searchRequest(trend, currentPage)"
          >
            {{ trend }}
            <v-icon right @click.stop="delTrend(trend)">
              mdi-close
            </v-icon>
          </v-chip>

          <v-chip
              v-show="i > 12"
              class="ma-2"
              color="#ea5506"
              text-color="white"
              @click="searchRequest(trend, currentPage)"
          >
            {{ trend }}
            <v-icon right @click.stop="delTrend(trend)">
              mdi-close
            </v-icon>
          </v-chip>

        </v-col>
      </v-row>
    </div>

    <v-container class="mx-auto">
      <!-- 搜索结果 -->
      <v-row dense>
        <v-col
            v-for="(item, i) in items"
            :key="i"
            cols="12"
        >
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
        </v-col>
      </v-row>

      <!--分页-->
      <v-row v-show="currentPage>0" align-content="center">
        <v-col>
          <v-pagination
              v-model="currentPage"
              :length="totalPage"
              circle
              @input="changePage(keyword, currentPage)"
          ></v-pagination>
        </v-col>
      </v-row>

    </v-container>
    <!--  </v-card>-->
  </v-container>
</template>

<script>

export default {
  name: "Find",
  data: () => ({
    // 是否隐藏更多选项
    hidden: true,
    inputMsg: '',
    placeholderMsg: '',
    // 关键词
    keyword: '',
    // 错误信息
    errorMsg: '',
    // 总数
    totalCount: -1,
    // 分页
    currentPage: 0,
    totalPage: 1,
    // 当前用户
    currentUser: '',
    // 网页数据
    items: '',
    // 热搜
    trending: '',
    // 是否显示热搜
    showTrending: true,
    // 是否正在搜索
    isSearching: false,
    // 生成搜索数据库按钮的对话框
    // dialog: false,
    // 删除搜索数据库按钮的对话框
    // dialog1: false,
    // 删除所有热搜词的对话框
    // dialog3: false,
    // 操作搜索数据库相关
    sheet: false,
    tiles: [
      {
        title: 'Refresh',
        tId: 'refresh',
        icon: 'mdi-refresh',
        color: 'green lighten-1'
      },
      {
        title: 'Delete All Trending Tags',
        tId: 'tags',
        icon: 'mdi-tag-off-outline',
        color: 'red lighten-1'
      },
      {
        title: 'Generate (Update) Search-Engine Database',
        tId: 'gen',
        icon: 'mdi-database-refresh',
        color: 'green darken-2'
      },
      {
        title: 'Delete Search-Engine Database',
        tId: 'del',
        icon: 'mdi-database-remove',
        color: 'red darken-2'
      },
    ],
    // 是否有数据库
    hasDb: true,
    // 是否有新的更新
    hasNewUpdate: false,
    // 正在进行搜索数据库相关操作
    processing: '',
  }),
  methods: {
    // 删除某个热搜词
    delTrend(word, arrayIndex) {
      if (confirm("Delete this Trending Tag?")) {
        this.axios.delete("/find/trends/" + word).then(res => {
          if (res.data === true) {
            alert("Deleted");
            this.trending.splice(arrayIndex, 1);
          } else {
            alert("Can't delete it...");
          }
        }).catch(error => {
          if (error.response.data.code === 2009) {
            alert("You are now Login as Guest and Guest can't delete this tag.\n\n" +
                "Please Login as Normal User or Admin to delete.");
          } else {
            alert("Something went wrong. Please try again later.")
          }
        });
      }
    },
    // 删除所有热搜标签
    delAllTrending() {
      // this.dialog3 = false;
      this.processing = 'Delete All Trending Tags.... Please wait a minute.';
      this.axios.delete("/find/trends").then(res => {
        if (res.data === true) {
          alert("Deleted");
          location.reload();
        } else {
          alert("Already Deleted");
        }
      }).catch(error => {
        if (error.response.data.code === 2009) {
          alert("You are now Login as Guest and Guest can't delete tags.\n\n" +
              "Please Login as Normal User or Admin to delete.");
        } else {
          alert("Something went wrong. Please try again later.");
        }
      }).finally(() => {
        this.processing = '';
      });
    },
    // 更多操作（打开生成/删除搜索数据库的按钮等）
    moreOptions(tId) {
      if (tId === 'del') {
        if (confirm("Are you sure you want to Delete Search-Engine Database?")) {
          this.delDb();
        }
      }
      if (tId === 'gen') {
        if (confirm("Are you sure you want to Generate (Update) Search-Engine Database?")) {
          this.genDb();
        }
      }
      if (tId === 'tags') {
        if (confirm("Are you sure you want to Delete All Trending Tags?")) {
          this.delAllTrending();
        }
      }
      if (tId === 'refresh') {
        location.reload();
      }
      // 最后，让按钮恢复正常
      let btn = document.getElementById("myFindBtn");
      btn.click();
    },
    // // 关闭更多操作的对话框
    // closeDialog() {
    //   this.dialog = false;
    //   let btn = document.getElementById("moreOpsBtn");
    //   btn.click();
    // },
    // closeDialog1() {
    //   this.dialog1 = false;
    //   let btn = document.getElementById("moreOpsBtn");
    //   btn.click();
    // },
    // closeDialog3() {
    //   this.dialog3 = false;
    //   let btn = document.getElementById("moreOpsBtn");
    //   btn.click();
    // },
    // 删除搜索数据库
    delDb() {
      // this.dialog1 = false;
      this.processing = 'Deleting The Search-Engine Database.... Please wait a minute.';
      this.axios.delete("/find/build").then(res => {
        if (res.data == true) {
          alert("Deleted");
          this.hasDb = false;
        } else {
          alert("Something went wrong. Please try again.")
        }
        this.processing = '';
      }).catch(error => {
        if (error.response.data.code === 5001) {
          this.processing = '';
          // 5001 表示网络异常
          alert(error.response.data.msg);
        }
      });
    },
    // 生成搜索数据库操作
    genDb() {
      this.hasNewUpdate = false;
      // this.dialog = false;
      this.processing = 'Generating The Search-Engine Database... Please wait a minute.';
      this.axios.get("/find/build").then(res => {
        if (res.data == true) {
          alert("Success!");
          this.hasDb = true;
        } else {
          alert("Something went wrong. Please try again.")
        }
        this.processing = '';
      }).catch(error => {
        if (error.response.data.code === 5001) {
          this.processing = '';
          // 5001 表示网络异常
          alert(error.response.data.msg);
        }
      });
    },
    // 跳转页面
    jump(url) {
      window.open(url, '_blank')
    },
    changePage(keyword, currentPage) {
      var keyword = keyword;
      var currentPage = currentPage;
      this.searchRequest(keyword, currentPage);

      // 让页面返回顶部
      document.body.scrollTop = 0;
      document.documentElement.scrollTop = 0;
    },
    searchRequest(keyword, currentPage) {
      if (keyword.trim() === '') {
        alert("Please enter something...");
      } else if (!this.hasDb) {
        alert("No Data to Search. Please Generate Search-Engine Database.")
      } else {
        if (this.keyword !== keyword) {
          currentPage = 1;
          this.currentPage = currentPage;
        }

        // 进行搜索的时候，不显示热搜
        this.showTrending = false;
        // 正在进行搜索
        this.isSearching = true;

        // 重新搜索的时候，删除错误信息
        this.errorMsg = '';

        this.axios.get("/find/search", {
          params: {
            "currentPage": currentPage,
            "keyword": keyword
          }
        }).then(resp => {
          this.items = resp.data.data.webs;
          this.totalPage = resp.data.data.totalPage;
          let totalCount = resp.data.data.totalCount;
          if (totalCount == 0 || !totalCount) {
            // 没有搜索结果的时候，显示热搜
            this.showTrending = true;
          }
          this.totalCount = totalCount;
          this.keyword = keyword;
          let inputField = document.getElementById("inputField");
          inputField.blur();
          // 搜索结束
          this.isSearching = false;
        }).catch(error => {
          this.processing = '';
          this.errorMsg = error.response.data.msg;
          this.isSearching = false;
        });

        this.clearMessage(keyword);
      }
    },
    clearMessage(keyword) {
      this.placeholderMsg = keyword
      this.inputMsg = ''
    },
    load() {
      this.axios.get("/find").then(res => {
        this.trending = res.data.data.trendingList;
        this.hasDb = res.data.data.dataStatus;
        this.hasNewUpdate = res.data.data.hasNewUpdate;
      }).catch((error) => {
        if (error.response.data.code === 2005) {
          this.$router.push("/login")
        } else if (error.response.data.code === 5001) {
          alert("Unable to connect to Search Engine.");
        } else {
          alert("Something went wrong. Can't load this page.")
        }
      });
    }
  },
  created() {
    window.onload = function () {
      document.getElementById("myFindBtn").click();
    }
    this.load();
  }
}
</script>

<style scoped>
</style>
