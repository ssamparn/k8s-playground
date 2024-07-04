# EKS - Create EKS Node Group in Private Subnets

## Introduction
- We are going to create a node group in VPC Private Subnets
- We are going to deploy workloads on the private node group wherein workloads will be running private subnets and load balancer gets created in public subnet and accessible via internet.

## Delete existing Public Node Group in EKS Cluster
```bash
# get node groups in a EKS cluster
$ eksctl get nodegroup --cluster=<Cluster-Name>
$ eksctl get nodegroup --cluster=k8s-cluster

# delete node group - Replace nodegroup name and cluster name
$ eksctl delete nodegroup <node-group-name> --cluster <cluster-name>
$ eksctl delete nodegroup k8s-cluster-ng-public1 --cluster k8s-cluster
```

## Create EKS Node Group in Private Subnets
- Create Private Node Group in a Cluster
- Key option for the command is `--node-private-networking`

```bash
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

## Verify if Node Group created in Private Subnets

### Verify External IP Address for Worker Nodes
- External IP Address should be none if our Worker Nodes created in Private Subnets
```bash
$ kubectl get nodes -o wide
```
### Subnet Route Table Verification - Outbound Traffic goes via NAT Gateway
- Verify the node group subnet routes to ensure it created in private subnets
    - Go to Services -> EKS -> k8s-cluster -> k8s-cluster-ng1-private
    - Click on Associated subnet in **Details** tab
    - Click on **Route Table** Tab.
    - We should see that internet route via NAT Gateway (0.0.0.0/0 -> nat-xxxxxxxx)
