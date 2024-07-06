---
title: AWS Load Balancer Controller Install on AWS EKS
description: Learn to install AWS Load Balancer Controller for Ingress Implementation on AWS EKS
---

## Introduction
1. Create IAM Policy and make a note of Policy ARN
2. Create IAM Role and k8s Service Account and bound them together
3. Install AWS Load Balancer Controller using HELM3 CLI
4. Understand IngressClass Concept and create a default Ingress Class

## Pre-requisites
### Pre-requisite-1: eksctl & kubectl command line utility

- Should be the latest eksctl version
```bash
# verify eksctl version
$ eksctl version

# For installing or upgrading latest eksctl version
https://docs.aws.amazon.com/eks/latest/userguide/eksctl.html

# verify EKS cluster version
$ kubectl version --short
$ kubectl version
Important Note: You must use a kubectl version that is within one minor version difference of your Amazon EKS cluster control plane. For example, a 1.20 kubectl client works with Kubernetes 1.19, 1.20 and 1.21 clusters.

# For installing kubectl cli
https://docs.aws.amazon.com/eks/latest/userguide/install-kubectl.html
```
### Pre-requisite-2: Create EKS Cluster and Worker Nodes (if not created)
```bash
# create k8s cluster
$ eksctl create cluster --name=k8s-cluster \
                      --region=us-east-1 \
                      --zones=us-east-1a,us-east-1b \
                      --without-nodegroup 
                      
# we can include a --version tag while creating the cluster. As a note, always keep the k8s client version upto date with k8s cluster version. 
$ eksctl create cluster --name=k8s-cluster \
                      --region=us-east-1 \
                      --zones=us-east-1a,us-east-1b \
                      --version="1.30" \
                      --without-nodegroup 
                      
# get list of clusters
$ eksctl get cluster

# create EKS node group in VPC private subnets
$ eksctl create nodegroup --cluster=k8s-cluster \
                        --region=us-east-1 \
                        --name=k8s-cluster-ng-private1 \
                        --node-type=t3.medium \
                        --nodes-min=2 \
                        --nodes-max=3 \
                        --node-volume-size=20 \
                        --ssh-access \
                        --ssh-public-key=kube-demo \
                        --managed \
                        --asg-access \
                        --external-dns-access \
                        --full-ecr-access \
                        --appmesh-access \
                        --alb-ingress-access \
                        --node-private-networking
```
### Pre-requisite-3:  Verify Cluster, Node Groups and configure kubectl cli if not configured
1. EKS Cluster
2. EKS Node Groups in Private Subnets

```bash
# verify EKS cluster
$ eksctl get cluster

# Verify EKS Node Groups
$ eksctl get nodegroup --cluster=k8s-cluster

# Verify if any IAM Service Accounts present in EKS Cluster
$ eksctl get iamserviceaccount --cluster=k8s-cluster
Observation:
1. No k8s Service accounts as of now. (No iamserviceaccounts found)

# Optional Task
# Configure kubeconfig for kubectl (if not configured)
$ eksctl get cluster # TO GET CLUSTER NAME
$ aws eks --region <region-code> update-kubeconfig --name <cluster_name>
$ aws eks --region us-east-1 update-kubeconfig --name k8s-cluster

# Verify EKS Nodes in EKS Cluster using kubectl
$ kubectl get nodes

# Verify using EKS and EC2 Services created in AWS Management Console
1. EKS EC2 Nodes (Verify Subnet in Networking Tab)
2. EKS Cluster
```

