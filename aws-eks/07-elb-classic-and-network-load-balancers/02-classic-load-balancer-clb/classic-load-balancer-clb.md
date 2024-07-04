# AWS - Classic Load Balancer - CLB

## Create AWS Classic Load Balancer Kubernetes Manifest & Deploy
- **03-user-management-clb-service.yaml**

## (Prerequisite) Connect to RDS Database using kubectl and create `user-management` schema/db
```bash
$ kubectl apply -f aws-eks/07-elb-classic-and-network-load-balancers/02-classic-load-balancer-clb/kube-manifests/01-mysql-externalname-service.yaml
$ kubectl run -it --rm --image=mysql:latest --restart=Never mysql-client -- mysql -h user-management.c5sgo8o4qt95.us-east-1.rds.amazonaws.com -u user -ppassword

mysql> show schemas;
mysql> create database `user-management`;
mysql> use user-management;
mysql> show tables;
mysql> exit
```

## Deploy all Manifest
```bash
# deploy all manifests
$ kubectl apply -f aws-eks/07-elb-classic-and-network-load-balancers/02-classic-load-balancer-clb/kube-manifests/.

# list services (verify newly created CLB service)
$ kubectl get svc

# verify pods
$ kubectl get pods
```

## Verify the deployment
- Verify if new CLB got created
    - Go to **Services** -> **EC2** -> **Load Balancing** -> **Load Balancers**
        - **CLB** should have been created
        - Copy **DNS Name** (Example: a85ae6e4030aa4513bd200f08f1eb9cc-7f13b3acc1bcaaa2.elb.us-east-1.amazonaws.com)
    - Go to **Services** -> **EC2** -> **Load Balancing** -> Target Groups
        - Verify the health status, we should see active.
- **Access Application**
```bash
# access application
curl http://<clb-dns-name>/actuator/health
```    

## Clean Up
```bash
# delete all Oobjects created
$ kubectl delete -f aws-eks/07-elb-classic-and-network-load-balancers/02-classic-load-balancer-clb/kube-manifests/.

# verify current kubernetes objects
$ kubectl get all
```