package ammonite.shell

import scala.reflect.runtime.universe._

/*
 * With just a bit more ClassLoader machinery, this should be the only dependency added to
 * the REPL user code
 */

trait ReplAPI {
  /**
   * Exit the Ammonite REPL. You can also use Ctrl-D to exit
   */
  def exit: Nothing

  /**
   * History of commands that have been entered into the shell
   */
  def history: Seq[String]

  /**
   *
   */
  // def reify[T: WeakTypeTag](t: => T): Tree

  /**
   * Tools related to loading external scripts and code into the REPL
   */
  def load: Load

  /**
   *
   */
  implicit def interpreter: ammonite.interpreter.api.Interpreter
}

trait Resolver

object IvyConstructor {
  val scalaBinaryVersion = scala.util.Properties.versionNumberString.split('.').take(2).mkString(".")

  implicit class GroupIdExt(groupId: String){
    def %(artifactId: String) = (groupId, artifactId)
    def %%(artifactId: String) = (groupId, artifactId + "_" + scalaBinaryVersion)
  }
  implicit class ArtifactIdExt(t: (String, String)){
    def %(version: String) = (t._1, t._2, version)
  }

  object Resolvers {
    case object Local extends Resolver
    case object Central extends Resolver
  }
}

trait Load {
  /**
   * Load a `.jar` file
   */
  def jar(jar: java.io.File*): Unit
  /**
   * Load a module from its maven/ivy coordinates
   */
  def module(coordinates: (String, String, String)*): Unit
  /**
   * Load one or several sbt project(s)
   */
  def sbt(path: java.io.File, projects: String*): Unit

  /**
   *
   */
  def resolver(resolver: Resolver*): Unit

  /**
   * Loads a command into the REPL and
   * evaluates them one after another
   */
  def apply(line: String): Unit
}
