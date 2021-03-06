package sort

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import javax.xml.transform.stream.StreamResult
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.RFormula
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit, TrainValidationSplitModel}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.jpmml.model.JAXBUtil
import org.jpmml.sparkml.{ConverterUtil, PMMLBuilder}
import utils.PropertiesUtil
import utils.UDFUtils.matchFlagUDF


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

    //"??????","??????","??????","??????","??????","??????","??????","??????","??????","??????","??????","??????","?????????","??????","??????","??????","??????","??????","??????","??????","??????","??????","??????","????????????","??????","??????","??????","??????","??????"
    val dataDF: DataFrame = rawDataDF
      .withColumn("matchFlag", matchFlagUDF(col("category"), col("movie_flags")))
//      .withColumn("u_xiju", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_qingse", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_kehuan", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_yundong", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_kongbu", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_ertong", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_zainan", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_tongxing", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_fanzui", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_xibu", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_donghua", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_zhuanji", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_jilupian", when(col("category").contains("?????????"), 1).otherwise(0))
//      .withColumn("u_jingsong", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_maoxian", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_qihuan", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_gewu", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_lishi", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_xuanyi", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_guzhuang", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_yinyue", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_juqing", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_duanpian", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_heisedianying", when(col("category").contains("????????????"), 1).otherwise(0))
//      .withColumn("u_wuxia", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_aiqing", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_jiating", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_zhanzheng", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("u_dongzuo", when(col("category").contains("??????"), 1).otherwise(0))
//      .withColumn("o_xiju", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_qingse", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_kehuan", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_yundong", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_kongbu", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_ertong", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_zainan", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_tongxing", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_fanzui", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_xibu", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_donghua", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_zhuanji", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_jilupian", when(col("movie_flags").contains("?????????"), 1).otherwise(0))
//      .withColumn("o_jingsong", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_maoxian", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_qihuan", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_gewu", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_lishi", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_xuanyi", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_guzhuang", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_yinyue", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_juqing", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_duanpian", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_heisedianying", when(col("movie_flags").contains("????????????"), 1).otherwise(0))
//      .withColumn("o_wuxia", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_aiqing", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_jiating", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_zhanzheng", when(col("movie_flags").contains("??????"), 1).otherwise(0))
//      .withColumn("o_dongzuo", when(col("movie_flags").contains("??????"), 1).otherwise(0))
      .drop("category", "movie_flags")


    dataDF.show(20, false)
    val Array(train, test) = dataDF.randomSplit(Array(0.8, 0.2))

    val lr = new LogisticRegression().setLabelCol("label").setFeaturesCol("features")

    val rf: RFormula = new RFormula()
      .setFormula("clicked ~ .")
    val stage = Array(rf, lr)
    val pipeline = new Pipeline().setStages(stage)

    val model: PipelineModel = pipeline.fit(train)
//    val params = new ParamGridBuilder()
//      .addGrid(lr.maxIter, Array(500))
//      .addGrid(lr.standardization, Array(true))
//      .build()
//
//    val evaluator: BinaryClassificationEvaluator = new BinaryClassificationEvaluator()
//      .setMetricName("areaUnderROC")
//      .setRawPredictionCol("prediction")
//      .setLabelCol("label")
//
//    val tvs: TrainValidationSplit = new TrainValidationSplit()
//      .setTrainRatio(0.75)
//      .setEstimatorParamMaps(params)
//      .setEstimator(pipeline)
//      .setEvaluator(evaluator)
//
//    val tvsFitted: TrainValidationSplitModel = tvs.fit(train)

//    val predictionRate: Double = evaluator.evaluate(tvsFitted.transform(test))
//    println("PRECISION: " + predictionRate)
//
//    val out = tvsFitted.transform(test)
//      .select("probability", "prediction", "label").limit(20)
//    //      .rdd.map(x => (x(0).asInstanceOf[Double], x(1).asInstanceOf[Double]))
//    out.rdd.foreach(println)

    //????????????
//    val path = "./model/lr/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date())
//    tvsFitted.write.overwrite().save(path)

    val pmml = new PMMLBuilder(train.schema, model).build()
    JAXBUtil.marshalPMML(pmml, new StreamResult(new File("./model/pmml/lr.pmml")))
  }
}
