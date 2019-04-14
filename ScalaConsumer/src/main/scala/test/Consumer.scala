package test

import java.time.Duration
import java.util.Properties

import java.io.File
import java.io.FileWriter
import java.io.Writer

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConverters._

class Consumer {

  val props: Properties = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumergroup")

  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
  consumer.subscribe(List("AlerteBateaux").asJava)
  val file = new File("data/donnees.txt")
  while(true){
    val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100))
    records.asScala.foreach { record =>
      val writer = new FileWriter("data/donnees.txt", true)
      println(s"${record.key()}, alerte : ${record.value()}")
      writer.write(s"${record.key()}, ${record.value}, \n")
      writer.close()
    }
    Thread.sleep(50)
  }


  consumer.commitSync()
}