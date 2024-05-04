## Kubernetes Cluster: Commands Cheatsheet

> Prerequisite: Before creating a k8s cluster, we assume kubectl and kind is installed.
> Note:  Before creating a multi-node cluster make sure you don't have any running containers. 
> Delete all the containers and system volumes.
> Delete the .kube directory (if any) in your home directory

```bash
$ docker ps -a
$ docker system prune -af
$ docker network ls
$ Do a rm -rf ~/.kube
```

#### Watch a Kubernetes Cluster:
```bash
$ watch -t -x kubectl get all
```

#### Create a multi-node cluster using kind
```bash
$ kind create cluster --config aws-eks/03-kubernetes-fundamentals/resources/kind-cluster/create-cluster.yaml

$ docker ps
$ docker network ls
```

#### Create a multi-node cluster using kind with port-mapping
```bash
$ kind create cluster --config aws-eks/03-kubernetes-fundamentals/resources/kind-cluster/create-cluster-with-port-mapping.yaml
$ docker ps
$ kubectl get node
```

#### Create a multi-node nginx ingress cluster using kind with port-mapping
```bash
$ kind create cluster --config aws-eks/03-kubernetes-fundamentals/resources/kind-cluster/create-nginx-ingress-cluster.yaml
$ docker ps
$ kubectl get node
```

#### Install Istio in a single node kind cluster
```bash
$ brew install istioctl
$ brew install helm
$ sh setup.sh
$ kubectl get pods --namespace=istio-system
```

> References: 
- https://www.danielstechblog.io/local-kubernetes-setup-with-kind/
- https://www.danielstechblog.io/running-istio-on-kind-kubernetes-in-docker/
- https://istio.io/latest/docs/ops/diagnostic-tools/istioctl/
- https://medium.com/@s4l1h/how-to-install-kind-and-istio-ingress-controller-3b510834c762
- https://github.com/neumanndaniel/kubernetes/blob/master/kind/setup.sh

#### Check the configuration of the K8S cluster created by kind
```bash
$ cat ~/.kube/config
$ kubectl version --output=json
```
> If we got good response from above 2 commands, that means kubectl is able to talk to k8s cluster created with kind.

#### Get all the nodes in a cluster
```bash
$ kubectl get nodes
```

### Kube Config
> When we issue kubectl command, it looks for kube config file located in `$HOME/.kube/config` directory. 
> It is a simple configuration file to organize cluster information.

### Explore k8s (Kind) Cluster:
```bash
$ docker ps
$ docker exec -it local-cluster-control-plane bash
$ cd /etc/kubernetes/manifests/
$ ls -l
```
> You will see all the configurations of all the components i.e: 
> `etcd`, `kube-apiserver`, `kube-controller-manager`, `kube-scheduler` of a kubernetes master node.
> But these are all config files. Where is the process running?

#### Explore the running components of k8s process:
```bash
$ ps -aux
```

#### Delete the k8s (kind) cluster:
```bash
$ kind get clusters
$ kind delete cluster --name=<cluster-name>
$ kind delete cluster --name=local-cluster
$ docker ps -a
```