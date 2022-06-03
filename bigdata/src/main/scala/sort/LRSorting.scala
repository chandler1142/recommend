package sort

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.RFormula
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit, TrainValidationSplitModel}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.PropertiesUtil

import java.util.Properties

object LRSorting extends Serializable {


  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("error")
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
      .withColumn("clicked", when(col("event") === "NotInterested", 0).otherwise(1))
      .select("clicked", "movie_id", "user_id")

    val rawDataDF: DataFrame = userBHDF.join(movieDF, userBHDF("movie_id") === movieDF("o_movie_id"))
      .join(userDF, userBHDF("user_id") === userDF("o_user_id"))
      .select("clicked", "category", "movie_flags", "age", "sex")

    //"喜剧","情色","科幻","运动","恐怖","儿童","灾难","同性","犯罪","西部","动画","传记","纪录片","惊悚","冒险","奇幻","歌舞","历史","悬疑","古装","音乐","剧情","短片","黑色电影","武侠","爱情","家庭","战争","动作"
    val dataDF: DataFrame = rawDataDF
      .withColumn("u_xiju", when(col("category").contains("喜剧"), 1).otherwise(0))
      .withColumn("u_qingse", when(col("category").contains("情色"), 1).otherwise(0))
      .withColumn("u_kehuan", when(col("category").contains("科幻"), 1).otherwise(0))
      .withColumn("u_yundong", when(col("category").contains("运动"), 1).otherwise(0))
      .withColumn("u_kongbu", when(col("category").contains("恐怖"), 1).otherwise(0))
      .withColumn("u_ertong", when(col("category").contains("儿童"), 1).otherwise(0))
      .withColumn("u_zainan", when(col("category").contains("灾难"), 1).otherwise(0))
      .withColumn("u_tongxing", when(col("category").contains("同性"), 1).otherwise(0))
      .withColumn("u_fanzui", when(col("category").contains("犯罪"), 1).otherwise(0))
      .withColumn("u_xibu", when(col("category").contains("西部"), 1).otherwise(0))
      .withColumn("u_donghua", when(col("category").contains("动画"), 1).otherwise(0))
      .withColumn("u_zhuanji", when(col("category").contains("传记"), 1).otherwise(0))
      .withColumn("u_jilupian", when(col("category").contains("纪录片"), 1).otherwise(0))
      .withColumn("u_jingsong", when(col("category").contains("惊悚"), 1).otherwise(0))
      .withColumn("u_maoxian", when(col("category").contains("冒险"), 1).otherwise(0))
      .withColumn("u_qihuan", when(col("category").contains("奇幻"), 1).otherwise(0))
      .withColumn("u_gewu", when(col("category").contains("歌舞"), 1).otherwise(0))
      .withColumn("u_lishi", when(col("category").contains("历史"), 1).otherwise(0))
      .withColumn("u_xuanyi", when(col("category").contains("悬疑"), 1).otherwise(0))
      .withColumn("u_guzhuang", when(col("category").contains("古装"), 1).otherwise(0))
      .withColumn("u_yinyue", when(col("category").contains("音乐"), 1).otherwise(0))
      .withColumn("u_juqing", when(col("category").contains("剧情"), 1).otherwise(0))
      .withColumn("u_duanpian", when(col("category").contains("短片"), 1).otherwise(0))
      .withColumn("u_heisedianying", when(col("category").contains("黑色电影"), 1).otherwise(0))
      .withColumn("u_wuxia", when(col("category").contains("武侠"), 1).otherwise(0))
      .withColumn("u_aiqing", when(col("category").contains("爱情"), 1).otherwise(0))
      .withColumn("u_jiating", when(col("category").contains("家庭"), 1).otherwise(0))
      .withColumn("u_zhanzheng", when(col("category").contains("战争"), 1).otherwise(0))
      .withColumn("u_dongzuo", when(col("category").contains("动作"), 1).otherwise(0))
      .withColumn("o_xiju", when(col("movie_flags").contains("喜剧"), 1).otherwise(0))
      .withColumn("o_qingse", when(col("movie_flags").contains("情色"), 1).otherwise(0))
      .withColumn("o_kehuan", when(col("movie_flags").contains("科幻"), 1).otherwise(0))
      .withColumn("o_yundong", when(col("movie_flags").contains("运动"), 1).otherwise(0))
      .withColumn("o_kongbu", when(col("movie_flags").contains("恐怖"), 1).otherwise(0))
      .withColumn("o_ertong", when(col("movie_flags").contains("儿童"), 1).otherwise(0))
      .withColumn("o_zainan", when(col("movie_flags").contains("灾难"), 1).otherwise(0))
      .withColumn("o_tongxing", when(col("movie_flags").contains("同性"), 1).otherwise(0))
      .withColumn("o_fanzui", when(col("movie_flags").contains("犯罪"), 1).otherwise(0))
      .withColumn("o_xibu", when(col("movie_flags").contains("西部"), 1).otherwise(0))
      .withColumn("o_donghua", when(col("movie_flags").contains("动画"), 1).otherwise(0))
      .withColumn("o_zhuanji", when(col("movie_flags").contains("传记"), 1).otherwise(0))
      .withColumn("o_jilupian", when(col("movie_flags").contains("纪录片"), 1).otherwise(0))
      .withColumn("o_jingsong", when(col("movie_flags").contains("惊悚"), 1).otherwise(0))
      .withColumn("o_maoxian", when(col("movie_flags").contains("冒险"), 1).otherwise(0))
      .withColumn("o_qihuan", when(col("movie_flags").contains("奇幻"), 1).otherwise(0))
      .withColumn("o_gewu", when(col("movie_flags").contains("歌舞"), 1).otherwise(0))
      .withColumn("o_lishi", when(col("movie_flags").contains("历史"), 1).otherwise(0))
      .withColumn("o_xuanyi", when(col("movie_flags").contains("悬疑"), 1).otherwise(0))
      .withColumn("o_guzhuang", when(col("movie_flags").contains("古装"), 1).otherwise(0))
      .withColumn("o_yinyue", when(col("movie_flags").contains("音乐"), 1).otherwise(0))
      .withColumn("o_juqing", when(col("movie_flags").contains("剧情"), 1).otherwise(0))
      .withColumn("o_duanpian", when(col("movie_flags").contains("短片"), 1).otherwise(0))
      .withColumn("o_heisedianying", when(col("movie_flags").contains("黑色电影"), 1).otherwise(0))
      .withColumn("o_wuxia", when(col("movie_flags").contains("武侠"), 1).otherwise(0))
      .withColumn("o_aiqing", when(col("movie_flags").contains("爱情"), 1).otherwise(0))
      .withColumn("o_jiating", when(col("movie_flags").contains("家庭"), 1).otherwise(0))
      .withColumn("o_zhanzheng", when(col("movie_flags").contains("战争"), 1).otherwise(0))
      .withColumn("o_dongzuo", when(col("movie_flags").contains("动作"), 1).otherwise(0))
      .drop("category", "movie_flags")


