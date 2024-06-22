# EKS Storage with EBS - Elastic Block Store

## Introduction
- Create IAM Policy for Elastic Block Storage (EBS)
- Associate IAM Policy to Worker Node IAM Role
- Install EBS CSI Driver

## Create IAM policy
- Go to **Services** -> **IAM**
- Create a **Policy**
- Select JSON tab, copy and paste the below JSON

#### IAM policy
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ec2:AttachVolume",
        "ec2:CreateSnapshot",
        "ec2:CreateTags",
        "ec2:CreateVolume",
        "ec2:DeleteSnapshot",
        "ec2:DeleteTags",
        "ec2:DeleteVolume",
        "ec2:DescribeInstances",
        "ec2:DescribeSnapshots",
        "ec2:DescribeTags",
        "ec2:DescribeVolumes",
        "ec2:DetachVolume"
      ],
      "Resource": "*"
    }
  ]
}
```
- Review the same in **IDE**
- Click on **Review Policy**
- **Policy Name:** Amazon_EBS_CSI_Driver
- **Description:** Policy for EC2 Instances to access Elastic Block Store for EKS
- Click on **Create Policy**

## Get the IAM role Worker Nodes ARN and Associate this policy to that role

```bash
# Get Worker node IAM Role ARN
$ kubectl -n kube-system describe configmap aws-auth

# from output check for rolearn
$ rolearn: arn:aws:iam::846374492554:role/eksctl-k8s-cluster-nodegroup-k8s-c-NodeInstanceRole-a7FpPg4WrbWR
```
- Go to **Services** -> **IAM** -> **Roles**
- Search for role with name **eksctl-k8s-cluster-nodegroup-k8s-c-NodeInstanceRole-a7FpPg4WrbWR** and open it
- Click on **Permissions** tab
- Click on **Add Permissions** and attach policy
- Search for **Amazon_EBS_CSI_Driver** and click on **Add Permissions**
- A customer managed policy will be added

## Deploy Amazon EBS CSI Driver

#### Check the kubectl version. It should be 1.14 or later
```bash
$ kubectl version --client
```
- Deploy Amazon EBS CSI Driver
```bash
# Deploy EBS CSI Driver
$ kubectl apply -k "github.com/kubernetes-sigs/aws-ebs-csi-driver/deploy/kubernetes/overlays/stable/?ref=master"

# Verify ebs-csi pods running
$ kubectl get pods -n kube-system
```