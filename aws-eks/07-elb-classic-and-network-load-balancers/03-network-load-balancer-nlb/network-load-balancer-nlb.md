# AWS - Network Load Balancer - NLB

## Create AWS Network Load Balancer Kubernetes Manifest & Deploy
- **03-user-management-nlb-service.yaml**

## Deploy all Manifest
```bash
# deploy all manifests
$ kubectl apply -f aws-eks/07-elb-classic-and-network-load-balancers/03-network-load-balancer-nlb/kube-manifests/.

# list services (verify newly created NLB service)
$ kubectl get svc

# verify pods
$ kubectl get pods
```

## Verify the deployment
- Verify if new CLB got created
    - Go to **Services** -> **EC2** -> **Load Balancing** -> **Load Balancers**
        - **CLB** should have been created
        - Copy **DNS Name** (Example: a85ae6e4030aa4513bd200f08f1eb9cc-7f13b3acc1bcaaa2.elb.us-east-1.amazonaws.com)
    - Go to **Services** -> **EC2** -> **Load Balancing** -> **Target Groups**
        - Verify the health status, we should see active.
- **Access Application**
```bash
# access application
curl http://<nlb-dns-name>/actuator/health
```    

## Clean Up
```bash
# delete all objects created
$ kubectl delete -f aws-eks/07-elb-classic-and-network-load-balancers/03-network-load-balancer-nlb/kube-manifests/.

# verify current kubernetes objects
$ kubectl get all
```


