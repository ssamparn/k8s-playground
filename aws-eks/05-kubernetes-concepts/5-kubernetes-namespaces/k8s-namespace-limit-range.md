# Kubernetes Namespaces - LimitRange - Declarative using YAML

## Create Namespace manifest
- **Important Note:** File name starts with `00-` so that when creating k8s objects namespace will get created first, so it doesn't throw an error.
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: dev
```

## Create LimitRange manifest
Instead of specifying resources like `cpu` and `memory` in every container spec of a pod definition, we can provide the default CPU & Memory for all containers in a namespace using `LimitRange`
```yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: default-cpu-mem-limit-range
  namespace: dev
spec:
  limits:
    - default:
        memory: "512Mi" # If not specified the Container's memory limit is set to 512Mi, which is the default memory limit for the namespace.
        cpu: "500m"  # If not specified default limit is 1 vCPU per container 
      defaultRequest:
        memory: "256Mi" # If not specified default it will take from whatever specified in limits.default.memory
        cpu: "300m" # If not specified default it will take from whatever specified in limits.default.cpu
      type: Container                        
```

## Update all k8s manifest with namespace
- Update all files from 02 to 08 with `namespace: dev` in top metadata section

- **Example**
```yml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: ebs-mysql-pv-claim
  namespace: dev
```

## Create k8s objects & Test
```bash
# create all objects
$ kubectl apply -f aws-eks/05-kubernetes-concepts/kube-manifests/

# list pods
$ kubectl get pods -n dev -w

# view pod specification (CPU & Memory)
$ kubectl get pod <pod-name> -o yaml -n dev

# get & describe limits
$ kubectl get limits -n dev
$ kubectl describe limits default-cpu-mem-limit-range -n dev

# get NodePort
$ kubectl get svc -n dev

# get public IP of a worker node
$ kubectl get nodes -o wide

# Access Application Health Status Page
$ curl http://<WorkerNode-Public-IP>:<NodePort>/actuator/health
```

## Clean Up
- Delete all k8s objects created as part of this section
```bash
# delete all
$ kubectl delete -f aws-eks/05-kubernetes-concepts/kube-manifests/
```
