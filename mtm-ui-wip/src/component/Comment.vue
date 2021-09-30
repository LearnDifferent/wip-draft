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

    <!-- 编辑新的评论 -->
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
        style="margin-bottom: 2%"
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
          <div>
            <v-btn
                rounded
                outlined
                color="#5b7e91"
                dark
                @click="showCommentsFunc"
                class="text-none"
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
        <!-- 编辑评论时，围成圈 -->
        <div
            :style="editCommentId == c.commentId ?
            'border-radius: 25px;margin-top: 2%;border: 2px solid #82ae46;padding: 20px;' : ''"
        >
          <!-- 展示评论 -->
          <v-card
              color="#e7e7eb"
              :id="c.commentId"
              @mouseover="onThisComment = c.commentId"
          >
            <v-card-text>
              <p>
                <!-- 用户名 -->
                <v-icon left>
                  {{ currentUsername == c.username ? 'mdi-account-check' : 'mdi-account-outline' }}
                </v-icon>
                {{ c.username }}

                <v-divider
                    class="mx-2"
                    vertical
                ></v-divider>

                <!-- 创建时间 -->
                <v-icon left>
                  mdi-clock-outline
                </v-icon>
                {{ c.creationTime }}

                <!-- 打开重新编辑评论的按钮 -->
                <v-divider
                    v-show="currentUsername == c.username && onThisComment == c.commentId"
                    class="mx-2"
                    vertical
                ></v-divider>
                <v-btn
                    x-small
                    class="text-none"
                    color="#82ae46"
                    v-show="currentUsername == c.username && onThisComment == c.commentId"
                    @click="openEditComment(c.comment, c.commentId)"
                    rounded
                >
                  <v-icon left>mdi-tooltip-edit-outline</v-icon>
                  Edit
                </v-btn>

                <!-- 删除按钮（只能删除自己的评论） -->
                <v-divider
                    v-show="currentUsername == c.username && onThisComment == c.commentId"
                    class="mx-2"
                    vertical
                ></v-divider>
                <v-btn
                    x-small
                    class="text-none"
                    color="#e83929"
                    v-show="currentUsername == c.username && onThisComment == c.commentId"
                    @click="deleteComment(c.commentId)"
                    rounded
                >
                  <v-icon left>mdi-delete-outline</v-icon>
                  Delete
                </v-btn>
              </p>
              <!-- 评论内容 -->
              <p>{{ c.comment }}</p>
            </v-card-text>
          </v-card>

          <div style="margin-top: 2%" v-show="editCommentId == c.commentId">
            <!-- 编辑已有的评论 -->
            <v-textarea
                label="Edit Comment"
                counter
                :rules="commentRule"
                auto-grow
                prepend-inner-icon="mdi-send"
                v-model="editCommentValue"
                @click:prepend-inner="sendEditComment(c)"
                color="#84a2d4"
                outlined
                rows="1"
                row-height="15"
            ></v-textarea>
            <!-- 发送编辑的评论 -->
            <v-btn
                class="text-none"
                color="#6c848d"
                outlined
                :disabled="!editCommentValue || editCommentValue.length > 140"
                @click="sendEditComment(c)"
            >
              <v-icon left>mdi-send</v-icon>
              Save
            </v-btn>
            <v-divider
                class="mx-2"
                vertical
            ></v-divider>
            <!-- 取消发送编辑的评论 -->
            <v-btn
                class="text-none"
                color="#f2c9ac"
                outlined
                @click="openEditComment"
            >
              <v-icon left>mdi-close-circle-outline</v-icon>
              Cancel
            </v-btn>
          </div>
        </div>
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
    // 悬浮在该 comment id 的位置
    onThisComment: -1,
    // 网页 id
    webId: -1,
    // 是否打开评论区
    showCommentArea: false,
    // 发送的评论内容
    commentValue: '',
    // 编辑已有的评论内容
    editCommentValue: '',
    // 被编辑的评论的 ID
    editCommentId: -1,
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
    // 重置数据
    resetDataAndGetComments() {
      this.editCommentId = -1;
      this.editCommentValue = '';
      this.load = 10;
      this.count = 0;
      this.getComment(true);
    },
    // 打开编辑评论的选项
    openEditComment(newCommentValue, editCommentId) {
      if (this.editCommentId < 0) {
        this.editCommentValue = newCommentValue;
        this.editCommentId = editCommentId;
      } else {
        this.editCommentValue = '';
        this.editCommentId = -1;
      }
    },
    // 对重新编辑过的已有评论进行发送
    sendEditComment(data) {
      // 给 data 加上 webId 属性
      data.webId = this.webId;
      // 替换为新的评论内容
      data.comment = this.editCommentValue;
      this.axios.post("comment", data).then(res => {
        if (res.data.code == 200 || res.data.code == 500) {
          // 200 表示成功，500 表示失败
          alert(res.data.msg);
        } else {
          alert("Something went wrong. Please try again later.");
        }
      }).catch(error => {
        let code = error.response.data.code;
        if (code === 2009 || code === 3012
            || code == 2001 || code == 3010 || code == 3011) {
          // 2009 表示没有权限，3012 表示评论不存在
          // 2001 网页不存在，3010 评论为空，3011 评论太长
          alert(error.response.data.msg);
        } else if (code == 3009) {
          // 3009 表示评论已经存在，也就是编辑内容没有发生改变
          alert("No change has been made");
        } else {
          alert("Something went wrong... Please try again later.");
        }
      }).finally(() => {
        // 重置数据
        this.resetDataAndGetComments();
      });
    },
    // 删除评论
    deleteComment(commentId) {
      if (confirm("Are you sure you want to delete this comment?")) {
        this.axios.delete("/comment", {
          params: {
            commentId: commentId,
            username: this.currentUsername
          }
        }).then(res => {
          if (res.data.code == 200) {
            // 200 表示删除成功
            alert(res.data.msg);
            this.getComment();
          } else {
            alert("You can't delete this comment");
          }
        }).catch(error => {
          let code = error.response.data.code;
          if (code === 2009 || code === 3012) {
            // 2009 表示没有权限，3012 表示评论不存在
            alert(error.response.data.msg);
          } else {
            alert("Something went wrong... Please try again later.")
          }
        }).finally(() => {
          // 重置数据
          this.resetDataAndGetComments();
        });
      }
    },
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
    getComment(dontShowNoMoreAlert) {
      this.axios.get("/comment", {
        params: {
          webId: this.webId,
          load: this.load,
          username: this.currentUsername
        }
      }).then(res => {
        // 200 表示获取到了数据
        if (res.data.code == 200) {
          // 获取数据
          this.comments = res.data.data;

          // 获取数据条数
          let count = this.comments.length;

          if (count == 0) {
            if (dontShowNoMoreAlert !== true) {
              alert("No comments yet");
            }
            // 重置数据
            this.load = 10;
            this.count = 0;
            this.showCommentArea = false;
          } else if (count === this.count && dontShowNoMoreAlert !== true) {
            // dontShowNoMoreAlert 表示不要提醒
            alert("No More Comments");
          } else {
            // 统计加载的数量
            this.count = count;
            // 每次都新增 10
            this.load += 10;
          }
        } else {
          // 如果返回其他，表示没有数据
          if (dontShowNoMoreAlert !== true) {
            alert("No comments yet");
          }
          // 重置数据
          this.load = 10;
          this.count = 0;
          this.showCommentArea = false;
        }
      }).catch(error => {
        alert(error.response.data.msg);
        // 重置数据
        this.load = 10;
        this.count = 0;
        this.showCommentArea = false;
      });
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
            // 重置编辑区
            this.commentValue = '';
          } else if (res.data.code == 500) {
            alert(res.data.msg);
          } else {
            alert("Something went wrong. Please try again later.");
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
            // 发送过后，如果评论区是打开的，就重置数据
            this.resetDataAndGetComments();
          }
        });
      }
    },
  },
}
</script>

<style scoped>

</style>