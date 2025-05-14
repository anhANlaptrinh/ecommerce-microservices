output "jenkins_master_dns" {
  value = "master.${var.domain_name}"
}

output "jenkins_agent_dns" {
  value = "agent.${var.domain_name}"
}
