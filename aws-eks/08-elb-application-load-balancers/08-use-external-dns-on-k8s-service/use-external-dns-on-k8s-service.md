---
title: AWS Load Balancer Controller - External DNS & Service
description: Learn AWS Load Balancer Controller - External DNS & Kubernetes Service
---

## Introduction
- We will create a Kubernetes Service of `type: LoadBalancer`
- We will annotate that Service with external DNS hostname `external-dns.alpha.kubernetes.io/hostname: externaldns-k8s-service-demo101.ssamparn.com` which will register the DNS in Route53 for that respective load balancer

## Create nginx-app-service docker image from docker file
```bash
$ cd aws-eks/08-elb-application-load-balancers/08-use-external-dns-on-k8s-service/docker
$ docker build -t ssamantr/nginx-app-service:1.0.0 .
$ docker images
$ docker run --name nginx-app-service -p 8080:80 ssamantr/nginx-app-service:1.0.0
$ docker exec -it <container-id> bash
$ curl http://localhost:8080
$ docker push ssamantr/nginx-app-service:1.0.0
```

### Deploy & Verify
```bash
# Deploy kube-manifests
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/08-use-external-dns-on-k8s-service/kube-manifests/.

# Verify Apps
$ kubectl get deploy
$ kubectl get pods

# Verify Service
$ kubectl get svc
```

### Verify Load Balancer
- Go to **EC2** -> **Load Balancers** -> Verify **Load Balancer Settings**

### Verify External DNS Log
```bash
# Verify External DNS logs
$ kubectl logs -f $(kubectl get po | egrep -o 'external-dns[A-Za-z0-9-]+')
```

### Verify Route53
- Go to Services -> Route53
- You should see **Record Sets** added for `externaldns-k8s-service-demo101.ssamparn.com`


## Access Application using newly registered DNS Name
### Perform nslookup tests before accessing Application
- Test if our new DNS entries registered and resolving to an IP Address
```bash
# nslookup commands
$ nslookup externaldns-k8s-service-demo101.stacksimplify.com
```

### Access Application using DNS domain
```bash
# HTTP URL
$ curl http://externaldns-k8s-service-demo101.stacksimplify.com/app1/index.html
```

## Clean Up
```bash
# Delete Manifests
$ kubectl delete -f aws-eks/08-elb-application-load-balancers/08-use-external-dns-on-k8s-service/kube-manifests/.
```

## Verify Route53 Record Set to ensure our DNS records got deleted
- Go to **Route53** -> **Hosted Zones** -> **Records** 
- The below records should be deleted automatically
  - externaldns-k8s-service-demo101.ssamparn.com

## References
- https://github.com/kubernetes-sigs/external-dns/blob/master/docs/tutorials/alb-ingress.md
- https://github.com/kubernetes-sigs/external-dns/blob/master/docs/tutorials/aws.md
