---
title: AWS Load Balancer Controller - Ingress Basics
description: Learn AWS Load Balancer Controller - Ingress Basics
---

## Introduction
- Discuss about the Application Architecture which we are going to deploy
- Understand the following Ingress Concepts
    - [Annotations](https://kubernetes-sigs.github.io/aws-load-balancer-controller/latest/guide/ingress/annotations/)
    - [ingressClassName](https://kubernetes-sigs.github.io/aws-load-balancer-controller/latest/guide/ingress/ingress_class/)
    - defaultBackend
    - rules

## Deploy kube-manifests and Verify
```bash
$ cd aws-eks/08-elb-application-load-balancers/02-alb-ingress-basics/docker/
$ docker build -t ssamantr/ingress-nginx-app:1.0.0 .
$ docker images
$ docker run --name ingress-nginx-app -p 8080:80 ssamantr/ingress-nginx-app:1.0.0
$ docker exec -it <container-id> bash
$ curl http://localhost:8080
$ docker push ssamantr/ingress-nginx-app:1.0.0

# Deploy kube-manifests
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/02-alb-ingress-basics/kube-manifests-default-backend/.

# Verify k8s Deployment and Pods
$ kubectl get deployments
$ kubectl get pods

# Verify Ingress (Make a note of Address field)
$ kubectl get ingress
Obsevation: 
1. Verify the ADDRESS value, we should see something like `app-ingress-lb-24019112.us-east-1.elb.amazonaws.com`

# Describe Ingress Controller
$ kubectl describe ingress ingress-nginx-app
Observation:
1. Review Default Backend and Rules

# List Services
$ kubectl get svc

# Verify Application Load Balancer using 
Goto **AWS Management Console** -> **Services** -> **EC2** -> **Load Balancers**
1. Verify Listeners and Rules inside a listener
2. Verify Target Groups

# Access App using Browser
$ kubectl get ingress
$ curl http://<ALB-DNS-URL>/
$ curl app-ingress-lb-24019112.us-east-1.elb.amazonaws.com
or

$ curl http://<INGRESS-ADDRESS-FIELD>
$ curl http://<INGRESS-ADDRESS-FIELD>/

# Verify AWS Load Balancer Controller logs
$ kubectl get po -n kube-system 
## POD1 Logs: 
$ kubectl -n kube-system logs -f <POD1-NAME>
$ kubectl -n kube-system logs -f aws-load-balancer-controller-65b4f64d6c-h2vh4
## POD2 Logs: 
$ kubectl -n kube-system logs -f <POD2-NAME>
$ kubectl -n kube-system logs -f aws-load-balancer-controller-65b4f64d6c-t7qqb
```

## Clean Up
```bash
# Delete Kubernetes Resources
$ kubectl delete -f aws-eks/08-elb-application-load-balancers/02-alb-ingress-basics/kube-manifests-default-backend/.
```

## Review Ingress kube-manifest with Ingress Rules
- Discuss about [Ingress Path Types](https://kubernetes.io/docs/concepts/services-networking/ingress/#path-types)
- [Better Path Matching With Path Types](https://kubernetes.io/blog/2020/04/02/improvements-to-the-ingress-api-in-kubernetes-1.18/#better-path-matching-with-path-types)
- [Sample Ingress Rule](https://kubernetes.io/docs/concepts/services-networking/ingress/#the-ingress-resource)
- **ImplementationSpecific (default):** With this path type, matching is up to the controller implementing the IngressClass. Implementations can treat this as a separate pathType or treat it identically to the Prefix or Exact path types.
- **Exact:** Matches the URL path exactly and with case sensitivity.
- **Prefix:** Matches based on a URL path prefix split by /. Matching is case-sensitive and done on a path element by element basis.

## Deploy kube-manifests and Verify
```bash
# Deploy kube-manifests
$ kubectl apply -f aws-eks/08-elb-application-load-balancers/02-alb-ingress-basics/kube-manifests-rules/.

# Verify k8s Deployment and Pods
$ kubectl get deploy
$ kubectl get pods

# Verify Ingress (Make a note of Address field)
$ kubectl get ingress
Obsevation: 
1. Verify the ADDRESS value, we should see something like `app-ingress-lb-24019112.us-east-1.elb.amazonaws.com`

# Describe Ingress Controller
$ kubectl describe ingress ingress-nginx-app
Observation:
1. Review Default Backend and Rules

# List Services
$ kubectl get svc

# Verify Application Load Balancer using 
Goto **AWS Management Console** -> **Services** -> **EC2** -> **Load Balancers**
1. Verify Listeners and Rules inside a listener
2. Verify Target Groups

# Access App using Browser
$ kubectl get ingress
http://<ALB-DNS-URL>
http://<ALB-DNS-URL>/
or
http://<INGRESS-ADDRESS-FIELD>
http://<INGRESS-ADDRESS-FIELD>/

# Verify AWS Load Balancer Controller logs
$ kubectl get po -n kube-system 
$ kubectl logs -f aws-load-balancer-controller-794b7844dd-8hk7n -n kube-system
```

## Clean Up
```bash
# Delete Kubernetes Resources
$ kubectl delete -f aws-eks/08-elb-application-load-balancers/02-alb-ingress-basics/kube-manifests-rules/.

# Verify if Ingress Deleted successfully 
$ kubectl get ingress
Important Note: It is going to cost us heavily if we leave ALB load balancer idle without deleting it properly

# Verify if the application load balancer got deleted properly 
Goto **AWS Management Console** -> **Services** -> **EC2** -> **Load Balancers**
```



