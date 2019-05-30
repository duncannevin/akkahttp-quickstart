package validation

import entities.{ApiError, CreateUser, UpdateUser}

object CreateUserValidator extends Validator[CreateUser] {
  def isValidEmail(email: String): Boolean =
    """(\w+)@([\w\.]+)""".r.unapplySeq(email).isDefined

  override def validate(createUser: CreateUser): Option[ApiError] = {
    if (!isValidEmail(createUser.email)) {
      Some(ApiError.invalidEmail)
    } else None
  }
}
