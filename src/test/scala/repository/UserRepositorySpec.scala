package repository

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.TestDbConfiguration
import entities.{CreateUser, User}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterEach, Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class UserRepositorySpec extends AsyncFlatSpec with ScalaFutures with Matchers with ScalatestRouteTest with BeforeAndAfterEach with TestDbConfiguration {
  private val timeout = FiniteDuration(500, "milliseconds")
  private val repository = new UserRepository(db)
  repository.init()

  private val user = User(CreateUser("tester@chester.com", "tester", "chester"))
  private var user2 = User(CreateUser("tester2@chester.com", "tester2", "chester2"))
  private val notSavedUser = User(Some(999999), "not@saved.com", "not", "saved")

  override def beforeAll(): Unit = {
    Await.result(repository.drop(), timeout)
    user2 = Await.result(repository.save(user), timeout).get
  }

  behavior of "User repository"

  it should "be instance of Repository[User]" in {
    repository.isInstanceOf[Repository[User]] shouldBe true
  }

  it should "save a user" in {
    val user = User(CreateUser("tester@chester444.com", "tester", "chester"))
    for {
      userOpt <- repository.save(user)
    } yield {
      userOpt.get.email shouldBe user.email
      userOpt.get.firstName shouldBe user.firstName
      userOpt.get.lastName shouldBe user.lastName
    }
  }

  it should "return None if user already saved" in {
    for {
      userOpt <- repository.save(user)
    } yield userOpt shouldBe None
  }

  it should "find a user by id" in {
    for {
      userOpt <- repository.find(user2.id.get)
    } yield {
      userOpt.get.email shouldBe user2.email
      userOpt.get.firstName shouldBe user2.firstName
      userOpt.get.lastName shouldBe user2.lastName
    }
  }

  it should "return None if user is not saved" in {
    for {
      userOpt <- repository.find(notSavedUser.id.get)
    } yield userOpt shouldBe None
  }

  it should "update a user" in {
    for {
      successful <- repository.update(user2.copy(email = "update@user.com"))
    } yield successful shouldBe true
  }

  it should "not update a none existent user" in {
    for {
      successful <- repository.update(notSavedUser)
    } yield successful shouldBe false
  }

  it should "delete a user" in {
    for {
      successful <- repository.delete(user2.id.get)
    } yield successful shouldBe true
  }

  it should "not delete a none existent user" in {
    for {
      successful <- repository.delete(notSavedUser.id.get)
    } yield successful shouldBe false
  }

  it should "not use 'all' method" in {
    val thrown = intercept[Exception] {
      repository.all(9999)
    }
    thrown.getMessage shouldBe "not used"
  }

  it should "not use 'done' method" in {
    val thrown = intercept[Exception] {
      repository.done(9999)
    }
    thrown.getMessage shouldBe "not used"
  }

  it should "not use 'pending' method" in {
    val thrown = intercept[Exception] {
      repository.pending(9999)
    }
    thrown.getMessage shouldBe "not used"
  }
}
