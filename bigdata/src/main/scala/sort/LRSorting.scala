package sort

import java.util.Properties

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.{GBTClassifier, LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{RFormula, RegexTokenizer, SQLTransformer, Word2Vec}
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit, TrainValidationSplitModel}
import org.apache.spark.sql.functions.{col, _}
import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.PropertiesUtil

object LRSorting extends Serializable {


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
    val movieDF: DataFrame = spark.read.jdbc(mysqlUrl, "movies", prop)
      .select(col("id"), col("category"), col("lang"))
      .withColumnRenamed("id", "o_movie_id")
    val userDF: DataFrame = spark.read.jdbc(mysqlUrl, "user", prop)
      .select(col("id"), col("age"), col("sex"), col("movie_flags"))
      .withColumnRenamed("id", "o_user_id")

    val userBHDF: DataFrame = spark.read.jdbc(mysqlUrl, "user_behavior", prop)
      .withColumn("clicked", when(col("event") === "NotInterested", 0).otherwise(1))
      .select("clicked", "movie_id", "user_id")

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
      .setVectorSize(30)
      .setMinCount(0)

    val flagsRT: RegexTokenizer = new RegexTokenizer()
      .setInputCol("movie_flags")
      .setOutputCol("movie_flags_out")
      .setPattern(",")

    val userFlagVecTransformer: Word2Vec = new Word2Vec()
      .setInputCol("movie_flags_out")
      .setOutputCol("movie_flags_vec")
      .setVectorSize(30)
      .setMinCount(0)

    val sqlSelector: SQLTransformer = new SQLTransformer()
      .setStatement(
        """
          |select clicked, category_vec, movie_flags_vec from __THIS__
          |""".stripMargin)

    val lr = new GBTClassifier()

    val stages1 = Array(categoryRT, categoryVecTransformer, flagsRT, userFlagVecTransformer, sqlSelector)

    val pipeline1: Pipeline = new Pipeline().setStages(stages1)

    val preparedDF: DataFrame = pipeline1.fit(dataDF).transform(dataDF)
    preparedDF.show(false)
    val Array(train, test) = preparedDF.randomSplit(Array(0.75, 0.25))


    //2. 准备训练
    val rf: RFormula = new RFormula()
    val stage2 = Array(rf, lr)
    val pipeline2 = new Pipeline().setStages(stage2)

    val params = new ParamGridBuilder()
      .addGrid(rf.formula, Array("clicked ~ category_vec + movie_flags_vec"))
//      .addGrid(lr.elasticNetParam, Array(0.0, 0.5, 1.0))
//      .addGrid(lr.regParam, Array(0.1, 2.0))
//      .addGrid(lr.maxIter, Array(100))
      .build()

    val evaluator: BinaryClassificationEvaluator = new BinaryClassificationEvaluator()
      .setMetricName("areaUnderROC")
      .setRawPredictionCol("prediction")
      .setLabelCol("label")

    val tvs: TrainValidationSplit = new TrainValidationSplit().setTrainRatio(0.75)
      .setEstimatorParamMaps(params)
      .setEstimator(pipeline2)
      .setEvaluator(evaluator)

    val tvsFitted: TrainValidationSplitModel = tvs.fit(train)

    val predictionRate: Double = evaluator.evaluate(tvsFitted.transform(test))
    println("PRECISION: "  + predictionRate)

//    val trainedPipeline = tvsFitted.bestModel.asInstanceOf[PipelineModel]
//    val trainedLR: LogisticRegressionModel = trainedPipeline.stages(1).asInstanceOf[LogisticRegressionModel]
//    val summaryLR = trainedLR.summary
//    summaryLR.objectiveHistory.foreach(println)

    //    val Array(train, test) = preparedDF.randomSplit(Array(0.75, 0.2))

    //    val lr: LogisticRegression = new LogisticRegression()
    //      .setMaxIter(50)
    //      .setRegParam(0.5)
    //      .setElasticNetParam(0.5)
    //      .setLabelCol("label")
    //      .setFeaturesCol("features")
    //    println(lr.explainParams())

    //    val fittedLR: LogisticRegressionModel = lr.fit(train)
    //    fittedLR.transform(train).select("label", "prediction").show(false)
    //
    //    println(fittedLR.evaluate(test))
    //    println("*"*100)
    //    println(fittedLR.summary.objectiveHistory)

  }
}
