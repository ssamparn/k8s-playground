# Kubernetes Namespaces - Imperative using kubectl

## Introduction
- Namespaces are also called **virtual clusters** in our physical k8s cluster.
- Namespaces allow to split-up resources into different groups.
- Resource names should be **unique** in a namespace
- We can use namespaces to create multiple environments like dev, staging and production etc. Useful where we have many users spread across multiple teams or projects.
- Kubernetes will always list the resources from **default** namespace unless we provide exclusively from which namespace we need information from.
- We can limit the resources like CPU, memory on per namespace basis which is called **Resource Quota**

## Generic Namespaces - Deploy in `dev` and `qa`
### Create Namespace
```bash
# list all objects from kube-system namespace
$ kubectl get all --namespace kube-system

# list namespaces
$ kubectl get ns 

# create namespaces
$ kubectl create namespace <namespace-name>
$ kubectl create namespace dev
$ kubectl create namespace qa

# list namespaces
$ kubectl get ns 
```

> Note: Comment `NodePort` in User Management NodePort Service in file 07-user-management-service.yaml
- **Why?**
    - Whenever we create with same manifests multiple environments like dev, qa with namespaces, we cannot have same worker node port for multiple services.
    - We will have port conflict.
    - It's good for k8s system to provide dynamic nodeport for us in such situations.
```yml
  #nodePort: 31231
```
- Otherwise we will receive **Error** if not commented
```log
The Service user-management-service" is invalid: spec.ports[0].nodePort: Invalid value: 31231: provided port is already allocated
```

### Deploy All k8s Objects
```bash
# Deploy All k8s Objects
$ kubectl apply -f aws-eks/05-kubernetes-concepts/kube-manifests/ -n dev
$ kubectl apply -f aws-eks/05-kubernetes-concepts/kube-manifests/ -n qa

# List all objects from dev & qa Namespaces
$ kubectl get all -n dev
$ kubectl get all -n qa
```

## Verify SC, PVC and PV
- **Important Note**
    - PVC is a namespace specific resource
    - PV and SC are generic
- **Observation-1:** `Persistent Volume Claim (PVC)` gets created in respective namespaces

```bash
# List PVC for dev and qa
$ kubectl get pvc -n dev
$ kubectl get pvc -n qa
```

- **Observation-2:** `Storage Class (SC) and Persistent Volume (PV)` gets created generic. No specific namespace for them
```bash
# List sc,pv
$ kubect get sc,pv
```

## Access Application
### dev namespace
```bash
# get public ip
$ kubectl get nodes -o wide

# get NodePort for dev user management service
$ kubectl get svc -n dev

# access application
$ curl http://<Worker-Node-Public-Ip>:<dev-NodePort>/actuator/health
```

### qa namespace
```bash
# get public ip
$ kubectl get nodes -o wide

# get NodePort for qa user management service
$ kubectl get svc -n qa

# access application
$ curl http://<Worker-Node-Public-Ip>:<qa-NodePort>/actuator/health
```

## Clean Up
```bash
# delete namespaces dev & qa
$ kubectl delete ns dev
$ kubectl delete ns qa

# list all objects from dev & qa namespaces
$ kubectl get all -n dev
$ kubectl get all -n qa

# list namespaces
$ kubectl get ns

# list sc,pv
$ kubectl get sc,pv

# delete storage class
$ kubectl delete sc ebs-sc

# get all from all namespaces
$ kubectl get all -all-namespaces
```