    dataDF.show(20, false)
    val Array(train, test) = dataDF.randomSplit(Array(0.8, 0.2))

    val lr = new LogisticRegression().setLabelCol("label").setFeaturesCol("features")

    val rf: RFormula = new RFormula()
      .setFormula("clicked ~ .")
    val stage = Array(rf, lr)
    val pipeline = new Pipeline().setStages(stage)

    val params = new ParamGridBuilder()
      .addGrid(lr.maxIter, Array(500))
      .build()

    val evaluator: BinaryClassificationEvaluator = new BinaryClassificationEvaluator()
      .setMetricName("areaUnderPR")
      .setRawPredictionCol("prediction")
      .setLabelCol("label")

    val tvs: TrainValidationSplit = new TrainValidationSplit()
      .setTrainRatio(0.75)
      .setEstimatorParamMaps(params)
      .setEstimator(pipeline)
      .setEvaluator(evaluator)

    val tvsFitted: TrainValidationSplitModel = tvs.fit(train)

    val predictionRate: Double = evaluator.evaluate(tvsFitted.transform(test))
    println("PRECISION: " + predictionRate)

    val out: RDD[(Double, Double)] = tvsFitted.transform(test)
      .select("prediction", "label").limit(20)
      .rdd.map(x => (x(0).asInstanceOf[Double], x(1).asInstanceOf[Double]))
    out.foreach(println)

  }
}
