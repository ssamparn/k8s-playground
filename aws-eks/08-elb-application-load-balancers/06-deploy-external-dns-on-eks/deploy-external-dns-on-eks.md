---
title: AWS Load Balancer Controller - External DNS Install
description: Learn AWS Load Balancer Controller - External DNS Install
---

## Introduction
- **External DNS:** Used for Updating Route53 RecordSets from Kubernetes 
- We need to create IAM Policy, k8s Service Account & IAM Role and associate them together for external-dns pod to add or remove entries in AWS Route53 Hosted Zones. 
- Update External-DNS default manifest to support our needs
- Deploy & Verify logs

## Create IAM Policy
- This IAM policy will allow external-dns pod to add, remove DNS entries (Record Sets in a Hosted Zone) in AWS Route53 service
- Go to **Services** -> **IAM** -> **Policies** -> **Create Policy**
  - Click on **JSON** Tab and copy paste below JSON
  - Click on **Visual editor** tab to validate
  - Click on **Review Policy**
  - **Name:** AllowExternalDNSUpdates 
  - **Description:** Allow access to Route53 Resources for ExternalDNS
  - Click on **Create Policy**  

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "route53:ChangeResourceRecordSets"
      ],
      "Resource": [
        "arn:aws:route53:::hostedzone/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "route53:ListHostedZones",
        "route53:ListResourceRecordSets"
      ],
      "Resource": [
        "*"
      ]
    }
  ]
}
```
- Make a note of Policy ARN which we will use in next step

### Policy ARN
arn:aws:iam::180789647333:policy/AllowExternalDNSUpdates


## Create IAM Role, k8s Service Account & Associate IAM Policy
- As part of this step, we are going to create a k8s Service Account named `external-dns` and also a AWS IAM role and associate them by annotating role ARN in Service Account.
- In addition, we are also going to associate the AWS IAM Policy `AllowExternalDNSUpdates` to the newly created AWS IAM Role.
### Create IAM Role, k8s Service Account & Associate IAM Policy
```bash
# Template
$ eksctl create iamserviceaccount \
    --name service_account_name \
    --namespace service_account_namespace \
    --cluster cluster_name \
    --attach-policy-arn IAM_policy_ARN \
    --approve \
    --override-existing-serviceaccounts

# Replaced name, namespace, cluster, IAM Policy arn 
$ eksctl create iamserviceaccount \
    --name external-dns \
    --namespace default \
    --cluster k8s-cluster \
    --attach-policy-arn arn:aws:iam::180789647333:policy/AllowExternalDNSUpdates \
    --approve \
    --override-existing-serviceaccounts
```
###  Verify the Service Account
- Verify external-dns service account, primarily verify annotation related to IAM Role
```bash
# List Service Account
$ kubectl get sa external-dns

# Describe Service Account
$ kubectl describe sa external-dns
Observation: 
1. Verify the Annotations and you should see the IAM Role is present on the Service Account
```

###  Verify CloudFormation Stack
- Go to **Services** -> **Cloud Formation**
- Verify the latest **CFN Stack** created.
- Click on **Resources** tab
- Click on link  in **Physical ID** field which will take us to **IAM Role** directly

### Verify IAM Role & IAM Policy
- With above step in CFN, we will be landed in IAM Role created for external-dns. 
- Verify in **Permissions** tab we have a policy named **AllowExternalDNSUpdates**
- Now make a note of that Role ARN, this we need to update in External-DNS k8s manifest

### IAM Role ARN
arn:aws:iam::180789647333:role/eksctl-eksdemo1-addon-iamserviceaccount-defa-Role1-JTO29BVZMA2N

### Verify IAM Service Accounts using eksctl
You can also make a note of External DNS Role ARN from here too. 
```bash
# List IAM Service Accounts using eksctl
$ eksctl get iamserviceaccount --cluster k8s-cluster
```

## Update External DNS Kubernetes manifest
- **Original Template** you can find in https://github.com/kubernetes-sigs/external-dns/blob/master/docs/tutorials/aws.md

### Get latest Docker Image name
[Get latest external dns image name](https://github.com/kubernetes-sigs/external-dns/releases/tag/v0.10.2)
```yaml
    spec:
      serviceAccountName: external-dns
      containers:
      - name: external-dns
        image: k8s.gcr.io/external-dns/external-dns:v0.10.2
```

## Deploy ExternalDNS manifests
- Deploy the manifest
```bash
# Deploy external DNS
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/06-deploy-external-dns-on-eks/kube-manifests/.

# List All resources from default Namespace
$ kubectl get all

# List pods (external-dns pod should be in running state)
$ kubectl get pods

# Verify Deployment by checking logs
$ kubectl logs -f $(kubectl get po | egrep -o 'external-dns[A-Za-z0-9-]+')
```

## References
- https://github.com/kubernetes-sigs/external-dns/blob/master/docs/tutorials/alb-ingress.md
- https://github.com/kubernetes-sigs/external-dns/blob/master/docs/tutorials/aws.md


