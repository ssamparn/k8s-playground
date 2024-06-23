# Deploy User Management Micro Service with MySQL Database

## Introduction
- We are going to deploy a **User Management Microservice** which will connect to MySQL Database schema **user-management** during startup.
- Then we can test the following APIs
    - Create Users
    - List Users
    - Get User by Id
    - Update User
    - Delete User
    - Health Status

| Kubernetes Object                 | YAML File                          |
|-----------------------------------|------------------------------------|
| Deployment, Environment Variables | 06-user-management-deployment.yaml |
| User Management NodePort Service  | 07-user-management-service.yaml    |

## Create following Kubernetes manifests

### Create User Management Microservice Deployment manifest
- **Environment Variables**

| Key Name                   | Value                                   |
|----------------------------|-----------------------------------------|
| spring.datasource.url      | jdbc:mysql://mysql:3306/user-management |
| spring.datasource.username | user                                    |
| spring.datasource.password | password                                |

### Create User Management Microservice NodePort Service manifest
- NodePort Service

## Create UserManagement Service Deployment & Service
```bash

# Create Deployment & NodePort Service
$ kubectl apply -f aws-eks/04-eks-storage-with-elastic-block-store/03-user-management-microservice-with-mysql/kube-manifests/06-user-management-deployment.yaml
$ kubectl apply -f aws-eks/04-eks-storage-with-elastic-block-store/03-user-management-microservice-with-mysql/kube-manifests/07-user-management-service.yaml

# List Pods
$ kubectl get pods

# Verify logs of user-management microservice pod
$ kubectl logs -f <pod-name>

# Verify sc, pvc, pv
$ kubectl get sc,pvc,pv
```
- **Problem Observation:**
    - If we deploy all manifests at a time, by the time mysql is ready our `User Management Microservice` pod will be restarting multiple times due to unavailability of Database.
    - To avoid such situations, we can apply `initContainers` concept to our User management Microservice `Deployment manifest`.
    - We will see that in our next section but for now lets continue to test the application

- **Access Application**
```bash
# List Services
$ kubectl get svc

# Get Public IP
$ kubectl get nodes -o wide

# Access Health Status API for User Management Service
$ http://<EKS-WorkerNode-Public-IP>:31231/actuator/health
```

## Test User Management Microservice using Postman

### Test User Management Services
- **Health Status API**
    - URL: `http://<EKS-WorkerNode-Public-IP>:31231/actuator/health`
- **Create User Service**
    - URL: `http://<EKS-WorkerNode-Public-IP>:31231/user/create`
```json
    {
      "userName": "Sashank",
      "userSalary": 57.40
    }
```

- **List User Service**
    - URL: `http://<EKS-WorkerNode-Public-IP>:31231/user/get-all`

- **Update User Service**
    - URL: `http://<EKS-WorkerNode-Public-IP>:31231/user/update/{userId}`
```json
    {
      "userName": "Sashank",
      "userSalary": 57.40
    }
```  
- **Delete User Service**
    - URL: `http://<EKS-WorkerNode-Public-IP>:31231/user/delete/{userId}`

## Verify Users in MySQL Database

```bash
# Connect to MYSQL Database
$ kubectl run -it --rm --image=mysql:5.6 --restart=Never mysql-client -- mysql -h mysql -u user -ppassword

# Verify user-management schema got created which we provided in ConfigMap
mysql> show schemas;
mysql> use user-management;
mysql> show tables;
mysql> select * from user;
mysql> exit
```

### Create and delete all the resources in one go
```bash
# Create All
$ kubectl create -f aws-eks/04-eks-storage-with-elastic-block-store/03-user-management-microservice-with-mysql/kube-manifests/.

# Delete All
$ kubectl delete -f aws-eks/04-eks-storage-with-elastic-block-store/03-user-management-microservice-with-mysql/kube-manifests/.
```