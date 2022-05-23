<template>
  <div>
    <el-table
      :data="tableData"
      style="width: 100%"
      @row-click="openDetail" v-click-stat="{event_name:'click',type:'button'}">
      <el-table-column
        prop="id"
        label="序号"
        width="180">
      </el-table-column>
      <el-table-column
        prop="name"
        label="名称"
        width="180">
      </el-table-column>
      <el-table-column
        prop="category"
        label="类别"
        width="180">
      </el-table-column>
      <el-table-column
        prop="lang"
        label="语言"
        width="180">
      </el-table-column>
      <el-table-column
        prop="score"
        label="评分"
        width="180">
      </el-table-column>
      <el-table-column
        prop="picUrl"
        label="电影图片"
        width="180">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" alt="" style="width: 120px;height: 120px">
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      align="center"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10,20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
  </div>
</template>

<script>
export default {
  name: 'List',
  data() {
    return {
      tableData: [],
      currentPage: 1, //当前页数
      total: 0, //总数据条数
      pageSize: 10 //每页显示条数
    };
  },
  mounted() {
    this.loadMoviesData();
  },
  methods: {
    loadMoviesData() {
      var pageSize = this.pageSize;
      var currentPage = this.currentPage;
      console.log("pageSize: " + pageSize + " currentPage: " + currentPage)
      var fetchUrl = '/movie/list?pageSize=' + pageSize + '&page=' + (currentPage - 1)
      this.$http({
        method: 'get',
        url: fetchUrl,
      }).then(({data}) => {
        console.log(data)
        console.log(data.code)
        if (data.code === 200) {
          console.log("请求电影列表成功")
          this.tableData = data.data.content
          this.total = data.data.totalElements
        } else {
          console.log("请求电影列表失败")
          console.log(data)
        }
      })
    },
    // 点击切换条数
    handleSizeChange(val) {
      // console.log(`每页 ${val} 条`);
      this.pageSize = val
      this.loadMoviesData()
    },
    // 点击页数
    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadMoviesData();
    },
    openDetail(row, column, event, type) {
      this.$router.push('/info/'+row.id)
    }
  }
};
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
