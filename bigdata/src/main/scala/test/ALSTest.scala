package test

import org.apache.spark.ml.recommendation.{ALS, ALSModel}
import org.apache.spark.sql.SparkSession

object ALSTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .getOrCreate()

    val df = spark.createDataFrame(Seq(
      (1, 9, 4.0),
      (1, 10, 2.0),
      (2, 9, 1.0),
      (2, 10, 2.0)
    )).toDF("user", "item", "rating")

    val als = new ALS()
      .setRank(10)
      .setRegParam(0.01)
      .setMaxIter(10)
      .setAlpha(0.1)
      .setUserCol("user")
      .setItemCol("item")
      .setRatingCol("rating")
      .setImplicitPrefs(false)

    val model: ALSModel = als.fit(df)

    model.userFactors.orderBy("id").rdd.foreach(println)
    model.itemFactors.rdd.foreach(println)

    spark.stop()
  }
}
