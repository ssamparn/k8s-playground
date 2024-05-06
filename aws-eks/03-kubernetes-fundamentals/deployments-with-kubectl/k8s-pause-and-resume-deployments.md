# Pause & Resume Deployments

- Why do we need Pausing & Resuming Deployments?
    - If we want to make multiple changes to our Deployment, we can pause the deployment make all changes and resume it.
- We are going to update our Application Version from **v3 to v4** as part of learning "Pause and Resume Deployments"

## Pausing & Resuming Deployments
### Check current State of Deployment & Application
```bash
# Check the Rollout History of a Deployment
$ kubectl rollout history deployment/nginx-web-server-deployment
Observation: Make a note of last version number

# Get list of ReplicaSets
$ kubectl get rs
Observation: Make a note of number of replicaSets present.

$ kubectl port-forward deployment.apps/nginx-web-server-deployment 8080:80
$ curl http://localhost:8080/

# Access the Application 
$ curl http://<worker-node-ip>:<Node-Port>
Observation: Make a note of application version
```

### Pause Deployment and Two Changes
```bash
# Pause the Deployment
$ kubectl rollout pause deployment/<Deployment-Name>
$ kubectl rollout pause deployment/nginx-web-server-deployment

# Update Deployment - Application Version from v3 to v4
$ kubectl set image deployment/nginx-web-server-deployment nginx-web-server-v1=ssamantr/nginx-web-server-v1:1.0.0

# Check the Rollout History of a Deployment
$ kubectl rollout history deployment/nginx-web-server-deployment
Observation: No new rollout should start, we should see same number of versions as we check earlier with last version number matches which we have noted earlier.

# Get list of ReplicaSets
$ kubectl get rs
Observation: No new replicaSet created. We should have same number of replicaSets as earlier when we took note. 

# Make one more change: set limits to our container
$ kubectl set resources deployment/nginx-web-server-deployment -c=nginx-web-server-v1 --limits=cpu=20m,memory=30Mi
```

### Resume Deployment
```bash
# Resume the Deployment
$ kubectl rollout resume deployment/nginx-web-server-deployment

# Check the Rollout History of a Deployment
$ kubectl rollout history deployment/nginx-web-server-deployment
Observation: You should see a new version got created

# Get list of ReplicaSets
$ kubectl get rs
Observation: You should see new ReplicaSet.
```

### Access Application
```bash
# Access the Application 
$ curl http://<node1-public-ip>:<Node-Port>
Observation: You should see Application V4 version
```

## Clean-Up
```bash
# Delete Deployment
$ kubectl delete deployment nginx-web-server-deployment

# Delete Service
$ kubectl delete svc nginx-web-server-service

# Get all Objects from Kubernetes default namespace
$ kubectl get all
```