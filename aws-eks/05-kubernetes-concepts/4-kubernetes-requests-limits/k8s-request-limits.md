# Kubernetes - Requests and Limits

## Introduction
- We can specify how much each container a pod needs resources like CPU & Memory.
- When we provide this information in our pod, the scheduler uses this information to decide which node to place the Pod on.
- When you specify a resource limit for a Container, the kubelet enforces those `limits` so that the running container is not allowed to use more of that resource than the limit you set.
- The kubelet also reserves at least the `request` amount of that system resource specifically for that container to use.
