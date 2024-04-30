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
Response: aws-cli/2.15.42 Python/3.11.8 Darwin/23.4.0 exe/x86_64 prompt/off
```
```bash
$ which aws
```

### Configure AWS Command Line using Security Credentials
- Go to AWS Management Console --> Services --> IAM
- Select the IAM User: aws-k8s-user
- **Important Note:** Use only IAM user to generate **Security Credentials**. Never ever use Root User. (Highly not recommended)
- Click on **Security credentials** tab
- Click on **Create access key**
- Copy Access ID and Secret access key
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