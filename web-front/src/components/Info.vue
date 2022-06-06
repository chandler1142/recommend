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


    <br/>
    推荐列表:
    <div class="demo_warp" style="text-align:left">
      <ul class="tab_tit">
        <li :class="n==1?'active':''" @click="n=1">随机推荐</li>
        <li :class="n==2?'active':''" @click="n=2">ALS推荐</li>
        <li :class="n==3?'active':''" @click="n=3">ItemCF推荐</li>
        <li :class="n==4?'active':''" @click="clickRealtimeRecommend">实时推荐</li>
      </ul>
      <div class="tab_con">
        <div v-show="n==1">
          <el-card v-if="n==1" shadow="never">
            <div>
              <el-row :gutter="10">
                <el-col
                  :span="4"
                  :class="{ line: (index + 1) % 5 }"
                  style="margin:5px 0px;"
                  v-for="(item, index) in recommendList"
                  :key="index"
                >
            <span>
                <img :src="item.picUrl" alt="" style="width: 120px;height: 120px" :id="item.id" @click="clickRecommend"
                     v-click-stat="{event_name:'click',type:'button'}">
            </span>
                  <p class="ptext">{{ item.name }}</p>
                  <p>{{ item.category }}</p>
                  <!--                  <p>{{ item.region }}</p>-->
                </el-col>
              </el-row>
            </div>
          </el-card>
        </div>
        <div v-show="n==2">
          <el-card v-if="n==2" shadow="never">
            <div>
              <el-row :gutter="10">
                <el-col
                  :span="4"
                  :class="{ line: (index + 1) % 5 }"
                  style="margin:15px 0px;"
                  v-for="(item, index) in alsRecommendList"
                  :key="index"
                >
            <span>
                <img :src="item.picUrl" alt="" style="width: 120px;height: 120px" :id="item.id" @click="clickRecommend"
                     v-click-stat="{event_name:'click',type:'button'}">
            </span>
                  <p class="ptext">{{ item.name }}</p>
                  <p>{{ item.category }}</p>
                  <!--                  <p>{{ item.region }}</p>-->
                </el-col>
              </el-row>
            </div>
          </el-card>
        </div>
        <div v-show="n==3">
          <el-card v-if="n==3" shadow="never">
            <div>
              <el-row :gutter="10">
                <el-col
                  :span="4"
                  :class="{ line: (index + 1) % 5 }"
                  style="margin:15px 0px;"
                  v-for="(item, index) in itemCFRecommendList"
                  :key="index"
                >
            <span>
                <img :src="item.picUrl" alt="" style="width: 120px;height: 120px" :id="item.id" @click="clickRecommend"
                     v-click-stat="{event_name:'click',type:'button'}">
            </span>
                  <p class="ptext">{{ item.name }}</p>
                  <p>{{ item.category }}</p>
                  <!--                  <p>{{ item.region }}</p>-->
                </el-col>
              </el-row>
            </div>
          </el-card>
        </div>
        <div v-show="n==4">
          <el-card v-if="n==4" shadow="never">
            <div>
              <el-row :gutter="10">
                <el-col
                  :span="4"
                  :class="{ line: (index + 1) % 5 }"
                  style="margin:15px 0px;"
                  v-for="(item, index) in realTimeRecommendList"
                  :key="index"
                >
            <span>
                <img :src="item.picUrl" alt="" style="width: 120px;height: 120px" :id="item.id" @click="clickRecommend"
                     v-click-stat="{event_name:'click',type:'button'}">
            </span>
                  <p class="ptext">{{ item.name }}</p>
                  <p>{{ item.category }}</p>
                  <!--                  <p>{{ item.region }}</p>-->
                </el-col>
              </el-row>
            </div>
          </el-card>
        </div>
      </div>
    </div>


  </el-row>
</template>

<style>
.demo_warp .tab_tit {
  display: flex;
  flex: 1;
  margin: .2rem;
  cursor: pointer;
}

.demo_warp .active {
  color: red;
  background-color: cadetblue;
}

.demo_warp ul li {
  list-style: none;
  width: 23%;
  text-align: center;
  background-color: #ccc;
  margin: 0 1%;
}

.demo_warp .tab_con {
  width: 100%;
  height: 3rem;
  border: 1px solid rgb(85, 85, 177);
  text-align: center;
}

.ptext {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100px;
  margin: 0 auto;
  height: 20px;
}
</style>
<script>
export default {
  name: 'App',
  data() {
    return {
      url: '',
      currentUser: '',
      movieData: {},
      recommendList: [],
      alsRecommendList: [],
      itemCFRecommendList: [],
      realTimeRecommendList: [],
      n: 1
    }
  },
  mounted() {
    console.log(this.$route.params.id)
    this.loadMovieDataById()
    this.loadRecommend()
    this.loadALSRecommend()
    this.loadItemCFRecommend()
  },
  methods: {
    clickRealtimeRecommend() {
      this.n = 4
      this.loadRealTimeRecommendList()
    },
    loadALSRecommend() {
      let token = window.sessionStorage.getItem("token")
      if (!token) {
        console.log('丢失登录信息')
        return
      }
      let fetchUrl = '/recommend/getByALS?token=' + token
      this.$http({
        method: 'get',
        url: fetchUrl,
      }).then(({data}) => {
        if (data.code === 200) {
          this.alsRecommendList = data.data
          console.log("请求als推荐列表: " + this.alsRecommendList)
        } else {
          console.log("请求推荐列表失败")
        }
      })
    },
    loadItemCFRecommend() {
      let token = window.sessionStorage.getItem("token")
      if (!token) {
        console.log('丢失登录信息')
        return
      }
      let fetchUrl = '/recommend/getByItemCF?token=' + token
      this.$http({
        method: 'get',
        url: fetchUrl,
      }).then(({data}) => {
        if (data.code === 200) {
          this.itemCFRecommendList = data.data
          console.log("请求item推荐列表: " + this.itemCFRecommendList)
        } else {
          console.log("请求推荐列表失败")
        }
      })
    },
    loadRealTimeRecommendList() {
      let token = window.sessionStorage.getItem("token")
      if (!token) {
        console.log('丢失登录信息')
        return
      }
      let fetchUrl = '/recommend/getRealTimeList?token=' + token
      this.$http({
        method: 'get',
        url: fetchUrl,
      }).then(({data}) => {
        if (data.code === 200) {
          this.realTimeRecommendList = data.data
          console.log("请求实时item推荐列表: " + this.realTimeRecommendList)
        } else {
          console.log("请求实时推荐列表失败")
        }
      })
    },

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
      let token = window.sessionStorage.getItem("token")
      if (!token) {
        console.log('丢失登录信息')
        return
      }
      let fetchUrl = '/recommend/getByUserToken?token=' + token
      this.$http({
        method: 'get',
        url: fetchUrl,
      }).then(({data}) => {
        if (data.code === 200) {
          console.log("随机推荐哦哦哦")
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
      this.loadRealTimeRecommendList()
      this.$router.push('/info/' + id)
      this.$router.go()
    }
  }
}
</script>

