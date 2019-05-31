package validation

import entities.{ApiError, UpdateUser}

object UpdateUserValidator extends Validator[UpdateUser] with utils {
  override def validate(updateUser: UpdateUser): Option[ApiError] = {
    if (!isValidEmail(updateUser.email)) {
      Some(ApiError.invalidEmail)
    } else None
  }
}
