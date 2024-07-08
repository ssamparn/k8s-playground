---
title: AWS Load Balancer Controller - External DNS & Ingress
description: Learn AWS Load Balancer Controller - External DNS & Ingress
---

## Update Ingress manifest by adding External DNS Annotation
- Added annotation with two DNS Names
    - dnstest901.ssamparn.com
    - dnstest902.ssamparn.com
- Once we deploy the application, we should be able to access our applications with both DNS Names.
```yaml
    # External DNS - For creating a Record Set in Route53
    external-dns.alpha.kubernetes.io/hostname: dnstest901.ssamparn.com, dnstest902.ssamparn.com
```
- In your case it is going to be, replace `yourdomain` with your domain name
    - dnstest901.yourdoamin.com
    - dnstest902.yourdoamin.com

## Deploy all Application Kubernetes Manifests
### Deploy
```bash
# Deploy kube-manifests
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/07-use-external-dns-on-k8s-ingress/kube-manifests/.

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

### Verify External DNS Log
```bash
# Verify External DNS logs
$ kubectl logs -f $(kubectl get po | egrep -o 'external-dns[A-Za-z0-9-]+')
```
### Verify Route53
- Go to Services -> Route53
- You should see **Record Sets** added for `dnstest901.ssamparn.com`, `dnstest902.ssamparn.com`

## Access Application using newly registered DNS Name
### Perform nslookup tests before accessing application
- Test if our new DNS entries registered and resolving to an IP Address
```bash
# nslookup commands
$ nslookup dnstest901.ssamparn.com
$ nslookup dnstest902.ssamparn.com
```
### Access Application using dnstest1 domain
```bash
# HTTP URLs (Should Redirect to HTTPS)
http://dnstest901.ssamparn.com/app1/v1
http://dnstest901.ssamparn.com/app2/v2
http://dnstest901.ssamparn.com/app3/v3
```

### Access Application using dnstest2 domain
```bash
# HTTP URLs (Should Redirect to HTTPS)
http://dnstest902.ssamparn.com/app1/v1
http://dnstest902.ssamparn.com/app2/v2
http://dnstest902.ssamparn.com/app3/v3
```

## Clean Up
```bash
# Delete Manifests
$ kubectl delete -f aws-eks/08-elb-application-load-balancers/07-use-external-dns-on-k8s-ingress/kube-manifests/.
```

## Verify Route53 Record Set to ensure our DNS records got deleted
- Go to **Route53** -> **Hosted Zones** -> **Records** 
- The below records should be deleted automatically
  - dnstest901.ssamparn.com
  - dnstest902.ssamparn.com


## References
- https://github.com/kubernetes-sigs/external-dns/blob/master/docs/tutorials/alb-ingress.md
- https://github.com/kubernetes-sigs/external-dns/blob/master/docs/tutorials/aws.md


