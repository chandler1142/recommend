import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.PropertiesUtil

import java.util.Properties

object ItemCF {
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
    val rawDF: DataFrame = sql("select user_id, movie_id from t_user_behavior where event != 'NotInterested' limit 100")
    println(rawDF.count())

    rawDF.show(false)

    /**
     * 1. 计算物品之间的相似度
     * 2. 根据物品的相似度和用户的历史行为给用户生成推荐列表
     */
    val NMap: collection.Map[Int, Int] = rawDF.rdd
      .map(row => (row.getAs[Int]("movie_id"), row.getAs[Int]("user_id")))
      .groupByKey()
      .map(a => (a._1, a._2.size)).collectAsMap()

    val rawDF2: DataFrame = sql("select user_id as target_user_id, movie_id as target_movie_id from t_user_behavior where event != 'NotInterested' limit 100")
    val CMap: RDD[((Int, Int), Int)] = rawDF.join(rawDF2)
      .filter(row => row.getAs[Int]("movie_id") != row.getAs[Int]("target_movie_id"))
      .filter(row => row.getAs[Int]("user_id") != row.getAs[Int]("target_user_id"))
      .rdd.map(row => ((row.getAs[Int]("movie_id"), row.getAs[Int]("target_movie_id")), 1))
      .reduceByKey(_ + _)

    //得到所有电影被不同用户行为的次数
    CMap.foreach(println)

    CMap.map(element => (
      element._1._1,
      (element._1._2,
        element._2.toFloat / Math.sqrt(NMap(element._1._1) * NMap(element._1._2)))
    )).groupByKey()
      .map(element => (element._1, element._2.toList.sortBy(a => a._2).reverse))


  }
}
