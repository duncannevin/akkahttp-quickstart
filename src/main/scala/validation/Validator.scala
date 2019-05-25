package validation

import entities.ApiError

trait Validator[T] {
  def validate(t: T): Option[ApiError]
}
