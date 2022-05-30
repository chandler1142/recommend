package sort

import java.util.Properties

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{RFormula, RegexTokenizer, SQLTransformer, Word2Vec}
import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.PropertiesUtil

object LRSorting {
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
    val movieDF: DataFrame = spark.read.jdbc(mysqlUrl, "movies", prop)
      .select(col("id"), col("category"), col("lang"))
      .withColumnRenamed("id", "o_movie_id")
    val userDF: DataFrame = spark.read.jdbc(mysqlUrl, "user", prop)
      .select(col("id"), col("age"), col("sex"), col("movie_flags"))
      .withColumnRenamed("id", "o_user_id")

    val userBHDF: DataFrame = spark.read.jdbc(mysqlUrl, "user_behavior", prop)
      .withColumn("clicked", lit(1))
      .select("clicked", "movie_id", "user_id")
      .limit(1000)

    val dataDF: DataFrame = userBHDF.join(movieDF, userBHDF("movie_id") === movieDF("o_movie_id"))
      .join(userDF, userBHDF("user_id") === userDF("o_user_id"))
      .select("clicked", "category", "movie_flags")

    dataDF.show(1000)

    /**
     * 定义word2vec转换器来处理文本
     */
    val categoryRT: RegexTokenizer = new RegexTokenizer()
      .setInputCol("category")
      .setOutputCol("categoryOut")
      .setPattern(",")

    val categoryVecTransformer: Word2Vec = new Word2Vec()
      .setInputCol("categoryOut")
      .setOutputCol("category_vec")
      .setVectorSize(20)
      .setMinCount(0)

    val flagsRT: RegexTokenizer = new RegexTokenizer()
      .setInputCol("movie_flags")
      .setOutputCol("movie_flags_out")
      .setPattern(",")

    val userFlagVecTransformer: Word2Vec = new Word2Vec()
      .setInputCol("movie_flags_out")
      .setOutputCol("movie_flags_vec")
      .setVectorSize(20)
      .setMinCount(0)

    val sqlSelector: SQLTransformer = new SQLTransformer()
      .setStatement(
        """
          |select clicked, category_vec, movie_flags_vec from __THIS__
          |""".stripMargin)
    val rf: RFormula = new RFormula()
      .setFormula("clicked ~ category_vec + movie_flags_vec")


    val stages = Array(categoryRT, categoryVecTransformer, flagsRT, userFlagVecTransformer, sqlSelector, rf)

    val pipeline: Pipeline = new Pipeline().setStages(stages)

    pipeline.fit(dataDF).transform(dataDF).show(false)


    //    supervised.fit(userFlagsProcessDF).transform(userFlagsProcessDF).show(false)


  }
}
