package validation

import entities.{ApiError, UpdateTodo}

object UpdateTodoValidator extends Validator[UpdateTodo] {
  override def validate(updateTodo: UpdateTodo): Option[ApiError] = {
    if (updateTodo.title.nonEmpty && updateTodo.description.nonEmpty && updateTodo.done.isInstanceOf[Boolean]) {
      Some(ApiError.emptyTitleField)
    } else None
  }
}
