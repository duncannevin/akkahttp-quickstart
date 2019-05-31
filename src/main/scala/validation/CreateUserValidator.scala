package validation

import entities.{ApiError, CreateUser, UpdateUser}

object CreateUserValidator extends Validator[CreateUser] with utils {
  override def validate(createUser: CreateUser): Option[ApiError] = {
    if (!isValidEmail(createUser.email)) {
      Some(ApiError.invalidEmail)
    } else None
  }
}
