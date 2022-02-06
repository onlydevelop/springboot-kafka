# Springboot with Kafka

## Why

Just to have a template of SpringBoot with Kafka for ready reference.

## What

```androiddatabinding
User -- uploads --> Uploader Service(short computation) -- saves
                           |                     |           |
                           |                   creates       |
                        [message]                |           |
                           |                     v           v
                           |                  Metadata     File
                           v                     ^           |
                          Kafka                  |           |
                           |                   updates    parses
                           v                     |           |
                       Parser Service(long computation) <----+
```

## How

```
$ docker-compose -f docker-compose-app.yml up

$ http -f POST :9090/api/v1/files file@~/data.txt
HTTP/1.1 201
Connection: keep-alive
Content-Length: 0
Date: Tue, 04 Jan 2022 17:14:54 GMT
Keep-Alive: timeout=60
Location: http://localhost:9090/api/v1/files/24

$ http :9090/api/v1/files/24
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Tue, 04 Jan 2022 17:15:44 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "id": 24,
    "letterCount": null,
    "name": "data.txt",
    "uuid": "2801eb6c-80e0-4bd8-9dd6-3d53b3596ead",
    "wordCount": null
}

# ---------- After the event is processed ----------
# ---- we get the values of letter/word counts -----

$ http :9090/api/v1/files/24
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Tue, 04 Jan 2022 17:15:44 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "id": 24,
    "letterCount": 57,
    "name": "data.txt",
    "uuid": "2801eb6c-80e0-4bd8-9dd6-3d53b3596ead",
    "wordCount": 11
}
```

## Dev

For all projects, in the Intellij IDEA, `Run > Edit Configurations > Environment Variables` put:

```
spring.profiles.active=dev
```

Then, you need to run the infra docker-compose, which will just make db, zookeeper and kafka up.

```
$ docker-compose -f docker-compose-infra.yml up
```

Then, you can run all the projects in debug.

## kubernetes

```
$ make appdown && ./build.sh && make appup # build and deploy to minikube
...
$ kubectl get svc
NAME          TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
db            ClusterIP   10.108.104.204   <none>        5432/TCP         28h
filestore     ClusterIP   10.103.75.168    <none>        80/TCP           5m25s
kafka         ClusterIP   10.101.111.7     <none>        9092/TCP         28h
parser        ClusterIP   10.105.192.139   <none>        80/TCP           5m25s
uploader      NodePort    10.96.162.132    <none>        8080:31050/TCP   5m25s
zookeeper     ClusterIP   10.111.44.4      <none>        2181/TCP         28h

# Note: The uploader exposed node port is 31050

$ http -f POST $(minikube ip):31050/api/v1/files file@~/data.txt
HTTP/1.1 201 Created
content-length: 0
date: Tue, 11 Jan 2022 14:15:19 GMT
location: http://192.168.49.2/api/v1/files/6
server: istio-envoy
x-envoy-decorator-operation: uploader.default.svc.cluster.local:8080/*
x-envoy-upstream-service-time: 2743

# Note: The Location header is mentioned without port, you need to add the port in the Location

$ http $(minikube ip):31050/api/v1/files/6
HTTP/1.1 200 OK
content-type: application/json
date: Tue, 11 Jan 2022 14:24:27 GMT
server: istio-envoy
transfer-encoding: chunked
x-envoy-decorator-operation: uploader.default.svc.cluster.local:8080/*
x-envoy-upstream-service-time: 64

{
    "fileStoreId": 5,
    "id": 6,
    "letterCount": 71,
    "name": "data.txt",
    "uuid": "be1462f7-b22e-48b8-8c51-da69977b1243",
    "wordCount": 16
}
```

1
