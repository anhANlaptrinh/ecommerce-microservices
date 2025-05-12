module "jenkins_master" {
  source  = "./modules/jenkins-master"
  ami           = var.ami
  instance_type = var.instance_type
  key_name      = var.key_name
}

module "jenkins_agent" {
  source  = "./modules/jenkins-agent"
  ami           = var.ami
  instance_type = var.instance_type
  key_name      = var.key_name
}
