package entities

final case class TodoNotFound(id: String) extends Exception(s"Todo with id $id not found.")
