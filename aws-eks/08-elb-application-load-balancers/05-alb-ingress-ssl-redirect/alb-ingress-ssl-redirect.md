---
title: AWS Load Balancer - Ingress SSL HTTP to HTTPS Redirect
description: Learn AWS Load Balancer - Ingress SSL HTTP to HTTPS Redirect
---

## Add annotations related to SSL Redirect
- Redirect from HTTP to HTTPS
```yaml
    # SSL Redirect Setting
    alb.ingress.kubernetes.io/ssl-redirect: '443'   
```

## Deploy all manifests and test

### Deploy and Verify
```bash
# Deploy kube-manifests
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/05-alb-ingress-ssl-redirect/kube-manifests/.

# Verify Ingress Resource
$ kubectl get ingress

# Verify Apps
$ kubectl get deploy
$ kubectl get pods

# Verify NodePort Services
$ kubectl get svc
```

### Verify Load Balancer & Target Groups
- Load Balancer -  Listeneres (Verify both 80 & 443)
- Load Balancer - Rules (Verify both 80 & 443 listeners)
- Target Groups - Group Details (Verify Health check path)
- Target Groups - Targets (Verify all 3 targets are healthy)

## Access Application using newly registered DNS Name
- **Access Application**
```bash
# HTTP URLs (Should Redirect to HTTPS)
http://ssl-ingress.domain.com/app1/v1
http://ssl-ingress.domain.com/app2/v2
http://ssl-ingress.domain.com/app3/v3

# HTTPS URLs
https://ssl-ingress.domain.com/app1/v1
https://ssl-ingress.domain.com/app2/v2
https://ssl-ingress.domain.com/app3/v3
```

## Clean Up
```bash
# Delete Manifests
$ kubectl delete -f aws-eks/08-elb-application-load-balancers/05-alb-ingress-ssl-redirect/kube-manifests/.

## Delete Route53 Record Set
- Delete Route53 Record we created (ssldemo101.stacksimplify.com)
```



