# Use Relational Database Service (RDS) for workloads running on AWS EKS Cluster

## Create RDS Database

### Review VPC of our EKS Cluster
- Go to **Services** -> **VPC**
- **VPC Name:**  eksctl-k8s-cluster-cluster/VPC

### Pre-requisite-1: Create DB Security Group
- **Create security group** to allow access for RDS Database on port 3306
- **Security group name:** eks_rds_db_sg
- **Description:** Allow access for RDS Database on Port **3306**
- **VPC:** eksctl-k8s-cluster-cluster/VPC
- **Inbound Rules**
    - Type: MySQL/Aurora
    - Protocol: TPC
    - Port: 3306
    - Source: Anywhere (0.0.0.0/0) and (::/0)
    - Description: Allow access for RDS Database on Port 3306
- **Outbound Rules**
    - Leave to defaults

### Pre-requisite-2: Create DB Subnet Group in RDS
- Go to RDS -> Subnet Groups
- Click on **Create DB Subnet Group**
    - **Name:** eks-rds-db-subnetgroup
    - **Description:** EKS RDS DB Subnet Group
    - **VPC:** eksctl-eksdemo1-cluster/VPC
    - **Availability Zones:** us-east-1a, us-east-1b
    - **Subnets:** 2 subnets in 2 AZs
    - Click on **Create**

### Create RDS Database
- Go to  **Services -> RDS**
- Click on **Create Database**
    - **Choose a Database Creation Method:** Standard Create
    - **Engine Options:** MySQL
    - **Edition**: MySQL Community
    - **Version**: 5.7.22 (default populated)
    - **Template Size:** Free Tier
    - **DB instance identifier:** user-management
    - **Master Username:** user
    - **Master Password:** password
    - **Confirm Password:** password
    - **DB Instance Size:** leave to defaults
    - **Storage:** leave to defaults
    - **Connectivity**
        - **VPC:** eksctl-k8s-cluster-cluster/VPC
        - **Additional Connectivity Configuration**
            - **Subnet Group:** eks-rds-db-subnetgroup
            - **Publicyly accessible:** YES (for our learning and troubleshooting - if required)
        - **VPC Security Group:** Create New
            - **Name:** eks_rds_db_sg
        - **Availability Zone:** No Preference
        - **Database Port:** 3306
    - Rest all leave to defaults
- Click on Create Database

### Edit Database Security to Allow Access from 0.0.0.0/0
> Note: Verify Only as it is already done
- Go to **EC2 -> Security Groups -> eks-rds-db-sg**
- **Edit Inboud Rules**
    - **Source:** Anywhere (0.0.0.0/0)  (Allow access from everywhere for now)


## Create Kubernetes externalName service Manifest and Deploy
- Create mysql externalName Service
- **01-mysql-externalname-service.yaml**
```yml
apiVersion: v1
kind: Service
metadata:
  name: mysql
spec:
  type: ExternalName
  externalName: usermgmtdb.c7hldelt9xfp.us-east-1.rds.amazonaws.com
```
- **Deploy Manifest**
```bash
$ kubectl apply -f aws-eks/06-eks-storage-with-rds/kube-manifests/01-mysql-externalname-service.yaml
$ kubectl get svc
```
## Connect to RDS Database using kubectl and create usermgmt schema/db
```bash
$ kubectl run -it --rm --image=mysql:latest --restart=Never mysql-client -- mysql -h user-management.c5sgo8o4qt95.us-east-1.rds.amazonaws.com -u user -ppassword

mysql> show schemas;
mysql> create database `user-management`;
mysql> show tables;
mysql> exit
```

## Deploy User Management Microservice and Test
```bash
# deploy all manifests
$ kubectl apply -f aws-eks/06-eks-storage-with-rds/kube-manifests/.

$ kubectl run -it --rm --image=mysql:latest --restart=Never mysql-client -- mysql -h user-management.c5sgo8o4qt95.us-east-1.rds.amazonaws.com -u user -ppassword

mysql> show schemas;
mysql> use user-management;
mysql> show tables;
mysql> exit

# list pods
$ kubectl get pods

# stream pod logs to verify DB Connection is successful from SpringBoot Application
$ kubectl logs -f <pod-name>
```
## Access Application
```bash
# capture worker node external IP or public IP
$ kubectl get nodes -o wide

# access application
$ curl http://<Worker-Node-Public-Ip>:31231/actuator/health
```

## Clean Up
```bash
# delete all objects created
$ kubectl delete -f aws-eks/06-eks-storage-with-rds/kube-manifests/.

# verify current kubernetes objects
$ kubectl get all
```
