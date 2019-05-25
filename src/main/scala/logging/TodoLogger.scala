package logging

trait TodoLogger extends Logger {
  def serverListening(port: Int): Unit = logger.info(s"Listening on port: $port")
  def startFailure(port: Int, msg: String): Unit = logger.fatal(s"Failed to start on port: $port because: $msg")
}
