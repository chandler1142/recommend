
import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:8081'//请求的地址
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

//数据上报
function send(uploadInfo) {
  axios({
    method: 'post',
    url: '/event/upload',
    data: JSON.stringify(uploadInfo)
  }).then(({data}) => {
    if(data.code !== 200){
      console.log(data)
      console.log("上传失败!")
    } else {
      console.log("上传成功");
    }
  })
}

//基础数据组装
function basicInfo(clickData, e) {

  let basicData = {};

  basicData['token'] = window.sessionStorage.getItem("token");
  basicData['agent'] = getAgent();
  basicData['time'] = Date.now();
  basicData['event'] = clickData.event_name
  basicData['movie_link'] = e.target.baseURI

  return basicData;
}

//获取客户端的操作系统
function getAgent() {

  var agent = null;
  var userAgent = navigator.userAgent;
  var agents = ["Android", "iPhone", "iPad"];
  var flag = true;
  for (var i = 0; i < agents.length; i++) {
    if (userAgent.indexOf(agents[i]) > 0) {
      agent = agents[i];
      flag = false;
      break;
    }
  }

  if (flag) {
    agent = "windows";
  }

  return agent;

}

//数据组装
function getInfo(clickData, target) {
  let data = basicInfo(clickData, target);
  return data;
}

export {send, getInfo}

