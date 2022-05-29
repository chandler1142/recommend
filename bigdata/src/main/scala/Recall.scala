import org.apache.spark.broadcast.Broadcast
import org.apache.spark.ml.recommendation.ALSModel
import org.apache.spark.sql.{DataFrame, SparkSession}
import recall.{ALSRecall, ItemCFRecall}
import utils.{ModelUtil, PropertiesUtil}

import java.text.SimpleDateFormat
import java.util.{Date, Properties}

object Recall {

  val clALS = "als"
  val colItem2Item = "itemCF"

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
    val ratingDF: DataFrame = spark.read.jdbc(mysqlUrl, "dws_user_item_rating", prop)
    ratingDF.createOrReplaceTempView("dws_user_item_rating")

    val modelUtil = ModelUtil(spark)
    val data: DataFrame = modelUtil.getUserItemRating

    //召回1: ALS
    val als = ALSRecall(data)
    //迭代次数, 指定为10
    val maxIter = 1
    val reg = Array(0.1)
    //维度数目
    val rank = Array(20)
    //学习率
    val alpha = Array(2.0)

    //生成最优的ALS模型
    val model: ALSModel = als.getModel(maxIter, rank, reg, alpha)
    val alsRecallData: DataFrame = als.getALSRecall(model, spark)

    //存储候选集
    modelUtil.saveRecall(alsRecallData, clALS)

    /**
     *
     * 获取上一个任务存储的ALS模型生成的物品特征向量
     */
//    val format = new SimpleDateFormat("yyyy-MM-dd")
//    val date = format.format(new Date())
//    val path = "/model/als_model/"+date+"/itemFactors"
//
//    val itemFactors = spark.read.parquet(path)


//    //生成候选集
//    //召回2：基于物品的协同过滤
//    val item2Item = ItemCFRecall()
//    //获取物品的相似度矩阵 相似度采用余弦相似度
//    /**
//     * 耗时约30m
//     *
//     * */
//    val itemCosSim = item2Item.getCosSim(itemFactors,spark)
//    //广播相似度矩阵
//    val itemCosSimBd:Broadcast[DataFrame] =
//      spark.sparkContext.broadcast(itemCosSim)
//    //获取推荐
//    val item2ItemRecallData
//    =  item2Item.getItem2ItemRecall(data,
//      itemCosSimBd,spark)
//    //存储候选集
//    modelUtil.saveRecall(item2ItemRecallData,colItem2Item)


    spark.stop()
  }
}
