package utils

import java.util.Properties

object PropertiesUtil {

  def getProperties(): Properties = {
    val properties = new Properties()
    //load resource in prod.properties
    val reader = this.getClass.getResourceAsStream("/app.properties") //生产环境
    //val reader=this.getClass.getResourceAsStream("/dev.properties") //开发环境
    properties.load(reader)
    properties
  }

  def getPropString(key: String): String = {
    getProperties().getProperty(key)
  }

  def getPropInt(key: String): Int = {
    getProperties().getProperty(key).toInt
  }

  def getPropBoolean(key: String): Boolean = {
    getProperties().getProperty(key).toBoolean
  }
}
