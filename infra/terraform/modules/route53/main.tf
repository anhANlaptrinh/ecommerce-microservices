resource "aws_route53_zone" "main" {
  name = var.domain_name
}

resource "aws_route53_record" "jenkins_master" {
  zone_id = aws_route53_zone.main.zone_id
  name    = "master.${var.domain_name}"
  type    = "A"
  ttl     = 300
  records = [var.master_ip]
}

resource "aws_route53_record" "jenkins_agent" {
  zone_id = aws_route53_zone.main.zone_id
  name    = "agent.${var.domain_name}"
  type    = "A"
  ttl     = 300
  records = [var.agent_ip]
}