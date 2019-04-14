DESCRIPTION :

Notre projet concerne une flotte de bateau de pêche.
Chaque bateau est composé des éléments suivants :
    Immatriculation
    Nombre de couchette
    Capacité de la cale (en kg)
    Poids actuel dans la cale (en kg)
    Zone actuelle

Chaque zone est, elle, caractérisée par :
    Nom
    Météo prévue
    Superficie
    Autorisation de pêche

Il existe 5 différentes alertes qu'un bateau peut déclencher:
    La cale est bientôt pleine (Lorsque le poids actuel de la cale est supérieur ou égal à 80% de la capacité maximale du bateau)
    La cale est pleine
    Carburant faible
    Zone interdite à la pêche
    Bulletin météo - conditions dangereuses



ARCHITECTURE :

Le projet s'articule autour d'un producer et d'un consumer kafka

Dans le producer :
    Les bateaux et les zones sont représentés par des listes bidimensionnelles
    Les données des bateaux sont modifiées aléatoirement :
        Poids dans la cale
        Zone actuelle (1 si interdit à la pêche)
        Carburant suffisant ou non (1 si insuffisant)

Dans le consumer :
    On récupère les données envoyées par le producer
    On enregistre les données dans un fichier données.txt sous le format "Immatriculation, Zone, Alerte"
    Le ZoneMining qui permet d'analyser les données stockées dans ce fichier, comme le nombre d'alertes provoquées par zone


LANCEMENT :

Pour lancer le projet, il suffit d'utiliser les commandes suivantes depuis deux terminaux:
    kafka_2.12-2.1.0/bin/zookeeper-server-start.sh kafka_2.12-2.1.0/config/zookeeper.properties
    kafka_2.12-2.1.0/bin/kafka-server-start.sh kafka_2.12-2.1.0/config/server.properties

Puis, d'ouvrir les deux projets ScalaConsumer et ScalaProducer.
Il faut alors Run les Main qui se trouvent dans le fichier Principal.scala de chaque projet
