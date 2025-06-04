variable "ami" {}
variable "instance_type" {}
variable "key_name" {}

variable "name_prefix" {
  type    = string
  default = "k8s-node"
}

variable "volume_size" {
  type    = number
  default = 40
}
