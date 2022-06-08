package test

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.sql.SparkSession

object MllibTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .getOrCreate()

    // Prepare training documents from a list of (id, text, label) tuples.
    val training = spark.createDataFrame(Seq(
      (0L, "a b c d e spark", 1.0),
      (1L, "b d", 0.0),
      (2L, "spark f g h", 1.0),
      (3L, "hadoop mapreduce", 0.0)
    )).toDF("id", "text", "label")
    val test = spark.createDataFrame(Seq(
      (0L, "d spark", 1.0),
      (1L, "a b", 0.0),
      (2L, "spark a b", 1.0),
      (3L, "hello world", 0.0)
    )).toDF("id", "text", "label")
    //transformer: package org.apache.spark.ml.feature
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val hashingTF = new HashingTF().setNumFeatures(1000).setInputCol(tokenizer.getOutputCol).setOutputCol("features")
    //estimator
    val lr = new LogisticRegression().setMaxIter(10).setRegParam(0.001)
    //pipeline
    val pipeline = new Pipeline().setStages(Array(tokenizer, hashingTF, lr))

    // Fit/train the pipeline to training documents.
    val model = pipeline.fit(training)
    //evaluator
    val evaluator = new BinaryClassificationEvaluator().setMetricName("areaUnderROC").setRawPredictionCol("prediction").setLabelCol("label")
    val result: Double = evaluator.evaluate(model.transform(test))
    println(result)

    model.transform(test).select("probability", "prediction", "label").rdd.foreach(println)

    spark.stop()
  }
}
