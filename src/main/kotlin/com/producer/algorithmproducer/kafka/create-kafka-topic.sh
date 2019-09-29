#!/bin/bash

NUM_RETRIES=30
SLEEP_TIME=5

for i in $(seq 1 "$NUM_RETRIES"); do
	BROKER_COUNT=$(/opt/kafka_2.12-1.0.1/bin/zookeeper-shell.sh zookeeper-1.vnet:2181 <<< "ls /brokers/ids" | grep -oc "\[0\]")

	if [ $BROKER_COUNT -eq 1 ]; then
		/opt/kafka_2.12-1.0.1/bin/kafka-topics.sh --zookeeper zookeeper-1.vnet:2181 --create --topic algorithm_complete --partitions 1 --replication-factor 1
		/opt/kafka_2.12-1.0.1/bin/kafka-topics.sh --zookeeper zookeeper-1.vnet:2181 --create --topic theErrors --partitions 1 --replication-factor 1
	    exit 0
	fi
    sleep "$SLEEP_TIME"
done

exit 1