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

module "dns" {
  source        = "./modules/route53"
  domain_name   = var.domain_name
  master_ip     = module.jenkins_master.public_ip
  agent_ip      = module.jenkins_agent.public_ip
}