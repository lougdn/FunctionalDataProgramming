package test

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD


class Producer {

  /*val zonesFile = "data/zones.txt"

  def loadData(): RDD[String] = {
    val conf = new SparkConf().setAppName("zonesAlert").setMaster("local[*]")
    val sc = SparkContext.getOrCreate(conf)
    sc.textFile(zonesFile).flatMap(_.split(" "))
  }*/
  val topic = "AlerteBateaux"
  val alerts = List("La cale est bientôt pleine", "La cale est pleine", "Carburant faible", "Zone interdite à la pêche", "Bulletin météo - conditions dangereuses")

  val props: Properties = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])

  val producer: KafkaProducer[String,String] = new KafkaProducer[String,String](props)


  val zones = List(List("zone1", 0, 100, 1), List("zone1", 1, 100, 1),
    List("zone2", 0, 100, 0), List("zone2", 1, 100, 0),
    List("zone3", 0, 100, 1), List("zone3", 1, 100, 1),
    List("zone4", 0, 100, 0), List("zone4", 1, 100, 1))

  val bateaux = List(List("NO35672", 15, 1000, 0, "zone1"),
    List("LS35452", 15, 1000, 0, "zone1"),
    List("YE34657", 15, 2000, 0, "zone2"),
    List("NO35789", 4, 500, 0, "zone3"),
    List("NO35609", 6, 800, 0, "zone4"),
    List("NO87672", 2, 300, 0, "zone4"))

  //fonction pour modifier au hasard l'emplacement d'un bateau et son remplissement
  def randomInfosBateaux() {
    //on prend au hasard un bateau que l'on va faire évoluer
    val indexBateau = scala.util.Random
    val boat = bateaux(indexBateau.nextInt(6))

    //on prend au hasard une zone dans laquelle on va le placer
    val indexZone = scala.util.Random
    val zone = zones(indexZone.nextInt(8))
    val nameZone = zone(0)

    //on change le poids dans la cale aléatoirement
    val capacity = boat(2).asInstanceOf[Int] + 1
    val nbPoids = scala.util.Random
    val poids = nbPoids.nextInt(capacity)

    //bateau actualisé
    val bateau = List(boat(0), boat(1), boat(2), poids, nameZone)

    //la clé envoyée est l'immatriculation du bateau qui reçoit l'alerte
    val key = bateau(0).toString

    //quantité carburant faible, complètement random
    val carburant = scala.util.Random
    val carbu = carburant.nextInt(2)

    println("\n\nnouveau bateau")
    println(bateau)

    //on prend au hasard une zone parmi celles qui ont le même que celle dans laquelle le bateau vient d'entrer
    val randomZone = zones.filter(_(0) == nameZone)
    val choixZone = randomZone(indexZone.nextInt(2))
    println(choixZone)

    //si cale pleine à 80% ou plus mais pas pleine, alerte "La cale est bientôt pleine"
    if(bateau(3).asInstanceOf[Int] >= bateau(2).asInstanceOf[Int]*0.8 && bateau(3).asInstanceOf[Int] < bateau(2).asInstanceOf[Int]) {
      val record = new ProducerRecord[String, String](topic, key, nameZone + ", " + alerts(0))
      producer.send(record)
      println(record)
    }
    //si la cale est pleine
    else if (bateau(3).asInstanceOf[Int] == bateau(2).asInstanceOf[Int]) {
      val record = new ProducerRecord[String, String](topic, key, nameZone + ", " + alerts(1))
      producer.send(record)
      println(record)
    }

    //si la zone est interdite (4e élément de la liste = 1)
    if (choixZone(3).asInstanceOf[Int] == 1) {
      val record = new ProducerRecord[String, String](topic, key, nameZone + ", " + alerts(3))
      producer.send(record)
      println(record)
      //si la météo est dangereuse (2e élément de la liste = 1)
      if (choixZone(1).asInstanceOf[Int] == 1) {
        val record = new ProducerRecord[String, String](topic, key, nameZone + ", " + alerts(4))
        producer.send(record)
        println(record)
      }
    }
    // si la météo est mauvaise dans une zone autorisée
    else if (choixZone(1).asInstanceOf[Int] == 1) {
      val record = new ProducerRecord[String, String](topic, key, nameZone + ", " + alerts(4))
      producer.send(record)
      println(record)
    }

    if (carbu == 1){
      val record = new ProducerRecord[String, String](topic, key, nameZone + ", " + alerts(2))
      producer.send(record)
      println(record)
    }
  }



  while(true){
    randomInfosBateaux()
    Thread.sleep(5000)
  }

  producer.close()
}

