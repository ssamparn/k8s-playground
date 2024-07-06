---
title: AWS Load Balancer Ingress Context Path Based Routing
description: Learn AWS Load Balancer Controller - Ingress Context Path Based Routing
---

## Introduction
- We are going to deploy all these 3 apps in kubernetes with context path based routing enabled in Ingress Controller
    - **/*** - should go to **nginx-app1-service**
    - **/app2/*** - should go to **nginx-app2-service**
    - **/app3/*** - should go to **nginx-app3-service**
    
- As part of this process, the respective annotation `alb.ingress.kubernetes.io/healthcheck-path:` will be moved to respective application NodePort Service.
- Only generic settings will be present in Ingress manifest annotations area `04-alb-ingress-context-path-based-routing.yaml`

## Deploy all manifests and test
```bash
# Deploy Kubernetes manifests
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/03-alb-ingress-context-path-based-routing/kube-manifests/.

# List Pods
$ kubectl get pods

# List Services
$ kubectl get svc

# List Ingress Load Balancers
$ kubectl get ingress

# Describe Ingress and view Rules
$ kubectl describe ingress context-path-routing-ingress

# Verify AWS Load Balancer Controller logs
$ kubectl -n kube-system get pods 
$ kubectl -n kube-system logs -f aws-load-balancer-controller-794b7844dd-8hk7n 
```

## Verify Application Load Balancer on AWS Management Console**
- Verify Load Balancer
    - In Listeners Tab, click on **View/Edit Rules** under Rules
- Verify Target Groups
    - GroupD Details
    - Targets: Ensure they are healthy
    - Verify Health check path
    - Verify all 3 targets are healthy)
```bash
# Access Application
$ curl http://<ALB-DNS-URL>/app1/index.html
$ curl http://<ALB-DNS-URL>/app2/index.html
$ curl http://<ALB-DNS-URL>/
```

## Clean Up
```bash
# Clean Up
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/03-alb-ingress-context-path-based-routing/kube-manifests/.
```
