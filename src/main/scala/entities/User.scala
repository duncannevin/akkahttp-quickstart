package entities

object User {
  def apply(createUser: CreateUser): User = new User(
    id = None,
    email = createUser.email,
    firstName = createUser.firstName,
    lastName = createUser.lastName
  )

  val tupled = (User.apply(_: Option[Int], _: String, _: String, _: String)).tupled
}

case class User(id: Option[Int], email: String, firstName: String, lastName: String)
