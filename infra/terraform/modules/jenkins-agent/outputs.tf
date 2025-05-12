output "jenkins_agent_ip" {
  value = aws_eip.jenkins_eip.public_ip
}