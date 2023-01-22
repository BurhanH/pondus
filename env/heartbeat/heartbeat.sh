#!/bin/sh

export USER=`whoami`
export HOST=`hostname`

env|sort

function send_data(){
    data="heartbeat,host=$HOST,user=$USER value=$((1 + RANDOM % 100)) $(date +%s000000001)"
    echo $data
    curl -sw '%{http_code} %{url_effective}\n' --request POST "${INFLUX_URL}/api/v2/write?org=${DOCKER_INFLUXDB_INIT_ORG}&bucket=${DOCKER_INFLUXDB_INIT_BUCKET}"  \
         --header "Authorization: Token ${DOCKER_INFLUXDB_INIT_ADMIN_TOKEN}" \
         --data-raw "$data"
}

while [ true ]
do
   send_data
   sleep 10
done