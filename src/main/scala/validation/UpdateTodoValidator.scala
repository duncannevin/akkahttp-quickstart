package validation

import entities.{ApiError, UpdateTodo}

object UpdateTodoValidator extends Validator[UpdateTodo] {
  override def validate(updateTodo: UpdateTodo): Option[ApiError] = {
    if (updateTodo.title.exists(_.isEmpty)) {
      Some(ApiError.emptyTitleField)
    } else None
  }
}

