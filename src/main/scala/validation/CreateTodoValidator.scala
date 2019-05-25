package validation

import entities.{ApiError, CreateTodo}

object CreateTodoValidator extends Validator[CreateTodo] {
  def validate(createTodo: CreateTodo): Option[ApiError] = {
    if (createTodo.title.isEmpty) Some(ApiError.emptyTitleField)
    else None
  }
}
