#!/bin/bash

docker-compose up -d --force-recreate

#docker cp create-kafka-topic.sh kafka-1:/tmp/create-kafka-topic.sh
docker cp publishSampleMessage.sh kafka-1:/tmp/publishSampleMessage.sh
docker cp publishRabbitMessage.sh kafka-1:/tmp/publishRabbitMessage.sh

#docker exec kafka-1 /tmp/create-kafka-topic.sh
docker exec kafka-1 /tmp/publishRabbitMessage.sh
docker exec kafka-1 /tmp/publishSampleMessage.sh

#./kafka-topics.sh --list --zookeeper zookeeper-1.vnet:2181 - verify topics !! (zookeeper1.vnet:2181)

# LOCATION OF aGLO files (maybe switch to json) inside Kafka container:
# /var/www/html/hell_world


#LOCATION of ALL KAFKA scripts including console-producer:
#/opt/kafka_2.12-1.0.1/bin/

#RUN ALL SCRIPTS with ./ in front from the bin folder

#COPY IN FILES AGAIN (MAYBE Switch to json)

#TURN ON APP TO TEST KAFKA CONFIG
#use docker property or script to create topics? We can remove the script im sure
#find spring.kafka.producer setting
#change publish scripts to use ./ without the root?

#ICE BOX
#Add validation error advice class to handle routing and other component errors