## Create IAM Policy
- Create IAM policy for the AWS Load Balancer Controller that allows it to make calls to AWS APIs on your behalf.
- As on today `v2.8` is the latest Load Balancer Controller
- We will download always latest from main branch of Git Repo
- [AWS Load Balancer Controller Main Git repo](https://github.com/kubernetes-sigs/aws-load-balancer-controller)
```bash
# change directroy
$ cd aws-eks/08-elb-application-load-balancers/01-install-load-balancer-controller/

# delete files before download (if any present)
$ rm iam_policy_latest.json

# download IAM policy
## download latest
$ curl -o iam_policy_latest.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/main/docs/install/iam_policy.json

## verify latest
$ ls -lrta 

## download specific version
$ curl -o iam_policy_v2.3.1.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.3.1/docs/install/iam_policy.json

# Create IAM Policy using policy downloaded 
$ aws iam create-policy \
    --policy-name AWSLoadBalancerControllerIAMPolicy \
    --policy-document file://iam_policy_latest.json

## Sample Output
```json
{
    "Policy": {
        "PolicyName": "AWSLoadBalancerControllerIAMPolicy",
        "PolicyId": "ANPA4KD6Z3GFECBPUXNQT",
        "Arn": "arn:aws:iam::846374492554:policy/AWSLoadBalancerControllerIAMPolicy",
        "Path": "/",
        "DefaultVersionId": "v1",
        "AttachmentCount": 0,
        "PermissionsBoundaryUsageCount": 0,
        "IsAttachable": true,
        "CreateDate": "2024-07-05T16:20:01+00:00",
        "UpdateDate": "2024-07-05T16:20:01+00:00"
    }
}
```

- **Important Note:** If you view the policy in the AWS Management Console, you may see warnings for ELB. These can be safely ignored because some of the actions only exist for ELB v2. You do not see warnings for ELB v2.

### Make a note of Policy ARN
- Make a note of Policy ARN as we are going to use that in next step when creating IAM Role.
```bash
# Policy ARN 
Policy ARN: arn:aws:iam::846374492554:policy/AWSLoadBalancerControllerIAMPolicy
```

## Create an IAM role for the AWS LoadBalancer Controller and attach the role to the Kubernetes service account
- Applicable only with `eksctl` managed clusters
- This command will create an AWS IAM role
- This command also will create Kubernetes Service Account in k8s cluster
- In addition, this command will bound IAM Role created and the Kubernetes service account created

### Step-03-01: Create IAM Role using eksctl
```bash
# Verify if any existing service account
$ kubectl get sa -n kube-system
$ kubectl get sa aws-load-balancer-controller -n kube-system
Obseravation:
1. Nothing with name "aws-load-balancer-controller" should exist (Error from server (NotFound): serviceaccounts "aws-load-balancer-controller" not found)

# Note: To enable and use AWS IAM roles for Kubernetes service accounts on our EKS cluster, we must create & associate OIDC identity provider.
# Create & Associate IAM OIDC Provider for our EKS Cluster
$ eksctl utils associate-iam-oidc-provider \
    --region us-east-1 \
    --cluster k8s-cluster \
    --approve
    
# Template
$ eksctl create iamserviceaccount \
  --cluster=cluster-name \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \ #Note:  K8S Service Account Name that need to be bound to newly created IAM Role
  --attach-policy-arn=arn:aws:iam::111122223333:policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --approve
    
# Replace name, cluster and policy arn (Policy arn we took note in step-02)
$ eksctl create iamserviceaccount \
  --cluster=k8s-cluster \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --attach-policy-arn=arn:aws:iam::846374492554:policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --approve
```

### Step-03-02: Verify using eksctl cli
```bash
# Get IAM Service Account
$ eksctl get iamserviceaccount --cluster k8s-cluster
```

### Step-03-03: Verify CloudFormation Template eksctl created & IAM Role
- Go to **Services** -> **Cloud Formation**
- **CFN Template Name:** eksctl-k8s-cluster-addon-iamserviceaccount-kube-system-aws-load-balancer-controller
- Click on **Resources** tab
- Click on link in **Physical ID** to open the IAM Role
- Verify it has **eksctl-k8s-cluster-addon-iamserviceaccount-ku-Role1-6XjLQsvXsMI3** associated

### Step-03-04: Verify k8s Service Account using kubectl
```bash
# Verify if any existing service account
$ kubectl get sa -n kube-system
$ kubectl get sa aws-load-balancer-controller -n kube-system
Obseravation:
1. We should see a new Service account created. 

# Describe Service Account aws-load-balancer-controller
$ kubectl describe sa aws-load-balancer-controller -n kube-system
```
- **Observation:** You can see that newly created Role ARN is added in `Annotations` confirming that **AWS IAM role bound to a Kubernetes service account**

## Install the AWS Load Balancer Controller using Helm V3
### Step-04-01: Install Helm
- [Install Helm](https://helm.sh/docs/intro/install/) if not installed
- [Install Helm for AWS EKS](https://docs.aws.amazon.com/eks/latest/userguide/helm.html)
```bash
# Install Helm (if not installed) MacOS
$ brew install helm

# Verify Helm version
$ helm version
```
### Step-04-02: Install AWS Load Balancer Controller
- **Important-Note-1:** If you're deploying the controller to Amazon EC2 nodes that have restricted access to the Amazon EC2 instance metadata service (IMDS), or if you're deploying to Fargate, then add the following flags to the command that you run:
```bash
--set region=region-code
--set vpcId=vpc-xxxxxxxx
```
- **Important-Note-2:** If you're deploying to any Region other than **us-west-2**, then add the following flag to the command that you run, replacing account and region-code with the values for your region listed in Amazon EKS add-on container image addresses.
- [Get Region Code and Account info](https://docs.aws.amazon.com/eks/latest/userguide/add-ons-images.html)
```bash
--set image.repository=account.dkr.ecr.region-code.amazonaws.com/amazon/aws-load-balancer-controller
```
```bash
# Add the eks-charts repository.
$ helm repo add eks https://aws.github.io/eks-charts

# Update your local repo to make sure that you have the most recent charts.
$ helm repo update

# Install the AWS Load Balancer Controller.
## Template
$ helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=<cluster-name> \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  --set region=<region-code> \
  --set vpcId=<vpc-xxxxxxxx> \
  --set image.repository=<account-id>.dkr.ecr.<region-code>.amazonaws.com/amazon/aws-load-balancer-controller

## Replace Cluster Name, Region Code, VPC ID, Image Repo Account ID and Region Code  
$ helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=k8s-cluster \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  --set region=us-east-1 \
  --set vpcId=vpc-06947630fad24f1e7 \
  --set image.repository=602401143452.dkr.ecr.us-east-1.amazonaws.com/amazon/aws-load-balancer-controller
```
- **Sample output for AWS Load Balancer Controller Install steps**
AWS Load Balancer controller installed!

### Verify that the controller is installed and Webhook Service created
```bash
``bash
# Verify that the controller is installed.
$ kubectl -n kube-system get deployment 
$ kubectl -n kube-system get deployment aws-load-balancer-controller
$ kubectl -n kube-system describe deployment aws-load-balancer-controller

# Verify AWS Load Balancer Controller Webhook service created
$ kubectl -n kube-system get svc 
$ kubectl -n kube-system get svc aws-load-balancer-webhook-service
$ kubectl -n kube-system describe svc aws-load-balancer-webhook-service

# Verify Labels in Service and Selector Labels in Deployment
$ kubectl -n kube-system get svc aws-load-balancer-webhook-service -o yaml
$ kubectl -n kube-system get deployment aws-load-balancer-controller -o yaml
```

### Step-04-04: Verify AWS Load Balancer Controller Logs
```bash
# List Pods
$ kubectl get pods -n kube-system

# Review logs for AWS LB Controller POD-1
$ kubectl -n kube-system logs -f <POD-NAME> 
$ kubectl -n kube-system logs -f aws-load-balancer-controller-7dd5dc4689-4tdrk

# Review logs for AWS LB Controller POD-2
$ kubectl -n kube-system logs -f <POD-NAME> 
$ kubectl -n kube-system logs -f aws-load-balancer-controller-7dd5dc4689-5m86v
```

### Step-04-05: Verify AWS Load Balancer Controller k8s Service Account - Internals
```bash
# List Service Account and its secret
$ kubectl -n kube-system get sa aws-load-balancer-controller
$ kubectl -n kube-system get sa aws-load-balancer-controller -o yaml
$ kubectl -n kube-system get secret <GET_FROM_PREVIOUS_COMMAND - secrets.name> -o yaml
$ kubectl -n kube-system get secret aws-load-balancer-controller-token-5w8th 
$ kubectl -n kube-system get secret aws-load-balancer-controller-token-5w8th -o yaml

## Decode ca.crt using below two websites
https://www.base64decode.org/
https://www.sslchecker.com/certdecoder

## Decode token using below two websites
https://www.base64decode.org/
https://jwt.io/

Observation 1: Review decoded JWT Token

# List Deployment in YAML format
$ kubectl -n kube-system get deploy aws-load-balancer-controller -o yaml

Observation 1: Verify "spec.template.spec.serviceAccount" and "spec.template.spec.serviceAccountName" in "aws-load-balancer-controller" Deployment
Observation 2: We should find the Service Account Name as "aws-load-balancer-controller"

# List Pods in YAML format
$ kubectl -n kube-system get pods
$ kubectl -n kube-system get pod <AWS-Load-Balancer-Controller-POD-NAME> -o yaml
$ kubectl -n kube-system get pod aws-load-balancer-controller-7dd5dc4689-4tdrk -o yaml

Observation 1: Verify "spec.serviceAccount" and "spec.serviceAccountName"
Observation 2: We should find the Service Account Name as "aws-load-balancer-controller"
Observation 3: Verify "spec.volumes". You should find something as below, which is a temporary credentials to access AWS Services
CHECK-1: Verify "spec.volumes.name = aws-iam-token"
  - name: aws-iam-token
    projected:
      defaultMode: 420
      sources:
      - serviceAccountToken:
          audience: sts.amazonaws.com
          expirationSeconds: 86400
          path: token
CHECK-2: Verify Volume Mounts
    volumeMounts:
    - mountPath: /var/run/secrets/eks.amazonaws.com/serviceaccount
      name: aws-iam-token
      readOnly: true          
CHECK-3: Verify ENVs whose path name is "token"
    - name: AWS_WEB_IDENTITY_TOKEN_FILE
      value: /var/run/secrets/eks.amazonaws.com/serviceaccount/token          
```

### Step-04-06: Verify TLS Certs for AWS Load Balancer Controller - Internals
```bash
# List aws-load-balancer-tls secret 
$ kubectl -n kube-system get secret aws-load-balancer-tls -o yaml

# Verify the ca.crt and tls.crt in below websites
https://www.base64decode.org/
https://www.sslchecker.com/certdecoder

# Make a note of Common Name and SAN from above 
Common Name: aws-load-balancer-controller
SAN: aws-load-balancer-webhook-service.kube-system, aws-load-balancer-webhook-service.kube-system.svc

# List Pods in YAML format
$ kubectl -n kube-system get pods
$ kubectl -n kube-system get pod <AWS-Load-Balancer-Controller-POD-NAME> -o yaml
$ kubectl -n kube-system get pod aws-load-balancer-controller-65b4f64d6c-h2vh4 -o yaml
Observation:
1. Verify how the secret is mounted in AWS Load Balancer Controller Pod
CHECK-2: Verify Volume Mounts
    volumeMounts:
    - mountPath: /tmp/k8s-webhook-server/serving-certs
      name: cert
      readOnly: true
CHECK-3: Verify Volumes
  volumes:
  - name: cert
    secret:
      defaultMode: 420
      secretName: aws-load-balancer-tls
```

### Step-04-07: UNINSTALL AWS Load Balancer Controller using Helm Command (Information Purpose - SHOULD NOT BE EXECUTED)
- This step should not be implemented.
- This is just put it here for us to know how to uninstall aws load balancer controller from EKS Cluster

```bash
# Uninstall AWS Load Balancer Controller
$ helm uninstall aws-load-balancer-controller -n kube-system 
```

## Ingress Class Concept
- Understand what is Ingress Class
- Understand how it overrides the default deprecated annotation `#kubernetes.io/ingress.class: "alb"`
- [Ingress Class Documentation Reference](https://kubernetes-sigs.github.io/aws-load-balancer-controller/latest/guide/ingress/ingress_class/)
- [Different Ingress Controllers available today](https://kubernetes.io/docs/concepts/services-networking/ingress-controllers/)


## Step-06: Review IngressClass Kubernetes Manifest
- **File Location:** `aws-eks/08-elb-application-load-balancers/01-install-load-balancer-controller/kube-manifests/01-ingressclass-resource.yaml`
- Understand in detail about annotation `ingressclass.kubernetes.io/is-default-class: "true"`
```yaml
apiVersion: networking.k8s.io/v1
kind: IngressClass
metadata:
  name: my-aws-ingress-class
  annotations:
    ingressclass.kubernetes.io/is-default-class: "true"
spec:
  controller: ingress.k8s.aws/alb

## Additional Note
# 1. You can mark a particular IngressClass as the default for your cluster. 
# 2. Setting the ingressclass.kubernetes.io/is-default-class annotation to true on an IngressClass resource will ensure that new Ingresses without an ingressClassName field specified will be assigned this default IngressClass.  
# 3. Reference: https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.3/guide/ingress/ingress_class/
```

## Step-07: Create IngressClass Resource
```bash
# Create IngressClass Resource
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/01-install-load-balancer-controller/kube-manifests/01-ingressclass-resource.yaml

# Verify IngressClass Resource
$ kubectl get ingressclass

# Describe IngressClass Resource
$ kubectl describe ingressclass my-aws-ingress-class
```

## References
- [AWS Load Balancer Controller Install](https://docs.aws.amazon.com/eks/latest/userguide/aws-load-balancer-controller.html)
- [ECR Repository per region](https://docs.aws.amazon.com/eks/latest/userguide/add-ons-images.html)









