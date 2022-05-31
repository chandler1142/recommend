import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.PropertiesUtil

import java.util.Properties

object UserItemRating {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .getOrCreate()

    val mysqlUrl = PropertiesUtil.getPropString("jdbc.mysql.url")
    val prop = new Properties()
    prop.put("user", PropertiesUtil.getPropString("jdbc.mysql.username"))
    prop.put("password", PropertiesUtil.getPropString("jdbc.mysql.password"))
    prop.put("useSSL", PropertiesUtil.getPropString("jdbc.mysql.ssl"))
    val scoreDF: DataFrame = spark.read.jdbc(mysqlUrl, "dws_user_behavior_score", prop)
    val ratingDF: DataFrame = spark.read.jdbc(mysqlUrl, "dws_user_item_rating", prop)

    scoreDF.createOrReplaceTempView("dws_user_behavior_score")
    ratingDF.createOrReplaceTempView("dws_user_item_rating")

    //sigmoid
    //把所有值映射到0到5之间
    spark.udf.register("scaler", (score: Double) => 5 * 1/(1 + math.exp(-score)))

    val scalerDF = spark.sql(" select user_id, movie_id, scaler(sum(score)) as rating from dws_user_behavior_score group by user_id, movie_id")

    scalerDF.createOrReplaceTempView("scaler")

    spark.sql("insert overwrite table dws_user_item_rating select user_id as user_id, movie_id as movie_id, rating as rating from scaler")

  }
}
