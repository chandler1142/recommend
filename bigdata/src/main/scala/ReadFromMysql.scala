import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.PropertiesUtil

import java.util.Properties

object ReadFromMysql {
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
    println(df.count())
    println(df.rdd.partitions.size)

    import spark.sql

    df.createOrReplaceTempView("user_behavior")
    sql("select * from user_behavior").show()
  }
}
