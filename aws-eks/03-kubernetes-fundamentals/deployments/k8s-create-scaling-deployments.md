# Kubernetes - Deployment

## Create Deployment
- Create Deployment to roll out a ReplicaSet
- Verify Deployment, ReplicaSet & Pods

### Create the docker image from Dockerfile from which we will create a pod
```bash
$ cd aws-eks/03-kubernetes-fundamentals/resources/kubenginx/v2-Release/
$ docker build -t ssamantr/nginx-web-server-v2:1.0.0 .
$ docker images
$ docker push ssamantr/nginx-web-server-v2:1.0.0
```
[**Docker Image Location**](https://hub.docker.com/repository/docker/ssamantr/nginx-web-server-v2/general)

```bash
# Create Deployment
$ kubectl create deployment <deplyment-name> --image=<Container-Image>
$ kubectl create deployment nginx-web-server-deployment --image=ssamantr/nginx-web-server-v2:1.0.0

# Verify Deployment
$ kubectl get deployments
$ kubectl get deploy 

# Describe Deployment
$ kubectl describe deployment <deployment-name>
$ kubectl describe deployment nginx-web-server-deployment

# Verify ReplicaSet
$ kubectl get rs

# Verify Pod
$ kubectl get pods
```

## Scaling a Deployment
- Scale the deployment to increase the number of replicas (pods)

```bash
# Scale Up the Deployment
$ kubectl scale --replicas=<desired-number-of-replicas> deployment/<Deployment-Name>
$ kubectl scale --replicas=5 deployment nginx-web-server-deployment
$ kubectl scale --replicas=5 deployment/nginx-web-server-deployment

# Verify Deployment
$ kubectl get deploy

# Verify ReplicaSet
$ kubectl get rs

# Verify Pods
$ kubectl get pods

# Scale Down the Deployment
$ kubectl scale --replicas=3 deployment nginx-web-server-deployment
$ kubectl get deploy
```

## Expose Deployment as a Service
- Expose **Deployment** with a service (NodePort Service) to access the application externally (from internet)

```bash
# Expose Deployment as a Service
$ kubectl expose deployment <Deployment-Name> --type=NodePort --port=80 --target-port=80 --name=<Service-Name-To-Be-Created>
$ kubectl expose deployment nginx-web-server-deployment --type=NodePort --port=80 --target-port=80 --name=nginx-web-server-service

# Get Service Info
$ kubectl get svc
Observation: Make a note of port which starts with 3 (Example: 80:3xxxx/TCP). Capture the port 3xxxx and use it in application URL below. 

# Get Public IP of Worker Nodes
$ kubectl get nodes -o wide
Observation: Make a note of "EXTERNAL-IP" if your Kubernetes cluster is setup on AWS EKS.
```

#### Access the Application using Public IP
```bash
$ curl http://<worker-node-public-ip>:<Node-Port>
```