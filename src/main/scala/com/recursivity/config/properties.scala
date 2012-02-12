package com.recursivity.config

import java.io.{InputStream, FileInputStream}
import java.util.Properties


/**
 * Created by IntelliJ IDEA.
 * User: wfaler
 * Date: 12/02/2012
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */

case class EnvironmentProperties(varName: String, default: Option[String] = None, configDir: String = "/config/") extends Config{
  val props = PropertiesLoader(() => {
    this.getClass.getResourceAsStream(configDir + {
      if(System.getenv(varName) != null) System.getenv(varName) + ".properties"
      else default.getOrElse("") + ".properties"
    })
  })
  def apply(key: String) = getProperty(key)
}

case class ContextProperties(cls: Class[_]) extends Config{
  val props = PropertiesLoader(() => traverseClassInheritance(cls))
  def apply(key: String) = getProperty(key)

  private def traverseClassInheritance(cls: Class[_]): InputStream = {
    val in = this.getClass.getResourceAsStream("/" + cls.getName.replace (".", "/") + ".properties")
    if(in != null && in.available() > 0) in
    else traverseClassInheritance(cls.getSuperclass)
  }
}

case class FilePathProperties(path: String) extends Config{
  val props = PropertiesLoader(() => {new FileInputStream(path)})
  def apply(key: String) = getProperty(key)
}

case class SystemProperties(property: String, default: Option[String] = None, configDir: String = "/config/") extends Config{
  val props = PropertiesLoader(() => this.getClass.getResourceAsStream(configDir + System.getProperty(property, default.getOrElse("")) + ".properties"))
  def apply(key: String) = getProperty(key)
}

trait Config{
  def props: Properties
  
  def getProperty(key: String): String = {
    if(props.getProperty(key) == null) throw new IllegalArgumentException("no property " + key + "in properties loaded with " + this.getClass.getName + "!")
    else props.getProperty(key)
  }
}