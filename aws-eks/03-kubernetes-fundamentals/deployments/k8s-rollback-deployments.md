# Rollback Deployment

- We can roll back a deployment in two ways.
    - Previous Version
    - Specific Version

## Rollback a Deployment to previous version

### Check the Rollout History of a Deployment

```bash
# List Deployment Rollout History
$ kubectl rollout history deployment/<Deployment-Name>
$ kubectl rollout history deployment/nginx-web-server-deployment
```

### Verify changes in each revision
- **Observation:** Review the `Annotations` and `Image` tags for clear understanding about changes.

```bash
# List Deployment History with revision information
$ kubectl get deploy -o yaml
$ kubectl rollout history deployment/nginx-web-server-deployment --revision=1
$ kubectl rollout history deployment/nginx-web-server-deployment --revision=2
$ kubectl rollout history deployment/nginx-web-server-deployment --revision=3
```

### Rollback to previous version
- **Observation:** If we undo the rollback, it will go back to revision-2 and the revision number increases to revision-4

```bash
# Undo Deployment
$ kubectl rollout undo deployment/nginx-web-server-deployment

$ kubectl port-forward deployment.apps/nginx-web-server-deployment 8080:80
$ curl http://localhost:8080/

# List Deployment Rollout History
$ kubectl rollout history deployment/nginx-web-server-deployment
```

### Verify Deployment, Pods, ReplicaSets
```bash
$ kubectl get deploy -o yaml
$ kubectl get rs
$ kubectl get pods
$ kubectl describe deploy nginx-web-server-deployment
```

### Access the Application using Public IP
- We should see `Application Version:v2` whenever we access the application in browser

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

## Rollback to specific revision
### Check the Rollout History of a Deployment

```bash
# List Deployment Rollout History
$ kubectl rollout history deployment/<Deployment-Name>
$ kubectl rollout history deployment/nginx-web-server-deployment
```

### Rollback to specific revision
```bash
# Rollback Deployment to Specific Revision
$ kubectl rollout undo deployment/nginx-web-server-deployment --to-revision=3
$ kubectl port-forward deployment.apps/nginx-web-server-deployment 8080:80
$ curl http://localhost:8080/
```

### List Deployment History
- **Observation:** If we rollback to revision 3, it will go back to revision-3 and its number increases to revision-5 in rollout history

```bash
# List Deployment Rollout History
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
$ curl http://<worker-node-public-ip>:<Node-Port>
```

## Rolling Restarts of Application
- Rolling restarts will kill the existing pods and recreate new pods in a rolling fashion.

```bash
# Rolling Restarts
$ kubectl rollout restart deployment/<Deployment-Name>
$ kubectl rollout restart deployment/nginx-web-server-deployment

# Get list of Pods
$ kubectl get pods
```