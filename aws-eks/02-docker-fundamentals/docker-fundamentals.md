# Docker Fundamentals

### Troubleshooting Docker Desktop issues on MAC
- Docker Desktop on mac has an issue with MAC **osxkeychain**
- To fix it perform the below steps.
- **Pre-requisite:** Refer this [link](https://medium.com/@dakshika/error-creating-the-docker-image-on-macos-wso2-enterprise-integrator-tooling-dfb5b537b44e) for additional understanding 

#### **Step-1:** Docker Desktop changes
- Open Docker Desktop --> Preferences
- Uncheck the option named **Securely store Docker logins in macOS keychain**

####  **Step-2:** Go to **config.json** file in **.docker** folder
- Sample Reference Location:
    - /Users/**userid**/.docker/config.json
    - ~/.docker/config.json
- Remove the line **“credSstore” : “osxkeychain”,** in config.json

> Note: This issue is subjected to MAC OS version and Docker Desktop version. Check before applying the fix.


### Verify Docker version and also login to Docker Hub
```bash
$ docker --version
$ docker login
```

## List Running Containers
```bash
$ docker ps
$ docker ps -a
$ docker ps -a -q
```

### Connect to Container Terminal
```bash
$ docker exec -it <container-name> /bin/sh
```

### Docker container Stop, Start
```bash
$ docker stop <container-name>
$ docker start <container-name>
```

### Remove Container
```bash
$ docker stop <container-name> 
$ docker rm <container-name>
```

### Remove Image
```bash
$ docker images
$ docker rmi <image-id>
```

## Docker - Essential Commands
- The below are the list of essential commands we are in need

| Commands                                 | Description                                                     |
|------------------------------------------|-----------------------------------------------------------------|
| $ docker ps                              | List all running containers                                     |
| $ docker ps -a                           | List all containers stopped, running containers                 |
| $ docker stop container-id               | Stop the container which is running                             |
| $ docker start container-id              | Start the container which is stopped                            |
| $ docker restart container-id            | Restart the container which is running                          |
| $ docker port container-id               | List port mappings of a specific container                      |
| $ docker rm container-id or name         | Remove the stopped container                                    |
| $ docker rm -f container-id or name      | Remove the running container forcefully                         |
| $ docker pull hello-world                | Pull the image from docker hub repository                       |
| $ docker exec -it container-name /bin/sh | Connect to linux container and execute commands in container    |
| $ docker rmi image-id                    | Remove the docker image                                         |
| $ docker logout                          | Logout from docker hub                                          |
| $ docker login -u username -p password   | Login to docker hub                                             |
| $ docker stats                           | Display a live stream of container(s) resource usage statistics |
| $ docker top container-id or name        | Display the running processes of a container                    |
| $ docker version                         | Show the Docker version information                             |