package repository

import entities.{CreateTodo, Todo, TodoDeleted, TodoNotFound, UpdateTodo}

import scala.concurrent.{ExecutionContext, Future}

class InMemoryTodoRepository(initialTodos: Seq[Todo] = Seq(
  Todo("1", "Buy eggs", "Ran out of eggs, buy a dozen", done = false),
  Todo("2", "Buy milk", "The cat is thirsty", done = true)
))(implicit ec: ExecutionContext) extends Repository {

  private var todos: Vector[Todo] = initialTodos.toVector

  override def all(): Future[Seq[Todo]] = Future.successful(todos)

  override def done(): Future[Seq[Todo]] = Future.successful(todos.filter(_.done))

  override def pending(): Future[Seq[Todo]] = Future.successful(todos.filterNot(_.done))

  override def save(createTodo: CreateTodo): Future[Todo] = Future.successful {
    val todo = Todo(createTodo)
    todos = todos :+ todo
    todo
  }

  override def update(id: String, updateTodo: UpdateTodo): Future[Todo] = {
    todos.find(_.id == id) match {
      case Some(foundTodo) =>
        val newTodo = updateHelper(foundTodo, updateTodo)
        todos = todos.map(t => if (t.id == id) newTodo else t)
        Future.successful(newTodo)
      case None => Future.failed(TodoNotFound(id))
    }
  }

  override def delete(id: String): Future[TodoDeleted] =
    todos.find(_.id == id) match {
      case Some(foundTodo) =>
        todos = todos.filterNot(_.id == foundTodo.id)
        Future.successful(TodoDeleted(foundTodo.id))
      case None => Future.failed(TodoNotFound(id))
    }


  private def updateHelper(todo: Todo, updateTodo: UpdateTodo): Todo = {
    val nTitle = updateTodo.title.getOrElse(todo.title)
    val nDescription = updateTodo.description.getOrElse(todo.description)
    val nDone = updateTodo.done.getOrElse(todo.done)
    todo.copy(title = nTitle, description = nDescription, done = nDone)
  }
}

