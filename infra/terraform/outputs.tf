output "jenkins_master_dns" {
  value = "master.${var.domain_name}"
}

output "jenkins_agent_dns" {
  value = "agent.${var.domain_name}"
}

output "argocd_dns" {
  value = "argocd.${var.domain_name}"
}

output "api_gateway_dns" {
  value = "api-gateway.${var.domain_name}"
}

output "frontend_dns" {
  value = "frontend.${var.domain_name}"
}