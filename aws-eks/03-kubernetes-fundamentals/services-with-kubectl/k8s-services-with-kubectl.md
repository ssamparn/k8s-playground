# Kubernetes - Services

## Introduction to Services
- **Service Types**
    1. ClusterIp
    2. NodePort
    3. LoadBalancer
    4. ExternalName
- We are going to look in to ClusterIP and NodePort.
- LoadBalancer Type is primarily for cloud providers, and it will differ cloud to cloud, so we will do it accordingly (per cloud basis)
- ExternalName doesn't have Imperative commands, and we need to write YAML definition for the same, so we will look in to it as and when it is required.

## ClusterIP Service - Backend Application Setup
- Create a deployment for Backend Application (Spring Boot REST Application)
- Create a ClusterIP service for load balancing backend application.

### Build and push backend docker image
```bash
$ cd aws-eks/03-kubernetes-fundamentals/resources/services/kube-helloworld/
$ mvn clean install
$ docker images
$ docker push ssamantr/kube-helloworld:1.0.0
```

```bash
# Create Deployment for Backend Rest App
$ kubectl create deployment backend-rest-app --image=ssamantr/kube-helloworld:1.0.0
$ kubectl get deploy

# Create ClusterIp Service for Backend Rest App
$ kubectl expose deployment backend-rest-app --port=8080 --target-port=8080 --name=backend-service
$ kubectl get svc
Observation: We don't need to specify "--type=ClusterIp" because default setting is to create ClusterIp Service. 
```
- **Important Note:** If backend application port (Container Port: 8080) and Service Port (8080) are same we don't need to use **--target-port=8080** but for avoiding the confusion we have added it. Same case applies to frontend application and service.

## NodePort Service - Frontend Application Setup
- We have implemented **NodePort Service** multiple times so far (in pods, replicasets and deployments), even then we are going to implement one more time to get a full architectural view in relation with ClusterIp service.
- Create a deployment for Frontend Application (Nginx acting as Reverse Proxy)
- Create a NodePort service for load balancing frontend application.

- **Important Note:** In Nginx reverse proxy, ensure backend service name `backend-service` is updated when you are building the frontend container.

### Build and push frontend docker image
```bash
$ cd aws-eks/03-kubernetes-fundamentals/resources/services/
$ docker build -t ssamantr/kube-frontend-nginx:1.0.0 .
$ docker images
$ docker push ssamantr/kube-frontend-nginx:1.0.0
```

```bash
# Create Deployment for Frontend Nginx Proxy
$ kubectl create deployment frontend-nginx-app --image=ssamantr/kube-frontend-nginx:1.0.0
$ kubectl get deploy
$ kubectl port-forward deployment.apps/frontend-nginx-app 9090:80
$ curl http://localhost:9090/hello

# Create ClusterIp Service for Frontend Nginx Proxy
$ kubectl expose deployment frontend-nginx-app  --type=NodePort --port=80 --target-port=80 --name=frontend-service
$ kubectl get svc

# Capture IP and Port to Access Application
$ kubectl get svc
$ kubectl get nodes -o wide
$ curl http://<node1-public-ip>:<Node-Port>/hello

# Scale backend with 10 replicas
$ kubectl scale --replicas=10 deployment/backend-rest-app

# Test again to view the backend service Load Balancing
$ curl http://<node1-public-ip>:<Node-Port>/hello
```