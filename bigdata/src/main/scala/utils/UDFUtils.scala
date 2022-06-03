package utils

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

object UDFUtils extends Serializable {
  val matchFlagUDF: UserDefinedFunction = udf((category: String, movieFlag: String) => {
    val categories = category.split(",")
    val movieFlags = movieFlag.split(",")
    var contained = false
    for (flag <- movieFlags) {
      if (categories.contains(flag)) {
        contained = true
      }
    }
    if (contained) {
      //即便包含，加一下杂音
      val d: Double = Math.random()
      if (d > 0.2) {
        1
      } else {
        0
      }
    } else {
      0
    }
  })
}
