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
    if(contained) {
      1
    } else {
      0
    }
  })
}
