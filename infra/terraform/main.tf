module "jenkins_master" {
  source         = "./modules/jenkins-master"
  ami            = var.ami
  instance_type  = var.instance_type
  key_name       = var.key_name
}

module "jenkins_agent" {
  source         = "./modules/jenkins-agent"
  ami            = var.ami
  instance_type  = var.instance_type
  key_name       = var.key_name
}

module "k8s_nodes" {
  source        = "./modules/k8s-nodes"
  ami           = var.ami
  instance_type  = var.instance_type
  key_name      = var.key_name
  name_prefix   = "k8s"
}

resource "cloudflare_record" "jenkins_master" {
  zone_id = var.cloudflare_zone_id
  name    = "master"
  type    = "A"
  value   = module.jenkins_master.public_ip
  ttl     = 300
  proxied = false
}

resource "cloudflare_record" "jenkins_agent" {
  zone_id = var.cloudflare_zone_id
  name    = "agent"
  type    = "A"
  value   = module.jenkins_agent.public_ip
  ttl     = 300
  proxied = false
}

