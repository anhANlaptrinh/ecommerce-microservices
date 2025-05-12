terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

resource "aws_instance" "jenkins_master" {
  ami                         = var.ami
  instance_type               = var.instance_type
  key_name                    = var.key_name
  associate_public_ip_address = false

  security_groups = [aws_security_group.allow_all.name]

  tags = {
    Name = "jenkins-master"
  }
}

resource "aws_eip" "jenkins_eip" {
  vpc = true
}

resource "aws_eip_association" "jenkins_eip_assoc" {
  instance_id   = aws_instance.jenkins_master.id
  allocation_id = aws_eip.jenkins_eip.id
}

resource "aws_security_group" "allow_all" {
  name        = "jenkins-master-sg"
  description = "Allow all traffic"

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}