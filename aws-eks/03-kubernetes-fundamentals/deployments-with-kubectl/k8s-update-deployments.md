# Kubernetes - Update Deployments

### Update Deployments Options
- We can update deployments using two options
    - Set Image
    - Edit Deployment

> Updating Application version v1 to v2 using "Set Image" Option

### Create the docker image from Dockerfile from which we will create a pod
```bash
$ cd aws-eks/03-kubernetes-fundamentals/resources/kubenginx/v1-Release/
$ docker build -t ssamantr/nginx-web-server-v1:1.0.0 .
$ cd aws-eks/03-kubernetes-fundamentals/resources/kubenginx/v2-Release/
$ docker build -t ssamantr/nginx-web-server-v2:1.0.0 .
$ docker images
$ docker push ssamantr/nginx-web-server-v1:1.0.0
$ docker push ssamantr/nginx-web-server-v2:1.0.0
```

### Update Deployment

```bash
# Create Deployment
$ kubectl create deployment <deplyment-name> --image=<Container-Image>
$ kubectl create deployment nginx-web-server-deployment --image=ssamantr/nginx-web-server-v1:1.0.0
$ kubectl port-forward deployment.apps/nginx-web-server-deployment 8080:80
$ curl http://localhost:8080/
```

```bash
# Get Container Name from current deployment
$ kubectl get deployment nginx-web-server-deployment -o yaml
```
- **Observation:** Please Check the container name in `spec.containers.name` yaml output and make a note of it and
  replace in `kubectl set image` command <Container-Name>

```bash
# Update Deployment - SHOULD WORK NOW
$ kubectl set image deployment/<deployment-name> <container-name>=<container-image> --record=true
$ kubectl set image deployment/nginx-web-server-deployment nginx-web-server-v1=ssamantr/nginx-web-server-v2:1.0.0 --record=true
$ kubectl port-forward deployment.apps/nginx-web-server-deployment 8080:80
$ curl http://localhost:8080/
```

### Verify Rollout Status (Deployment Status)
> By default, rollout happens in a rolling update model, so no downtime.

```bash
# Verify Rollout Status 
$ kubectl rollout status deployment/nginx-web-server-deployment

# Verify Deployment
$ kubectl get deploy
```

### Describe Deployment
- **Observation:**
    - Verify the Events and understand that Kubernetes by default do "Rolling Update" for new application releases.
    - With that said, we will not have downtime for our application.

```bash
# Descibe Deployment
$ kubectl describe deployment nginx-web-server-deployment
```

### Verify ReplicaSet
- **Observation:** New ReplicaSet will be created for new version

```bash
# Verify ReplicaSet
$ kubectl get rs
```

### Verify Pods
- **Observation:** Pod template hash label of new replicaset should be present for PODs letting us
  know these pods belong to new ReplicaSet.

```bash
# List Pods
$ kubectl get pods
```

### Verify Rollout History of a Deployment
- **Observation:** We have the rollout history, so we can switch back to older revisions using
  revision history available to us.

```bash
# Check the Rollout History of a Deployment
$ kubectl rollout history deployment/<Deployment-Name>
$ kubectl rollout history deployment/nginx-web-server-deployment
```

### Access the Application using Public IP
- We should see `Application Version:V2` whenever we access the application in browser

```bash
# Get NodePort
$ kubectl get svc
Observation: Make a note of port which starts with 3 (Example: 80:3xxxx/TCP). Capture the port 3xxxx and use it in application URL below. 

# Get Public IP of Worker Nodes
$ kubectl get nodes -o wide
Observation: Make a note of "EXTERNAL-IP" if your Kubernetes cluster is setup on AWS EKS.

# Application URL
$ curl http://<worker-node-public-ip>:<Node-Port>
```

## Update the Application from V2 to V3 using "Edit Deployment" Option

### Edit Deployment

```bash
# Edit Deployment
$ kubectl edit deployment/<Deployment-Name> --record=true
$ kubectl edit deployment/nginx-web-server-deployment --record=true
```

```bash
# deployment.yml

# Change From v2
    spec:
      containers:
      - image: ssamantr/nginx-web-server-v2:1.0.0

# Change To v3
    spec:
      containers:
      - image: ssamantr/nginx-web-server-v3:1.0.0
```

### Verify Rollout Status
- **Observation:** Rollout happens in a rolling update model, so no downtime.

```bash
# Verify Rollout Status 
$ kubectl rollout status deployment/nginx-web-server-deployment
```

### Verify Replicasets
- **Observation:**  We should see 3 ReplicaSets now, as we have updated our application to v3 version of the application

### Verify ReplicaSet and Pods
```bash
# Verify ReplicaSet and Pods
$ kubectl get rs
$ kubectl get pods
```

### Verify Rollout History
```bash
# Check the Rollout History of a Deployment
$ kubectl rollout history deployment/<Deployment-Name>
$ kubectl rollout history deployment/nginx-web-server-deployment
```

### Access the Application using Public IP
- We should see `Application Version:v3` whenever we access the application in browser

```bash
# Get NodePort
$ kubectl get svc
Observation: Make a note of port which starts with 3 (Example: 80:3xxxx/TCP). Capture the port 3xxxx and use it in application URL below. 

# Get Public IP of Worker Nodes
$ kubectl get nodes -o wide
Observation: Make a note of "EXTERNAL-IP" if your Kubernetes cluster is setup on AWS EKS.

# Application URL
$ http://<worker-node-public-ip>:<Node-Port>
```
