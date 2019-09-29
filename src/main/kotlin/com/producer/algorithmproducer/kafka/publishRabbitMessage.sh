#!/bin/bash

sleep 5
#THIS WORKS
cat /var/www/html/hell_world  | ./kafka-console-producer.sh --broker-list localhost:9092 --topic algorithm_complete --property "parse.key=true" --property "key.separator=:"

#Original
#cat /var/www/html/hell_world | ./opt/kafka_2.12-1.0.1/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic algorithm_complete --property "parse.key=true" --property "key.separator=:"

#cat /var/www/html/hell_world | ./kafka-console-producer.sh --broker-list localhost:9092 --topic topic1


#test by checking consumer
#./kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic algorithm_complete


#TODO - finish kafka producer config and setup spring integration flow
#connect config to docker instance
