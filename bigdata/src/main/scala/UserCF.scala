import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.json4s.jackson.JsonMethods
import redis.clients.jedis.Jedis
import utils.{PropertiesUtil, RedisUtil}

import java.util.Properties
import scala.collection.mutable.ListBuffer

object UserCF {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName(this.getClass.getSimpleName)
      .master("local[2]")
      .getOrCreate()

    val mysqlUrl = PropertiesUtil.getPropString("jdbc.mysql.url")
    val prop = new Properties()
    prop.put("user", PropertiesUtil.getPropString("jdbc.mysql.username"))
    prop.put("password", PropertiesUtil.getPropString("jdbc.mysql.password"))
    prop.put("useSSL", PropertiesUtil.getPropString("jdbc.mysql.ssl"))
    val userBehaviorDF: DataFrame = spark.read.jdbc(mysqlUrl, "user_behavior", prop)

    //1. 准备数据，数据需要清晰成 (user, item, rating), rating暂时用1代替
    import spark.sql
    userBehaviorDF.createOrReplaceTempView("t_user_behavior")
    val rawDF: DataFrame = sql("select user_id, movie_id from t_user_behavior where event != 'NotInterested'")
    println(rawDF.count())
    rawDF.show(false)

    //2. 组装物品-用户的倒排矩阵
    val userItemRDD: RDD[(Int, (Int, Int))] = rawDF.rdd.map(row => (row.getAs[Int]("user_id"), (row.getAs[Int]("movie_id"), 1)))
    val userItemRDDForB = rawDF.rdd.map(row => (row.getAs[Int]("user_id"), row.getAs[Int]("movie_id")))

    val bMap: Broadcast[collection.Map[Int, ListBuffer[Int]]] = spark.sparkContext.broadcast(userItemRDDForB.combineByKey((V: Int) => {
      val list = new ListBuffer[Int]
      list += V
    }, (C: ListBuffer[Int], V: Int) => {
      C += V
    }, (c1: ListBuffer[Int], c2: ListBuffer[Int]) => {
      c1 ++ c2
    }).collectAsMap())


    val itemUserRDD = rawDF.rdd.map(row => (row.getAs[Int]("movie_id"), row.getAs[Int]("user_id")))

    //2. 找到和目标用户兴趣相似的用户集合
    val NMap: collection.Map[Int, Int] = userItemRDD.reduceByKey((a, b) => (a._1, a._2 + b._2)).map(a => (a._1, a._2._2)).collectAsMap()
    val WMapStep1 = itemUserRDD.combineByKey((V: Int) => {
      val list = new ListBuffer[Int]
      list += V
    }, (C: ListBuffer[Int], V: Int) => {
      C += V
    }, (c1: ListBuffer[Int], c2: ListBuffer[Int]) => {
      c1 ++ c2
    })

    val WMap = WMapStep1.mapValues(listBuffer => {
      val result = new ListBuffer[(Int, Int)]
      for (m <- listBuffer) {
        for (n <- listBuffer) {
          if (m != n) {
            result.append((m, n))
          }
        }
      }
      result
    }).flatMap(_._2).map(element => (element, 1)).reduceByKey((a, b) => (a + b)).map(element => (element._1, element._2.toFloat / Math.sqrt(NMap(element._1._1) * NMap(element._1._2))))
    //3. 根据WMap的结构，将用户做为id，与之相似的k个用户作为value
    val K = 5
    val topKResult: RDD[(Int, Iterable[(Int, Double)])] = WMap.map(a => (a._1._1, (a._1._2, a._2))).sortBy(record => record._2._2, false)
      .groupByKey()
      .map(element => (element._1, element._2.slice(0, K)))
    topKResult.foreach(println)

    //4.根据用户筛选电影
    //找到该用户行为的电影集合，找到用户类似用户行为过的电影集合，两个集合做减法
    val n = 10
    val finalResultTopN: RDD[(Int, Array[Int])] = topKResult.map(element => {
      val targetUserId = element._1
      val selectUserIds = element._2.map(x => x._1).toArray
      val list1: Array[Int] = bMap.value.filter(element => {
        element._1 == targetUserId
      }).flatMap(element => element._2).toArray
      val list2: Array[Int] = bMap.value.filter(element => {
        selectUserIds.contains(element._1)
      }).flatMap(element => element._2).toArray
      val list3: Array[Int] = list2.filter(element => {
        !list1.contains(element)
      }).distinct
      //推荐N部电影
      (targetUserId, list3.splitAt(10)._1)
    })

    finalResultTopN.foreach(element => {
      println(element._1, element._2.mkString(","))
    })

    finalResultTopN.foreachPartition(it => {
      if (!it.isEmpty) {
        val redisClient: Jedis = RedisUtil.getClient

        import org.json4s.JsonDSL._

        val key = "recommend:usercf"
        val map: Map[String, String] = it.toMap.map {
          case (userId, recommendArray) => (userId.toString, JsonMethods.compact(JsonMethods.render(recommendArray.mkString(","))))
        }
        import scala.collection.JavaConverters._

        println(map.asJava)
        redisClient.hmset(key, map.asJava)

        redisClient.close()
      }


    })
  }


}
