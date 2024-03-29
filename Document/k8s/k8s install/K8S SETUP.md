# Setup K8S

# ==For Every VM==

## Step 1: Time synchronization
Input [yum install ntp] on all your VM and synchronize the VM time by using [ntpdate] command.  

## Step 2: Name your VMs
Change the name of yours VMs use the following instructions.   
vi /etc/hosts   
10.141.211.162 node-1   
10.141.211.163 master   
10.141.211.164 node-2   

## Step 3: Install Docker
Move the .rpm file to your every VM and use the following instructions to install Docker:   
yum install -y docker-ce-selinux-17.03.2.ce-1.el7.centos.noarch.rpm   
yum install -y docker-ce-17.03.2.ce-1.el7.centos.x86_64.rpm    
systemctl start docker.service     
systemctl enable docker.service

## Step 4: Change cgroup of Docker
//No Longer Needed
Change cgroup of Docker by using the following instructions:    
You may restart the docker after this step.   
cat << EOF > /etc/docker/daemon.json    
{   
  "exec-opts": ["native.cgroupdriver=systemd"]  
}   
EOF   

## Step 5: Install kubeadm
Install kubeadm using the following instructions:   
cat <<EOF > /etc/yum.repos.d/kubernetes.repo  
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
EOF
setenforce 0    
yum install -y kubelet kubeadm kubectl
systemctl enable kubelet && systemctl start kubelet  


cat <<EOF >  /etc/sysctl.d/k8s.conf  
net.bridge.bridge-nf-call-ip6tables = 1  
net.bridge.bridge-nf-call-iptables = 1  
EOF   
sysctl --system  

## Step 6： Add to PATH
## Step 7: Do some settings
## Step 8: Close SWAP
export KUBECONFIG=/etc/kubernetes/admin.conf  
sysctl net.bridge.bridge-nf-call-iptables=1  
sudo swapoff -a  

# ==For Master==

## Step 9: Init kubeadm
yum install -y kubernetes-cni-0.6.0  kubelet-1.14.1 kubeadm-1.14.1 kubectl-1.14.1 --disableexcludes=kubernete
Use the follow instructions to init kubeadm  
kubeadm reset  
kubeadm init --pod-network-cidr=10.244.0.0/16   

kubeadm init --kubernetes-version=v1.15.2   --pod-network-cidr=10.244.0.0/16 
kubeadm join 192.168.193.128:6443 --token 83ln77.5jehuwn2bqwalw91 \
    --discovery-token-ca-cert-hash sha256:2f74e2f48187381b6129a37132b3ac6bd47490b89bf9e30517aaf0fc264dcaa0

## Step 10: Record the instructions
After step 9, you will get an instruction printed on the screen like:   
kubeadm join 10.141.212.23:6443 --token qrxigf.bdqvtgdzyygj1qek --discovery-token-ca-cert-hash      ...................... 
Please write down this instruction, you will use this command to join you node to your cluser.    

## Step 11: Install a network plugin
kubectl apply -f "https://cloud.weave.works/k8s/net?k8s-version=$(kubectl version | base64 | tr -d '\n')"


# ==For Slave/Node==
## Step 12: Join to the cluster
Use the command you write down in Step 10.   

# ==Some command may be helpful==
kubectl get nodes   
kubectl get pods --all-namespaces   
kuebctl get svc --all-namespaces   



/etc/init.d/network restart

echo "1" >/proc/sys/net/ipv4/ip_forward
echo "1" >/proc/sys/net/bridge/bridge-nf-call-iptables

kubeadm join 10.141.212.24:6443 --token ebt1h4.knutq8g9zuqe9uf3 \
    --discovery-token-ca-cert-hash sha256:2ab9f269bfd9e374243748accf2e2c15e5bf32b81129bf018df9de14b61acc20