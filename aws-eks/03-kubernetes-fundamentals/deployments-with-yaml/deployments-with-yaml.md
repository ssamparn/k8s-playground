# Deployments with YAML

```bash
# Create Deployment
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/deployments-with-yaml/kube-manifests/deployment-definition.yaml
$ kubectl get deploy
$ kubectl get rs
$ kubectl get po

# Create NodePort Service
$ kubectl apply -f aws-eks/03-kubernetes-fundamentals/deployments-with-yaml/kube-manifests/deployment-nodeport-service.yaml
# List Service
$ kubectl get svc

# Get Public IP
$ kubectl get nodes -o wide

# Access Application
$ curl http://<Worker-Node-Public-IP>:31233
or 
$ kubectl port-forward deployment/backend-app-deployment 8080:8080
$ curl http://localhost:8080/hello

$ kubectl port-forward service/deployment-node-port-service 8080:8080
$ curl http://localhost:8080/hello

# Delete Deployment
$ kubectl delete -f aws-eks/03-kubernetes-fundamentals/deployments-with-yaml/kube-manifests/deployment-definition.yaml

# Delete NodePort Service
$ kubectl delete -f aws-eks/03-kubernetes-fundamentals/deployments-with-yaml/kube-manifests/deployment-nodeport-service.yaml
```