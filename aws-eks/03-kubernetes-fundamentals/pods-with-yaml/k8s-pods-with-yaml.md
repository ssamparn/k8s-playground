# PODs with YAML

## Kubernetes YAML Top level Objects

- Discuss the k8s YAML top level objects

#### **kube-base-definition.yml**
```yml
apiVersion:
kind:
metadata:
  
spec:
```

[**Pod API Objects Reference:**](https://kubernetes.io/docs/reference/kubernetes-api/workload-resources/pod-v1/)

## Create Simple Pod Definition using YAML
- We are going to create a very basic pod definition

#### pod-definition.yml

#### Create Pod
```bash
# Create Pod
$ kubectl create -f aws-eks/03-kubernetes-fundamentals/pods-with-yaml/kube-manifests/pod-definition.yaml
[or]
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/pods-with-yaml/kube-manifests/pod-definition.yaml

# List Pods
$ kubectl get pods
```


## Create a NodePort Service
#### pod-nodeport-service.yml

#### Create NodePort Service for Pod

```bash
# Create Service
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/pods-with-yaml/kube-manifests/pod-nodeport-service.yaml

# List Service
$ kubectl get svc

# Get Public IP
$ kubectl get nodes -o wide

# Access Application
$ http://<WorkerNode-Public-IP>:<NodePort>
$ http://<WorkerNode-Public-IP>:31231
or
$ kubectl port-forward pod/backend-app-pod 8080:8080
$ kubectl port-forward service/backend-app-nodeport-service 8080:8080
$ curl http://localhost:8080/hello

# Delete Pod
$ kubectl delete -f aws-eks/03-kubernetes-fundamentals/pods-with-yaml/kube-manifests/pod-definition.yaml

# Delete Service
$ kubectl delete -f aws-eks/03-kubernetes-fundamentals/pods-with-yaml/kube-manifests/pod-nodeport-service.yaml
```