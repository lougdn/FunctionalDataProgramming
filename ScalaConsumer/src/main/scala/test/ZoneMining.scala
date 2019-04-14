package test

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

object ZoneMining {

  val pathToFile = "data/donnees.txt"

  def LoadData() : RDD[String] = {

    val conf = new SparkConf()
      .setAppName("Zone Mining")
      .setMaster("local[*]")

    val sc = SparkContext
      .getOrCreate(conf)

    sc.textFile(pathToFile)
      .flatMap(_.split(", "))
  }

  //Zone avec le plus d'alertes
  def zoneAlerte(): RDD[String] = {
    LoadData()
      .map(_.text)
      .flatMap(_.split(", "))
      .filter(_.startWith("zone"))
      .filter(_.length > 4)
      .map(word => (word,1))
      .reduceByKey((acc,i) => acc + i)
  }
}
