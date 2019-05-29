package entities

import java.util.UUID

object User {
  def apply(createUser: CreateUser): User = new User(
    id = UUID.randomUUID().toString,
    email = createUser.email,
    firstName = createUser.firstName,
    lastName = createUser.lastName
  )

  val tupled = (User.apply(_: String, _: String, _: String, _: String)).tupled
}

case class User(id: String, email: String, firstName: String, lastName: String)
