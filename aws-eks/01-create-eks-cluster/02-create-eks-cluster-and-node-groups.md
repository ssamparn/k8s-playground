# Create EKS Cluster & Node Groups:

## Step-01: Create EKS Cluster using eksctl

#### Create EKS Cluster
```bash
$ eksctl create cluster --name=k8s-cluster \
                      --region=us-east-1 \
                      --zones=us-east-1a,us-east-1b \
                      --without-nodegroup
```
> Reference: https://docs.aws.amazon.com/eks/latest/userguide/create-cluster.html

#### Get List of clusters
```bash
$ eksctl get cluster              
```
> It will take 15 to 20 minutes to create the Cluster Control Plane in AWS EKS

## Step-02: Create & Associate IAM OIDC Provider for our EKS Cluster
- To enable and use AWS IAM roles for Kubernetes service accounts on our EKS cluster, we must create & associate OIDC identity provider.
- To do so using `eksctl` we can use the  below command.
- Use latest eksctl version (as of today the latest version is `0.21.0`)

```bash                   
# Template
$ eksctl utils associate-iam-oidc-provider \
    --region region-code \
    --cluster <cluter-name> \
    --approve

# Replace with region & cluster name
$ eksctl utils associate-iam-oidc-provider \
    --region us-east-1 \
    --cluster k8s-cluster \
    --approve
```

## Step-03: Create EC2 Keypair
- Create a new EC2 Keypair with name as `kube-demo`. For that go to `Network & Seurity -> Key Pairs`. Create a new keypair
- This keypair we will use it when creating the EKS NodeGroup.
- This will help us to login to the EKS Worker Nodes using Terminal.

## Step-04: Create Node Group with additional Add-Ons in Public Subnets

- These add-ons will create the respective IAM policies for us automatically within our Node Group role.
```bash
$ eksctl create --help
$ eksctl create cluster --help
$ eksctl create nodegroup --help
```
```bash
# Create Public Node Group
$ eksctl create nodegroup --cluster=k8s-cluster \
                        --region=us-east-1 \
                        --name=k8s-cluster-ng-public1 \
                        --node-type=t3.medium \
                        --nodes=2 \
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
                        --alb-ingress-access 
```
> It will take 3 to 5 minutes to create the Node Group

```bash
# Get all the nodes
$ kubectl get nodes
$ kubectl get nodes -o wide
```

## Step-05: Verify Cluster & Nodes

### Verify Cluster, NodeGroup in EKS Management Console
- Go to **Services** -> Elastic Kubernetes Service **(EKS)** -> **k8s-cluster**

### Verify NodeGroup subnets to confirm EC2 Instances are in Public Subnet
- Verify the node group subnet to ensure it created in public subnets
    - Go to **Services** -> **EKS** -> **k8s-cluster** -> **Compute** -> **k8s-cluster-ng-public1**. Click on the NodeGroup.
    - Click on Associated subnet in **Details** tab. Click on any one of the subnet.
    - Click on **Route Table** Tab.
    - We should see that internet route via Internet Gateway (0.0.0.0/0 -> igw-xxxxxxxx)

### List Worker Nodes
```bash
# List EKS clusters
$ eksctl get cluster

# List NodeGroups in a cluster
$ eksctl get nodegroup --cluster=<clusterName>
$ eksctl get nodegroup --cluster=k8s-cluster

# List Nodes in current kubernetes cluster
$ kubectl get nodes -o wide

# Our kubectl context should be automatically changed to new cluster
$ kubectl config view --minify
```

### Verify Worker Node IAM Role and list of Policies
- Go to **Services** -> **EC2** -> **Worker Nodes**
- Click on **IAM Role** associated to EC2 Worker Nodes in the Details Tab.


### Verify CloudFormation Stacks
- Go to **Services** -> **Cloud Formation** -> **Stacks** -> **Worker Node Group**
- Verify Control Plane Stack & Events
- Verify NodeGroup Stack & Events

### Verify NAT Gateway
- Go to **Services** -> **VPC** -> **NAT Gateways**

### Login to Worker Node using Keypair `kube-demo`
- Login to worker node

```bash
# For MAC or Linux or Windows10
$ cd my_projects/backend/kubernetes/k8s-cloud-resources/
$ chmod 400 kube-demo.pem
$ ssh -i kube-demo.pem ec2-user@<Public-IP-of-anyone-Worker-Node>
$ ssh -i kube-demo.pem ec2-user@54.85.200.111
$ df -h
# For Windows 7
Use putty
```

### Verify Security Group Associated to Worker Nodes
- Go to **Services** -> **EC2** -> **Worker Nodes**
- Click on **Security Group** associated to EC2 Instance which contains `remoteAccess` in the name in the Security Tab.

## Step-06: Update Worker Nodes Security Group to allow all traffic
- We need to allow `All Traffic` on worker node security group.
- So basically, we need to add Type `All Traffic` from source `IPv4-0.0.0.0/0` and `IPv6-::/0` in the security groups inbound rules.


## Additional References
- https://docs.aws.amazon.com/eks/latest/userguide/enable-iam-roles-for-service-accounts.html
- https://docs.aws.amazon.com/eks/latest/userguide/create-service-account-iam-policy-and-role.html
