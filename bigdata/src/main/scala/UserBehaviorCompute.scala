import java.util.Properties

import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.PropertiesUtil

/**
 * 定时执行，每日凌晨执行计算
 */
object UserBehaviorCompute {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName(this.getClass.getSimpleName)
      .master("local[2]")
      .getOrCreate()

    val mysqlUrl = PropertiesUtil.getPropString("jdbc.mysql.url")
    val prop = new Properties()
    prop.put("user", PropertiesUtil.getPropString("jdbc.mysql.username"))
    prop.put("password", PropertiesUtil.getPropString("jdbc.mysql.password"))
    prop.put("useSSL", PropertiesUtil.getPropString("jdbc.mysql.ssl"))
    val df: DataFrame = spark.read.jdbc(mysqlUrl, "user_behavior", prop)
    val scoreDF: DataFrame = spark.read.jdbc(mysqlUrl, "dws_user_behavior_score", prop)

    println(df.count())
    println(df.rdd.partitions.size)

    //行为权重(分数定义)
    //Array("click", "NotInterested", "marked", "watch");
    def behaviorAsScore(behavior: String): Int = behavior match {
      case "NotInterested" => -10
      case "click" => 1
      case "marked" => 3
      case "watch" => 5
    }

    spark.udf.register("behaviorAsScore", behaviorAsScore(_: String): Int)

    /**
     * 认为行为过物品就算感兴趣, 权重为1
     */
    df.createOrReplaceTempView("user_behavior")
    scoreDF.createOrReplaceTempView("dws_user_behavior_score")

    val userBehaviorScoreDF: DataFrame = spark.sql(
      "select user_id, movie_id, sum(behaviorAsScore(event)) as score from user_behavior group by user_id, movie_id "
    )

    userBehaviorScoreDF.createOrReplaceTempView("user_behavior_score")

    spark.sql("insert overwrite table dws_user_behavior_score select user_id as user_id, movie_id as movie_id, score as score from user_behavior_score")

  }
}
