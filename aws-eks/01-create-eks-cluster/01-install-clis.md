# Install CLI's of AWS, KUBECTL & EKSCTL:

## Introduction
- Install AWS CLI
- Install kubectl CLI
- Install eksctl CLI

## Step-01: Install AWS CLI
- Reference-1: https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html
- Reference-2: https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html

### MAC - Install and configure AWS CLI
> Download and install the binary via command line using below two commands.

#### Download the Binary
```bash
$ curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"
```
#### Install the binary
```bash
$ sudo installer -pkg ./AWSCLIV2.pkg -target /
```
#### Verify the installation
```bash
$ aws --version
Response: aws-cli/2.17.0 Python/3.11.8 Darwin/23.5.0 exe/x86_64
```
```bash
$ which aws
Response: /usr/local/bin/aws
```

### Configure AWS Command Line using Security Credentials
- Go to AWS Management Console --> Services --> IAM
- Select the **IAM User**: **aws-k8s-user**
- **Important Note:** Use only IAM user to generate **Security Credentials**. Never ever use Root User. (Highly not recommended)
- Click on **Security credentials** tab
- Click on **Create access key**
- Copy **Access ID** and **Secret access key**
- Go to command line and provide the required details

```bash
$ aws configure
AWS Access Key ID [None]: ABCDEFGHIAZBERTUCNGG  (Replace your creds when prompted)
AWS Secret Access Key [None]: uMe7fumK1IdDB094q2sGFhM5Bqt3HQRw3IHZzBDTm  (Replace your creds when prompted)
Default region name [None]: us-east-1
Default output format [None]: json
```

- Test if AWS CLI is working after configuring the above
```bash
$ aws ec2 describe-vpcs
```


## Step-02: Install kubectl CLI
- **IMPORTANT NOTE:**  For `kubectl` binaries for EKS, please prefer to use from Amazon (**Amazon EKS-vended kubectl binary**)
- This will help us to get the exact Kubectl client version based on our EKS Cluster version. You can use the below documentation link to download the binary.
- Reference: https://docs.aws.amazon.com/eks/latest/userguide/install-kubectl.html

### MAC - Install and configure kubectl CLI
- Kubectl version we are using here is 1.29.3 (It may vary based on Cluster version you are planning use in AWS EKS).
- The binary is identical to the upstream community versions. The binary is not unique to Amazon EKS or AWS.

```bash 
$ cd sashankasamantray
$ mkdir kubectlbinary
$ cd kubectlbinary
$ curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.30.0/2024-05-12/bin/darwin/amd64/kubectl
$ curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.30.0/2024-05-12/bin/darwin/amd64/kubectl.sha256
$ ls
```
#### Verify the downloaded binary with the SHA-256 checksum for your binary
```bash
$ openssl sha1 -sha256 kubectl
```
#### Apply execute permissions to the binary
```bash
$ chmod +x ./kubectl
```

#### Copy the binary to a folder in your PATH. If you have already installed a version of kubectl, then we recommend creating a $HOME/bin/kubectl and ensuring that $HOME/bin comes first in your $PATH.
#### Add the $HOME/bin path to your shell initialization file so that it is configured when you open a shell.
```bash
$ mkdir -p $HOME/bin && cp ./kubectl $HOME/bin/kubectl && export PATH=$PATH:$HOME/bin
$ echo 'export PATH=$PATH:$HOME/bin' >> ~/.bash_profile
```

#### Verify the kubectl installation
```bash
$ kubectl version --client
```

> Note: If the above steps to install `kubectl` does not work, and `$ kubectl version --client` points to the wrong kubectl version, then move the kubectl executable from the `$HOME/bin` directory to `/usr/local/bin/kubectl` directory.
```bash
$ sudo mv ./kubectl /usr/local/bin/kubectl
```
> Reference: https://kubernetes.io/docs/tasks/tools/install-kubectl-macos/#install-with-homebrew-on-macos
> `$ kubectl version --client` should now point to the AWS EKS `kubectl` version.
> `$HOME/bin/kubectl` contains the `kubectl` executable downloaded for AWS EKS Cluster.
> `/usr/local/bin/kubectl` contains the `kubectl` executable downloaded for local k8s cluster.
> To update the kubectl version for mac use `$ brew install kubectl`. This should update the kubectl version in `/usr/local/bin/kubectl` directory for the local k8s cluster.


## Step-03: Install eksctl CLI

#### Install Homebrew if you don't already have installed on MacOS
```bash
$ /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
```

#### Install the Weaveworks Homebrew tap
```bash
$ brew tap weaveworks/tap
```

#### Install or Upgrade the Weaveworks Homebrew tap.
```bash
# install eksctl
$ brew install weaveworks/tap/eksctl

# upgrade eksctl if it is already installed
$ brew upgrade eksctl & brew link --overwrite eksctl
```

#### Verify eksctl version
```bash
$ eksctl version
```

References: https://docs.aws.amazon.com/emr/latest/EMR-on-EKS-DevelopmentGuide/setting-up-eksctl.html