package entities

import java.util.UUID

object Todo {
  def apply(createTodo: CreateTodo): Todo = new Todo(UUID.randomUUID().toString, createTodo.title, createTodo.description, false)
}

case class Todo(id: String, title: String, description: String, done: Boolean)

