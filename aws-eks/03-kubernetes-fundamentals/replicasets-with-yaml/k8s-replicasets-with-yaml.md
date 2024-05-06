# ReplicaSets with YAML

## Create ReplicaSet Definition
#### replicaset-definition.yml

## Create ReplicaSet
- Create ReplicaSet with 3 Replicas

```bash
# Create ReplicaSet
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/replicasets-with-yaml/kubemanifests/replicaset-definition.yaml

# List replica sets
$ kubectl get rs
```

#### Delete a pod
- ReplicaSet immediately creates the pod.
```bash
# List Pods
$ kubectl get pods

# Delete Pod
$ kubectl delete pod <Pod-Name>
```

## Create NodePort Service for ReplicaSet
#### replicaset-nodeport-service.yaml

#### Create NodePort Service for ReplicaSet & Test
```bash
# Create NodePort Service
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/replicasets-with-yaml/kubemanifests/replicaset-nodeport-servie.yaml

# List NodePort Service
$ kubectl get svc

# Get Public IP
$ kubectl get nodes -o wide

# Access Application
$ curl http://<Worker-Node-Public-IP>:<NodePort>
$ curl http://<Worker-Node-Public-IP>:31232
or
$ kubectl port-forward service/replicaset-node-port-service 8080:8080
$ curl http://localhost:8080/hello
```