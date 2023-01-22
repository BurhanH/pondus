# Performance monitoring infrastructure

This solution is to monitor real-time performance for certain metrics.

Used stack: Docker, Telegraf, InfluxDB, Grafana.

The main goal is to make a workable solution to collect real-time performance metrics from the Gatling tool and save those metrics in a database.

## How to up infrastructure:

```
docker-compose up -d
```

## Monitoring

Login to Grafana - http://localhost:3000

Login to InfluxDB - http://localhost:8086


## How to down infrastructure:

```
docker-compose down
```

## TODOs

0. Add Gatling integration (workable sample)
1. Make data volumes persistent
2. Add more metrics and different dashboards
3. Make Gatling sample as a part of Docker configuration
4. Add different scenarious for load testing (like: RESR API testing, gRPC, Web)
5. Create a dummy service for load testing
6. Implement metrics collection from a dummy service (like: CPU, RAM, database metrics, etc.)