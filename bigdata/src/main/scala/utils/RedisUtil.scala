package utils

import redis.clients.jedis.{JedisPool, JedisPoolConfig}

object RedisUtil {
  private val conf: JedisPoolConfig = new JedisPoolConfig
  conf.setMaxTotal(100)
  conf.setMaxIdle(10)
  conf.setMinIdle(10)
  conf.setBlockWhenExhausted(true)   // 忙碌时间是否等待
  conf.setMaxWaitMillis(1000)        // 最大等待时间
  conf.setTestOnBorrow(true)
  conf.setTestOnReturn(true)

  val host = PropertiesUtil.getPropString("redis.host")
  val password = PropertiesUtil.getPropString("redis.password")

  val pool: JedisPool = new JedisPool(conf,host,6379, 10000, password)

  def getClient = pool.getResource
}
