output "jenkins_master_dns" {
  value = module.dns.master_dns
}

output "jenkins_agent_dns" {
  value = module.dns.agent_dns
}
