#!/bin/bash

sleep 5
#cat /var/www/html/palindrome | /opt/kafka_2.12-1.0.1/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic algorithm_complete
cat /var/www/html/palindrome | ./kafka-console-producer.sh --broker-list localhost:9092 --topic algorithm_complete --property "parse.key=true" --property "key.separator=:"
