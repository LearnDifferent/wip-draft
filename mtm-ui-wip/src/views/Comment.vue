<template>
  <div
      style="border-radius: 25px;
              margin-top: 2%;
              border: 2px solid #84a2d4;
              padding: 20px;"
  >
    <v-chip
        style="margin-bottom: 1%"
        color="#84a2d4"
        outlined
        label
    >
      <v-icon left>
        mdi-comment-text-outline
      </v-icon>
      Comments
    </v-chip>

    <!-- 编辑评论 -->
    <v-textarea
        label="Write a comment..."
        counter
        :rules="commentRule"
        auto-grow
        prepend-inner-icon="mdi-send"
        v-model="commentValue"
        @click:prepend-inner="sendComment"
        color="#84a2d4"
        outlined
        rows="1"
        row-height="15"
        style="margin-bottom: 1%"
    ></v-textarea>

    <v-row>
      <v-col>
        <v-row>
          <!-- 发送评论 -->
          <div style="margin-left: 1%">
            <v-btn
                color="#6c848d"
                rounded
                outlined
                :disabled="!commentValue || commentValue.length > 140"
                @click="sendComment"
            >
              <v-icon left>mdi-send</v-icon>
              Send
            </v-btn>
          </div>

          <v-divider
              class="mx-2"
              vertical
          ></v-divider>

          <!-- 打开评论区  -->
          <div class="text-none">
            <v-btn
                rounded
                outlined
                color="#5b7e91"
                dark
                @click="showCommentsFunc"
            >
              <v-icon left>
                {{ showCommentArea ? 'mdi-comment-remove-outline' : 'mdi-comment-processing-outline' }}
              </v-icon>
              {{ showCommentArea ? 'Close' : 'Show Comments' }}
            </v-btn>
          </div>
        </v-row>
      </v-col>

      <!-- 评论区 -->
      <v-col
          v-show="showCommentArea"
          v-for="c in comments"
          cols="12"
      >
        <v-card color="#e7e7eb" :id="c.commentId">
          <v-card-text>
            <p>
              <v-icon left>
                mdi-account-outline
              </v-icon>
              {{ c.username }}
              <v-divider
                  class="mx-2"
                  vertical
              ></v-divider>
              <v-icon left>
                mdi-clock-outline
              </v-icon>
              {{ c.creationTime }}
            </p>
            <p>{{ c.comment }}</p>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 加载新数据 -->
      <v-col>
        <v-btn
            block
            color="#84a2d4"
            v-show="showMore"
            @click="getComment"
        >
          <v-icon left>
            mdi-refresh
          </v-icon>
          More
        </v-btn>
      </v-col>
    </v-row>

  </div>
</template>

<script>
export default {
  name: "Comment",
  data: () => ({
    // 加载的条数：默认为每次加载 10 条
    load: 10,
    // 已经加载的数据的条数，用于对比
    count: 0,
    // 当前用户
    currentUsername: '',
    // 评论的限制
    commentRule: [
      v => v.length <= 140 || '140-character limit',
      // v => !!v || 'Please enter a comment',
    ],
    // 网页 id
    webId: -1,
    // 是否打开评论区
    showCommentArea: false,
    // 发送的评论内容
    commentValue: '',
    // 所有评论
    comments: ''
  }),

  props: {
    webId: {
      type: Number,
      required: true
    },
    currentUsername: {
      type: String,
      required: true
    }
  },

  computed: {
    showMore() {
      // 注意，因为每次 load 都会加 10，所以结果的数量 count 一定是 10 的倍数，才有新的数据
      return this.count !== 0 && this.count % 10 === 0 && this.showCommentArea
    }
  },

  methods: {
    // 打开评论区
    showCommentsFunc() {
      this.showCommentArea = !this.showCommentArea;
      if (this.showCommentArea) {
        // 如果是打开评论区：
        this.getComment();
      } else {
        // 如果是关闭评论区，需要重置数据：
        this.load = 10;
        this.count = 0;
      }
    },
    // 获取评论
    getComment() {
      this.axios.get("/comment", {
        params: {
          webId: this.webId,
          load: this.load
        }
      }).then(res => {
        // 200 表示获取到了数据
        if (res.data.code == 200) {
          // 获取数据
          this.comments = res.data.data;

          // 获取数据条数
          let count = this.comments.length;

          if (count == 0) {
            alert("No comments yet");
            // 重置数据
            this.load = 10;
            this.count = 0;
            this.showCommentArea = false;
          } else if (count === this.count) {
            alert("No More Comments");
          } else {
            // 统计加载的数量
            this.count = count;
            // 每次都新增 10
            this.load += 10;
          }
        } else {
          // 如果返回其他，表示没有数据
          alert("No comments yet");
          // 重置数据
          this.load = 10;
          this.count = 0;
          this.showCommentArea = false;
        }
      })
    },
    // 发送评论
    sendComment() {
      if (confirm("Send this comment?")) {
        this.axios.get("/comment/create", {
          params: {
            comment: this.commentValue,
            webId: this.webId,
            username: this.currentUsername,
          }
        }).then(res => {
          if (res.data.code == 200) {
            // 200 表示成功，500 表示失败
            alert(res.data.msg);
            this.commentValue = '';
          } else if (res.data.code == 500) {
            alert(res.data.msg);
          }
        }).catch(error => {
          let code = error.response.data.code;
          if (code == 2009 || code == 3009 || code == 2001 || code == 3010 || code == 3011) {
            // 2009 表示没有权限，3009 表示评论已经存在，2001 网页不存在
            // 3010 评论为空，3011 评论太长
            alert(error.response.data.msg);
          } else {
            alert("Something went wrong. Please try again later.");
          }
        }).finally(() => {
          if (this.showCommentArea) {
            // 发送过后，如果评论区是打开的，就让其关闭
            this.showCommentsFunc();
          }
        });
      }
    },
  },
}
</script>

<style scoped>

</style>