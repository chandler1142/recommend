package utils

import org.apache.spark.sql.functions.{col, collect_list}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.json4s.jackson.JsonMethods
import redis.clients.jedis.Jedis

import scala.collection.JavaConversions.`deprecated asScalaBuffer`
import scala.collection.mutable

class ModelUtil(spark: SparkSession) {

  //本次召回推荐表
  val rsRecall = "history_rs_recall"
  //cf
  val cf = "recall"

  def getUserItemRating: DataFrame = {
    val data = spark.sql(" select * from dws_user_item_rating limit 1000")
    data
  }

  def saveRecall(recommend: DataFrame, key: String): Unit = {
    /**
     * 目标生成格式
     * uid   itemid
     * 3     [12, 3, 1, 2]
     * 5     [19, 73, 121]
     */
    val recommendList: DataFrame = recommend.groupBy(col("user_id"))
      .agg(collect_list("movie_id"))
      .withColumnRenamed("collect_list(movie_id)", "movie_id")
      .select(col("user_id"), col("movie_id"))

    recommendList.rdd.foreach(println)
    recommendList.rdd.foreachPartition(it => {
      if (!it.isEmpty) {
        val redisClient: Jedis = RedisUtil.getClient

        import spark.implicits._
        import org.json4s.JsonDSL._


        val map: Map[String, String] = it.map(row =>
          (java.lang.String.valueOf(row.getAs[String]("user_id")),
            row.get(1).asInstanceOf[mutable.WrappedArray[Int]].mkString(",")
          )).toMap

        import scala.collection.JavaConverters._
        println(map.asJava)

        redisClient.hmset(key, map.asJava)

        redisClient.close()
      }


    })

  }


}

object ModelUtil {
  var spark: SparkSession = _

  def apply(spark: SparkSession): ModelUtil = new ModelUtil(spark)
}
