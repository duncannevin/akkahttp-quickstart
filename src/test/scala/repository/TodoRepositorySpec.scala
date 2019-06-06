package repository

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.TestDbConfiguration
import entities.{CreateTodo, CreateUser, Todo, User}
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class TodoRepositorySpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest with TestDbConfiguration {
  private val timeout = FiniteDuration(500, "milliseconds")
  private val userRepository = new UserRepository(db)
  userRepository.init()
  private val todoRepository = new TodoRepository(db)
  todoRepository.init()

  private var user = User(CreateUser("tester@chester.com", "tester", "chester"))
  private var todo = Todo(999, CreateTodo("test em", "till they break!"))
  private val todo2 = Todo(999, CreateTodo("another todo", "for user anywhere"))
  private var todo3 = Todo(999, CreateTodo("another todo 3", "for user anywhere 3"))
  private val notSavedTodo = Todo(Some(111), 999, "not", "saved todo", done = false)

  override def beforeAll(): Unit = {
    Await.result(userRepository.drop(), timeout)
    Await.result(todoRepository.drop(), timeout)
    user = Await.result(userRepository.save(user), timeout).get
    todo = Await.result(todoRepository.save(todo.copy(userId = user.id.get)), timeout).get
    todo3 = Await.result(todoRepository.save(todo3.copy(userId = user.id.get)), timeout).get
    Await.result(todoRepository.update(todo.copy(done = true)), timeout)
  }

  behavior of "Todo repository"

  it should "save a todo" in {
    val todo = Todo(999, CreateTodo("test em yo yo", "ok doky till they break!"))
    for {
      todoOpt <- todoRepository.save(todo.copy(userId = user.id.get))
    } yield {
      todoOpt.get.title shouldBe todo.title
      todoOpt.get.description shouldBe todo.description
    }
  }

  it should "not save a todo with invalid userId" in {
    for {
      todoOpt <- todoRepository.save(todo2)
    } yield todoOpt shouldBe None
  }

  it should "find a todo" in {
    for {
      todoOpt <- todoRepository.find(todo.id.get)
    } yield {
      todoOpt.get.userId shouldBe user.id.get
      todoOpt.get.title shouldBe todo.title
      todoOpt.get.description shouldBe todo.description
    }
  }

  it should "not find a non existent todo" in {
    for {
      todoOpt <- todoRepository.find(notSavedTodo.id.get)
    } yield todoOpt shouldBe None
  }

  it should "get all todos" in {
    for {
      todos <- todoRepository.all(user.id.get)
    } yield {
      todos.isInstanceOf[Seq[Todo]] shouldBe true
      todos.exists(_.id == todo.id) shouldBe true
    }
  }

  it should "get all done todos" in {
    for {
      doneTodos <- todoRepository.done(user.id.get)
    } yield {
      doneTodos.isInstanceOf[Seq[Todo]] shouldBe true
      doneTodos.forall(_.done == true) shouldBe true
    }
  }

  it should "get all pending todos" in {
    for {
      doneTodos <- todoRepository.pending(user.id.get)
    } yield {
      doneTodos.isInstanceOf[Seq[Todo]] shouldBe true
      doneTodos.forall(_.done == false) shouldBe true
    }
  }

  it should "update a todo" in {
    for {
      success <- todoRepository.update(todo3.copy(title = "it is changed"))
    } yield success shouldBe true
  }

  it should "not update a todo that does not exist" in {
    for {
      success <- todoRepository.update(todo3.copy(id = Some(999)))
    } yield success shouldBe false
  }

  it should "delete a todo" in {
    for {
      success <- todoRepository.delete(todo3.id.get)
    } yield success shouldBe true
  }

  it should "not delete a todo that does not exist" in {
    for {
      success <- todoRepository.delete(notSavedTodo.id.get)
    } yield success shouldBe false
  }
}
