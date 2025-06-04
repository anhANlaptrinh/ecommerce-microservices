resource "aws_security_group" "allow_all" {
  name        = "k8s-allow-all"
  description = "Allow all inbound and outbound traffic"

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

resource "aws_instance" "this" {
  count                         = 3
  ami                           = var.ami
  instance_type                 = var.instance_type
  key_name                      = var.key_name
  associate_public_ip_address   = true
  security_groups               = [aws_security_group.allow_all.name]

  root_block_device {
    volume_size = var.volume_size
  }

  tags = {
    Name = "${var.name_prefix}-${count.index}"
    Role = element(["master", "worker", "worker"], count.index)
  }
}
