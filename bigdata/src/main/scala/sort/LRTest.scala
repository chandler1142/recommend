package sort

import com.ai.models.MovieLRInputModel
import com.ai.service.MovieLRService

object LRTest {
  def main(args: Array[String]): Unit = {
    val lrService = new MovieLRService;
    val value = lrService.score(new MovieLRInputModel(31, 1, 1))
    println(value)
  }
}
