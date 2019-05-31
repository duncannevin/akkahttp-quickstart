package repository

import db.TestDbConfiguration

trait MockRepositories extends TestDbConfiguration {
  val userRepository = new UserRepository(db)
  userRepository.init()
  val todoRepository = new TodoRepository(db)
  todoRepository.init()
}
