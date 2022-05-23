<template>
  <el-row>
    <el-row :gutter="10">
      <el-col :span="10">
        <div style="text-align:right">
          <el-image
            style="width: 320px; height: 320px"
            :src="url"
            fit="fill"></el-image>
        </div>
      </el-col>
      <el-col :span="10">
        <div style="padding:10px;">
          <div style="font-weight:bold;font-size:22px;">
            {{ movieData.name }}
          </div>
          <div style="font-size:15px;margin-top:13px;">
            导演:{{ movieData.director }}
          </div>
          <div style="font-size:15px;margin-top:13px;">
            演员:{{ movieData.actors }}
          </div>
          <div style="font-size:15px;margin-top:13px;">
            类别:{{ movieData.category }}
          </div>
          <div style="font-size:15px;margin-top:13px;">
            语言:{{ movieData.lang }}
          </div>
          <div style="font-size:15px;margin-top:13px;">
            国家:{{ movieData.region }}
          </div>
          <div style="font-size:15px;margin-top:13px;">
            上映时间:{{ movieData.releaseTime }}
          </div>
          <div style="font-size:15px;margin-top:13px;">
            时长:{{ movieData.time }}
          </div>
        </div>
        <div style="font-weight:bold;font-size:30px;color:red;margin-top:30px;">
          评分:{{ movieData.score }}
        </div>
        <div style="font-weight:bold;font-size:30px;color:red;margin-top:30px;">
          参评人数:{{ movieData.scoreNum }}
        </div>
        <div style="margin-top:20px;">
          <el-row>
            <el-button type="warn" v-click-stat="{event_name:'NotInterested',type:'button'}">
              不感兴趣
            </el-button>
            <el-button type="success" v-click-stat="{event_name:'marked',type:'button'}">
              收藏
            </el-button>
            <el-button type="danger" v-click-stat="{event_name:'watch',type:'button'}">
              立即观看
            </el-button>
          </el-row>
        </div>
      </el-col>
    </el-row>


    <br></br>
    <div style="text-align:left">推荐列表:</div>
    <el-card shadow="never">
      <div>
        <el-row :gutter="10">
          <el-col
            :span="4"
            :class="{ line: (index + 1) % 3 != 0 }"
            style="margin:15px 0px;"
            v-for="(item, index) in recommendList"
            :key="index"
          >
            <span>
                <img :src="item.picUrl" alt="" style="width: 120px;height: 120px" :id="item.id" @click="clickRecommend"  v-click-stat="{event_name:'click',type:'button'}">
            </span>
            <p>{{ item.name }}</p>
            <p>{{ item.category }}</p>
            <p>{{ item.region }}</p>
          </el-col>
        </el-row>
      </div>
    </el-card>
    <!--    </el-row>-->


  </el-row>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      url: '',
      movieData: {},
      recommendList: []
    }
  },
  mounted() {
    console.log(this.$route.params.id)
    this.loadMovieDataById()
    this.loadRecommend()
  },
  methods: {
    loadMovieDataById() {
      let fetchUrl = '/movie/getById?id=' + this.$route.params.id
      this.$http({
        method: 'get',
        url: fetchUrl,
      }).then(({data}) => {
        console.log(data)
        if (data.code === 200) {
          this.movieData = data.data
          this.url = data.data.picUrl
        } else {
          console.log("请求电影列表失败")
        }
      })
    },
    loadRecommend() {
      let token =  window.sessionStorage.getItem("token")
      if(!token) {
        console.log('丢失登录信息')
        return
      }
      let fetchUrl = '/recommend/getByUserToken?token=' + token
      this.$http({
        method: 'get',
        url: fetchUrl,
      }).then(({data}) => {
        if (data.code === 200) {
          this.recommendList = data.data.content
          console.log(this.recommendList)
        } else {
          console.log("请求推荐列表失败")
        }
      })
    },
    clickRecommend(e) {
      console.log(e)
      let id = e.target.id
      this.$router.push('/info/'+id)
      this.$router.go()
    }
  }
}
</script>

