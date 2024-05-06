# Services with YAML

## Introduction to Services
- We are going create below two services in detail with a frontend (NodePort) and backend (ClusterIP) services.
  - NodePort Service
  - ClusterIP Service

## Create Backend Deployment & Cluster IP Service
```bash
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/services-with-yaml/kube-manifests/backend-clusterip-service.yaml
$ kubectl get all
$ kubectl port-forward service/backend-service 8080:8080
$ curl http://localhost:8080/hello
```

## Create Frontend Deployment & NodePort Service
```bash
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/services-with-yaml/kube-manifests/frontend-nodeport-service.yaml
$ kubectl get all
```

#### Access REST Application
```bash
# Get External IP of nodes using
$ kubectl get nodes -o wide

# Access REST Application  (Port is static 31234 configured in frontend service template)
$ curl http://<node1-public-ip>:31234/hello
or 
$ kubectl port-forward deployment.apps/frontend-nginx-app-deployment 9090:80
$ curl http://localhost:9090/hello
or
$ kubectl port-forward service/frontend-service 8080:80
$ curl http://localhost:8080/hello
```

## Delete with kubectl apply
```bash
$ kubectl delete -f aws-eks/03-kubernetes-fundamentals/services-with-yaml/kube-manifests/backend-clusterip-service.yaml
$ kubectl get all
$ kubectl delete -f aws-eks/03-kubernetes-fundamentals/services-with-yaml/kube-manifests/frontend-nodeport-service.yaml
$ kubectl get all
```


