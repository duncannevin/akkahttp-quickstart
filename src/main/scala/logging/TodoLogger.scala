package logging

trait TodoLogger extends Logger {
  def serverListening(port: Int): Unit = logger.info(s"Listening on port: $port")
  def startFailure(port: Int, msg: String): Unit = logger.fatal(s"Failed to start on port: $port because: $msg")
  def failedToSave(reason: String, email: String): Unit = logger.info(s"Failed to add $email because: $reason")
  def createdTable(name: String): Unit = logger.info(s"Created table $name")
  def failedToCreateTable(name: String, reason: String): Unit = logger.info(s"Failed to create table: $name because: $reason")
  def droppedTable(name: String): Unit = logger.info(s"Dropped table $name")
  def failedToDropTable(name: String, reason: String): Unit = logger.info(s"Failed to drop table: $name because: $reason")
}
