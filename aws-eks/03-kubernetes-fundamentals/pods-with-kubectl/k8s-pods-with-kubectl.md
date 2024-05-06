# Kubernetes  - PODs

> Prerequisite: Before creating a k8s pod, we assume k8s cluster is created.

### Get Worker Nodes Status
- Verify if kubernetes worker nodes are ready.

```bash
# Get Worker Node Status
$ kubectl get nodes

# Get Worker Node Status with wide option
$ kubectl get nodes -o wide
```

### Create the docker image from Dockerfile from which we will create a pod
```bash
$ cd aws-eks/03-kubernetes-fundamentals/resources/kubenginx/v1-Release/
$ docker build -t ssamantr/nginx-web-server-v1:1.0.0 .
$ docker images
$ docker push ssamantr/nginx-web-server-v1:1.0.0
```

### Create a Pod
- Create a Pod from a docker image

```bash
# Template for creating pod
$ kubectl run <desired-pod-name> --image <docker-container-image-name>
# Create a pod from ssamantr/nginx-web-server-v1:1.0.0
$ kubectl run nginx-pod-v1 --image ssamantr/nginx-web-server-v1:1.0.0
```  

### List Pods
- Get the list of pods
```bash
# List Pods
$ kubectl get pods

# Alias name for pods is po
$ kubectl get po
```

### List Pods with wide option
- List pods with wide option which also provide Node information on which Pod is running
```bash
$ kubectl get pods -o wide
```

### What happened in the backgroup when above command is run?
> 1. Kubernetes created a pod
> 2. Pulled the docker image from docker hub
> 3. Created the container in the pod
> 4. Started the container present in the pod


### Describe Pod
- Describe the POD, primarily required during troubleshooting.
- Events shown will be of a great help during troubleshooting.

```bash
# To get list of pod names
$ kubectl get pods

# Describe the Pod
$ kubectl describe pod <Pod-Name>
$ kubectl describe pod nginx-pod-v1
```

### Access the Application
- Currently we can access this application only inside worker nodes.
- To access it externally, we need to create a **NodePort Service**.
- **Services** is one very important concept in Kubernetes.

### Delete Pod
```bash
# To get list of pod names
$ kubectl get pods

# Delete Pod
$ kubectl delete pod <Pod-Name>
$ kubectl delete pod nginx-pod-v1
```

## Expose Pod with a Service
- Expose pod with a service (NodePort Service) to access the application externally **(from internet)**
- **Ports**
    - **port:** Port on which node port service listens in Kubernetes cluster internally.
    - **targetPort:** We define container port here on which our application is running.
    - **NodePort:** Worker Node port on which we can access our application externally.

```bash
# Template for creating pod
$ kubectl run <desired-pod-name> --image <docker-container-image-name>
# Create a pod from ssamantr/nginx-web-server-v1:1.0.0
$ kubectl run nginx-pod-v1 --image ssamantr/nginx-web-server-v1:1.0.0

$ kubectl get pods

# Expose Pod as a Service
$ kubectl expose pod <pod-name> --type=NodePort --port=80 --name=<desired-service-Name>
$ kubectl expose pod nginx-pod-v1 --type=NodePort --port=80 --name=nginx-node-port-service

# Get Service Info
$ kubectl get service
$ kubectl get svc
```

```bash
# Get Public IP of Worker Nodes which is nothing but the external ip.
$ kubectl get nodes -o wide
```

> Note: With local k8s cluster created using kind, it is not possible to demo k8s service of type NodePort.

#### Access the Application using Public IP and NodePort
```bash
$ curl http://<any-worker-node-public-ip>:<Node-Port>
```

**Important Note About: target-port**
- If **target-port** is not defined, by default and for convenience, the **targetPort** is set to the same value as the **port** field.

```bash
# Below command will fail when accessing the application, as service port (81) and container port (80) are different
$ kubectl expose pod nginx-pod-v1 --type=NodePort --port=81 --name=nginx-node-port-service2     

# Expose Pod as a Service with Container Port (--target-port)
$ kubectl expose pod nginx-pod-v1 --type=NodePort --port=81 --target-port=80 --name=nginx-node-port-service3

# Get Service Info
$ kubectl get service
$ kubectl get svc

# Get Public IP of Worker Nodes
$ kubectl get nodes -o wide
```
- **Access the Application using Public IP**
```bash
$ curl http://<any-worker-node-public-ip>:<Node-Port>
or
$ kubectl port-forward pod/nginx-pod-v1 8080:80
$ curl http://localhost:8080/
or
$ kubectl port-forward service/nginx-node-port-service 8080:80
$ curl http://localhost:8080/
```

## Interact with a Pod

### Verify Pod Logs
```bash
# Get Pod Details
$ kubectl get po

# Dump Pod logs
$ kubectl logs <pod-name>
$ kubectl logs nginx-pod-v1

# Stream pod logs with -f option and access application to see logs
$ kubectl logs <pod-name>
$ kubectl logs -f nginx-pod-v1
```
#### **Important Notes**
  - Refer below link and search for **Interacting with running Pods** for additional log options
  - Troubleshooting skills are very important. So please go through all logging options available and master them.
[**Reference Link**](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)


### Connect to Container in a POD
**Connect to a Container in POD and execute commands**

```bash
# Connect to nginx container in a pod
$ kubectl exec -it <pod-name> -- /bin/bash
$ kubectl exec -it nginx-pod-v1 -- /bin/bash

# Execute some commands in Nginx container
$ ls
$ cd /usr/share/nginx/html
$ cat index.html
$ exit
```

**Running individual commands in a Container**

```bash
$ kubectl exec -it <pod-name> <command>

# Sample Commands
$ kubectl exec -it nginx-pod-v1 env
$ kubectl exec -it nginx-pod-v1 ls
$ kubectl exec -it nginx-pod-v1 cat /usr/share/nginx/html/index.html
```

## Get YAML Output of Pod & Service
### Get YAML Output
```bash
# Get pod definition YAML output
$ kubectl get pod nginx-pod-v1 -o yaml   

# Get service definition YAML output
$ kubectl get service nginx-node-port-service -o yaml   
```

## Clean Up
```bash
# Get all Objects in default namespace
$ kubectl get all

# Delete Pod
$ kubectl delete pod nginx-pod-v1

# Delete Services
$ kubectl delete svc nginx-node-port-service

# Get all Objects in default namespace
$ kubectl get all
```

