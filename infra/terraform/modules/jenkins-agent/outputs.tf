output "public_ip" {
  value = aws_instance.jenkins_agent.public_ip
}
