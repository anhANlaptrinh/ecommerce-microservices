output "master_dns" {
  value = "master.${var.domain_name}"
}

output "agent_dns" {
  value = "agent.${var.domain_name}"
}