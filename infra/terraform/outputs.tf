output "jenkins_master_ip" {
  value = module.jenkins_master.jenkins_master_ip
}

output "jenkins_agent_ip" {
  value = module.jenkins_agent.jenkins_agent_ip
}