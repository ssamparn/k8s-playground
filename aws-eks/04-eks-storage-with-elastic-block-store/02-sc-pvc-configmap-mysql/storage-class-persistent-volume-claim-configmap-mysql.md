# EKS Storage -  Storage Classes, Persistent Volume Claims

## Introduction
- Create a MySQL Database with persistence storage using AWS EBS Volumes

| Kubernetes Object                                         | YAML File                          |
|-----------------------------------------------------------|------------------------------------|
| Storage Class                                             | 01-storage-class.yaml              |
| Persistent Volume Claim                                   | 02-persistent-volume-claim.yaml    |
| Config Map                                                | 03-user-management-config-map.yaml |
| Deployment, Environment Variables, Volumes, VolumeMounts, | 04-mysql-deployment.yml            |
| ClusterIP Service                                         | 05-mysql-clusterip-service.yaml    |


## Create following Kubernetes manifests

### Create Storage Class manifest
- **Important Note:** `WaitForFirstConsumer` mode will delay the volume binding and provisioning of PersistentVolume until a Pod using the PersistentVolumeClaim is created.

### Create Persistent Volume Claims manifests
```bash
# Create Storage Class & PVC
$ kubectl apply -f aws-eks/04-eks-storage-with-elastic-block-store/02-sc-pvc-configmap-mysql/kube-manifests/01-storage-class.yaml
$ kubectl apply -f aws-eks/04-eks-storage-with-elastic-block-store/02-sc-pvc-configmap-mysql/kube-manifests/02-persistent-volume-claim.yaml
$ kubectl apply -f aws-eks/04-eks-storage-with-elastic-block-store/02-sc-pvc-configmap-mysql/kube-manifests/03-user-management-configmap.yaml

# List Storage Classes
$ kubectl get sc

# List PVC
$ kubectl get pvc 

# List PV
$ kubectl get pv
```

### Create ConfigMap manifest
- We are going to create a **usermanagement** database schema during the mysql pod creation time which we will leverage when we deploy User Management Microservice.

### Create MySQL Deployment and ClusterIP Service manifest
- Environment Variables
- Volumes
- Volume Mounts

### Create MySQL ClusterIP Service manifest
- At any point of time we are going to have only one mysql pod in this design.
- `ClusterIP: None` will use the `Pod IP Address` instead of creating or allocating a separate IP for `MySQL Cluster IP service`


## Create MySQL Database with all above manifests
```bash
# Create MySQL Database deployment
$ kubectl apply -f aws-eks/04-eks-storage-with-elastic-block-store/02-sc-pvc-configmap-mysql/kube-manifests/04-mysql-deployment.yaml

# Create MySQL Database clusterip service
$ kubectl apply -f aws-eks/04-eks-storage-with-elastic-block-store/02-sc-pvc-configmap-mysql/kube-manifests/05-mysql-clusterip-service.yaml

# List Storage Classes
$ kubectl get sc

# List PVC
$ kubectl get pvc

# List PV
$ kubectl get pv

# List pods
$ kubectl get pods 

# List pods based on label name
$ kubectl get pods -l app=mysql
```

## Connect to MySQL Database
```bash
# Connect to MYSQL Database
$ kubectl run -it --rm --image=mysql:5.6 --restart=Never mysql-client -- mysql -h mysql -pmysql-password

# Verify usermanagement schema got created which we provided in ConfigMap
mysql> show schemas;
```

### Create and delete all the resources in one go
```bash
$ kubectl create -f aws-eks/04-eks-storage-with-elastic-block-store/02-sc-pvc-configmap-mysql/kube-manifests/.
$ kubectl delete -f aws-eks/04-eks-storage-with-elastic-block-store/02-sc-pvc-configmap-mysql/kube-manifests/.
```

## References
- We need to discuss references exclusively here.
- These will help you in writing effective templates based on need in your environments.
- Few features are still in alpha stage as on today (Example:Resizing), but once they reach beta you can start leveraging those templates and make your trials.
- **EBS CSI Driver:** https://github.com/kubernetes-sigs/aws-ebs-csi-driver
- **EBS CSI Driver Dynamic Provisioning:**  https://github.com/kubernetes-sigs/aws-ebs-csi-driver/tree/master/examples/kubernetes/dynamic-provisioning
- **EBS CSI Driver - Other Examples like Resizing, Snapshot etc:** https://github.com/kubernetes-sigs/aws-ebs-csi-driver/tree/master/examples/kubernetes