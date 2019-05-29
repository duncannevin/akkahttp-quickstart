package entities

import java.util.UUID

object Todo {
  def apply(userId: String, createTodo: CreateTodo): Todo = new Todo(
    UUID.randomUUID().toString,
    userId,
    createTodo.title,
    createTodo.description,
    done = false
  )

  def apply(userId: String, id: String, updateTodo: UpdateTodo): Todo = new Todo(
    id,
    userId,
    updateTodo.title,
    updateTodo.description,
    updateTodo.done
  )

  val tupled = (Todo.apply(_: String, _: String, _: String, _: String, _: Boolean)).tupled
}

case class Todo(id: String, userId: String, title: String, description: String, done: Boolean)
