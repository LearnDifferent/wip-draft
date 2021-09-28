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

    <v-textarea
        label="Write a comment..."
        auto-grow
        prepend-inner-icon="mdi-send"
        v-model="commentValue"
        @click:prepend-inner="sendComment"
        @keyup.enter="sendComment"
        color="#84a2d4"
        outlined
        rows="1"
        row-height="15"
    ></v-textarea>

    <v-row>
      <v-col>
        <div class="text-center">
          <v-btn
              class="text-none"
              rounded
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
      </v-col>
      <v-col
          v-show="showCommentArea"
          v-for="c in comments"
          cols="12"
      >
        <v-card color="#e7e7eb">
          <v-card-title>
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
          </v-card-title>

          <v-card-text>
            {{ c.commentText }}
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

  </div>
</template>

<script>
export default {
  name: "Comment",
  data: () => ({
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
  },

  methods: {
    // 打开评论区
    showCommentsFunc() {
      this.showCommentArea = !this.showCommentArea;
      if (this.showCommentArea) {
        this.getComment();
      }
    },
    // 获取评论
    getComment() {
      // todo test data
      this.comments = [
        {
          username: this.webId,
          commentText: 'jfiehfioh',
          creationTime: '2012'
        },
        {
          username: this.webId,
          commentText: 'jfiehfiohjfie',
          creationTime: '2012'
        }
      ];
    },
    // 发送评论
    sendComment() {
      if (confirm("Send this comment?")) {
        alert(this.commentValue);
        this.commentValue = '';
      }
    },
  },
}
</script>

<style scoped>

</style>