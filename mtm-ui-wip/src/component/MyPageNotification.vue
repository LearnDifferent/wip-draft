<template>
  <v-container class="mx-auto">
    <v-col
        v-for="(notification, i) in notificationList"
        :key="i"
        cols="12"
    >
      <v-alert
          prominent
          color="#ee827c"
          dark
          :icon="notification.replyToCommentId !== null ? 'mdi-message-reply-text' : 'mdi-comment-text-outline'"
          @click="openReplyNotification(i, notification)"
      >
        <v-row align="center">
          <v-col class="grow">
            <b>{{ notification.sendUsername === currentUsername ? 'You' : notification.sendUsername }} </b>
            {{
              notification.replyToCommentId !== null ?
                  notification.sendUsername === currentUsername ? 'replied to yourself' : 'replied to you'
                  : 'posted a comment on your activity'
            }}

            <span style="font-size: small;color: #d3cbc6">
                <v-icon small color="#d3cbc6">
                  mdi-clock-outline
                </v-icon>
                {{ notification.creationTime }}
              </span>
          </v-col>
          <v-col class="shrink">
            <v-btn
                color="#d0576b"
                class="text-none"
            >
              <v-icon left>mdi-eye</v-icon>
              View
            </v-btn>
          </v-col>
        </v-row>
      </v-alert>

      <div v-show="showNotification==='notify' + i">

        <v-card :id="websiteData.webId">
          <div class="d-flex flex-no-wrap justify-space-between">
            <div>
              <v-card-title
                  class="headline"
                  v-text="websiteData.title"
                  @click="jump(websiteData.url)"
              ></v-card-title>

              <v-card-subtitle v-text="websiteData.desc"></v-card-subtitle>

              <v-card-actions>
                <v-chip
                    class="ma-2"
                    color="black"
                    label
                    outlined
                >
                  <v-icon left>
                    {{ currentUsername === websiteData.userName ? 'mdi-account-check' : 'mdi-account' }}
                  </v-icon>
                  {{ websiteData.userName }}
                </v-chip>

                <v-chip
                    color="#bf783a"
                    @click="jump(websiteData.url)"
                    outlined
                    style="margin-right: 3px"
                >
                  <v-icon left>
                    mdi-link-variant
                  </v-icon>
                  View
                </v-chip>

                <v-divider
                    class="mx-2"
                    vertical
                ></v-divider>

                <div>
                  <v-icon>mdi-clock-outline</v-icon>
                  <span style="color: grey;">
                      {{ websiteData.createTime }}
                    </span>
                </div>
              </v-card-actions>
            </div>

            <v-avatar
                class="ma-3"
                size="125"
                tile
                @click="jump(websiteData.url)"
            >
              <v-img :src="websiteData.img"></v-img>
            </v-avatar>
          </div>
        </v-card>

        <comment
            ref="commentArea"
            :current-username="currentUsername"
            :web-id="notification.webId"
        ></comment>

        <!--        <p>{{ notification.commentId }}</p>-->
        <!--        <p>{{ notification.replyToCommentId }}</p>-->
        <!--        <p>{{ notification.receiveUsername }}</p>-->
        <!--        <p>{{ notification.sendUsername }}</p>-->
        <!--        <p>{{ notification.webId }}</p>-->
      </div>
    </v-col>

  </v-container>
</template>

<script>
import Comment from "./Comment";

export default {
  name: "MyPageNotification",
  components: {Comment},
  data: () => ({
    // 消息提醒列表
    notificationList: '',
    // 当前用户名
    currentUsername: '',
    // 显示该 id 的消息：id 为: 'notify' + index
    showNotification: null,
    // 网页数据
    websiteData: '',
  }),

  methods: {
    // 获取回复我的通知
    getMyNotifications(size) {
      this.axios.get("/notify/reply", {
        params: {
          "username": this.currentUsername,
          "size": size
        }
      }).then(res => {
        let code = res.data.code;
        if (code === 200) {
          this.notificationList = res.data.data;
        } else if (code === 2009) {
          alert(res.data.msg);
        } else {
          alert("Something went wrong... Please try again later.")
        }
      }).catch(error => {
        if (error.response.data.code === 2013) {
          // 2013 表示没有数据
          alert("No Notifications Yet");
        } else {
          alert("Something went wrong... Please try again later.")
        }
      })
    },

    // 跳转页面
    jump(url) {
      window.open(url, '_blank')
    },

    openReplyNotification(index, notificationData) {
      this.showNotification = 'notify' + index;
      this.getWebsiteData(notificationData.webId);
      // if (this.websiteData.webId===null)

      if (notificationData.replyToCommentId !== null) {
        // 不为 null，说明是回复
        this.$refs.commentArea[index].openCommentFromOutside(false);
      } else {
        // null 时，说明是普通的评论
        this.$refs.commentArea[index].openCommentFromOutside(true, notificationData.commentId)
      }
    },

    getWebsiteData(webId) {
      this.axios.get("/web/get", {
        params: {
          userName: this.currentUsername,
          webId: webId
        }
      }).then(res => {
        let code = res.data.code;
        if (code === 200) {
          this.websiteData = res.data.data;
        } else if (code === 500) {
          alert("You don't have permission to view the post");
        } else {
          alert("Something went wrong..");
        }
      })
    }
  },

  props: {
    currentUsername: {
      type: String,
      required: true
    }
    // notificationList: {
    //   type: Array,
    //   required: true
    // }
  },
}
</script>

<style scoped>

</style>