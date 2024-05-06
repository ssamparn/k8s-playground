# Kubernetes - ReplicaSets

### Create ReplicaSet
> Note: We don't have a particular imperative command to create a ReplicaSet. So we will create one with a declarative approach.
```bash
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/resources/replica-sets/replicaset-demo.yaml
```

### List ReplicaSets
```bash
# Get list of ReplicaSets
$ kubectl get replicaset
$ kubectl get rs
```

### Describe ReplicaSet
```bash
# Describe the newly created ReplicaSet
$ kubectl describe rs/<replicaset-name>

$ kubectl describe rs/hello-rs
[or]
$ kubectl describe rs hello-rs
```

### List of Pods
```bash
# Get list of Pods
$ kubectl get pods
$ kubectl describe pod <pod-name>

# Get list of Pods with Pod IP and Node in which it is running
$ kubectl get pods -o wide
```

### Verify the Owner of the Pod
- Verify the owner reference of the pod.
- Verify under **"name"** tag under **"ownerReferences"**, we will find the replicaset name to which this pod belongs to.

```bash
$ kubectl get pods <pod-name> -o yaml
$ kubectl get pods hello-rs-489fk -o yaml 
```

## Expose ReplicaSet as a Service
- Expose ReplicaSet with a service (NodePort Service) to access the application externally (from internet)

```bash
# Expose replicaset as a service
$ kubectl expose rs <ReplicaSet-Name> --type=NodePort --port=80 --target-port=8080 --name=<Service-Name-To-Be-Created>
$ kubectl expose rs hello-rs  --type=NodePort --port=80 --target-port=8080 --name=hello-rs-service

# Get Service info
$ kubectl get service
$ kubectl get svc

# Get Public IP of Worker Nodes
$ kubectl get nodes -o wide
```

> Note: With local k8s cluster created using kind, it is not possible to demo k8s service of type NodePort.

## Test Replicaset Reliability or High Availability
- Test how the high availability or reliability concept is achieved automatically in Kubernetes
- Whenever a POD is accidentally terminated due to some application issue, ReplicaSet should auto-create that Pod to maintain desired number of Replicas configured to achieve High Availability.

```bash
# To get Pod Name
$ kubectl get pods

# Delete the Pod
$ kubectl delete pod <Pod-Name>

# Verify the new pod got created automatically
$ kubectl get pods (verify Age and name of new pod)
```

## Test ReplicaSet Scalability feature
- Test how scalability is going to seamless & quick
- Update the **replicas** field in **replicaset-demo.yml** from 3 to 6.

```bash
# Before change
spec:
  replicas: 5

# After change
spec:
  replicas: 8
```

- Update the ReplicaSet in `replicaset-demo.yaml` file

```bash
# Apply latest changes to ReplicaSet
$ kubectl replace -f aws-eks/03-kubernetes-fundamentals/resources/replica-sets/replicaset-demo.yaml

# Verify if new pods got created
$ kubectl get pods -o wide
```

## Delete ReplicaSet & Service
### Delete ReplicaSet

```bash
# Delete ReplicaSet
$ kubectl delete rs <replicaset-name>

# Sample Commands
$ kubectl delete rs/my-helloworld-rs
[or]
$ kubectl delete rs my-helloworld-rs

# Verify if ReplicaSet got deleted
$ kubectl get rs
```

### Delete Service created for ReplicaSet

```bash
# Delete Service
$ kubectl delete svc <service-name>

# Sample Commands
$ kubectl delete svc hello-rs-service
[or]
$ kubectl delete svc/hello-rs-service

# Verify if Service got deleted
$ kubectl get svc
```