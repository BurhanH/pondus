FROM alpine:3.17.0

RUN apk add --no-cache curl

ADD heartbeat.sh /heartbeat.sh

ENTRYPOINT /heartbeat.sh