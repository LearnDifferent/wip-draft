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
  </div>
</template>

<script>
export default {
  name: "Comment",
  data: () => ({
    webId: -1,
    // 评论内容
    commentValue: '',
  }),

  props: {
    webId: {
      type: Number,
      required: true
    }
  },

  methods: {
    // 发送评论
    sendComment() {
      if (confirm("Send this comment?")) {
        alert(this.commentValue);
        alert(this.webId);
        this.commentValue = '';
      }
    },
  },

  created() {

  }
}
</script>

<style scoped>

</style>