package entities

object User {
  def apply(createUser: CreateUser): User = new User(
    id = None,
    email = createUser.email,
    firstName = createUser.firstName,
    lastName = createUser.lastName
  )

  def apply(id: Int, updateUser: UpdateUser): User = new User(
    Some(id),
    email = updateUser.email,
    firstName = updateUser.firstName,
    lastName = updateUser.lastName
  )

  val tupled = (User.apply(_: Option[Int], _: String, _: String, _: String)).tupled
}

case class User(id: Option[Int], email: String, firstName: String, lastName: String)
