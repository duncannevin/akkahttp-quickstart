package validation

trait utils {
  def isValidEmail(email: String): Boolean =
    """(\w+)@([\w\.]+)""".r.unapplySeq(email).isDefined
}
