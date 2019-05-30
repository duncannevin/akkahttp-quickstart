package entities

object Todo {
  def apply(userId: Int, createTodo: CreateTodo): Todo = new Todo(
    None,
    userId,
    createTodo.title,
    createTodo.description,
    done = false
  )

  def apply(userId: Int, id: Int, updateTodo: UpdateTodo): Todo = new Todo(
    id = Some(id),
    userId,
    updateTodo.title,
    updateTodo.description,
    updateTodo.done
  )

  val tupled = (Todo.apply(_: Option[Int], _: Int, _: String, _: String, _: Boolean)).tupled
}

case class Todo(id: Option[Int], userId: Int, title: String, description: String, done: Boolean)
