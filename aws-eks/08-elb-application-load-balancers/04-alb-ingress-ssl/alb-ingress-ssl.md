---
title: AWS Load Balancer Ingress SSL
description: Learn AWS Load Balancer Controller - Ingress SSL
---

## Deploy all manifests and test
### Deploy and Verify
```bash
# Deploy kube-manifests
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/04-alb-ingress-ssl/kube-manifests/.

# Verify Ingress Resource
$ kubectl get ingress

# Verify Apps
$ kubectl get deploy
$ kubectl get pods

# Verify NodePort Services
$ kubectl get svc
```

### Verify Load Balancer & Target Groups
- Load Balancer - Listeners (Verify both 80 & 443)
- Load Balancer - Rules (Verify both 80 & 443 listeners)
- Target Groups - Group Details (Verify Health check path)
- Target Groups - Targets (Verify all 3 targets are healthy)

## Add DNS in Route53
- Go to **Services** -> **Route 53**
- Go to **Hosted Zones**
    - Click on **yourdomain.com** (in my case stacksimplify.com)
- Create a **Record Set**
    - **Name:** ssldemo101.stacksimplify.com
    - **Alias:** yes
    - **Alias Target:** Copy our ALB DNS Name here (Sample: ssl-ingress-551932098.us-east-1.elb.amazonaws.com)
    - Click on **Create**

## Access Application using newly registered DNS Name
- **Access Application**
- **Important Note:** Instead of you need to replace with your registered Route53 domain (Refer pre-requisite Step-02)
```bash
# HTTP URLs
http://ssldemo101.stacksimplify.com/app1/index.html
http://ssldemo101.stacksimplify.com/app2/index.html
http://ssldemo101.stacksimplify.com/

# HTTPS URLs
https://ssldemo101.stacksimplify.com/app1/index.html
https://ssldemo101.stacksimplify.com/app2/index.html
https://ssldemo101.stacksimplify.com/
```