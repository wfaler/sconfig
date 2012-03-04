package com.recursivity.config

import java.io.{InputStream, FileInputStream}
import java.util.Properties
import java.text.MessageFormat


/**
 * Resolves properties from the classpath based on a environment variable, such as a variable in a .profile file or EXPORT.
 * @param varName  the environment variable used to resolve the properties file
 * @param default default for the above property if no environment variable is found, defaults to None
 * @param configLocation  location of properties file. Defaults to "/config/", but can be changed. Can also be given a filename prefix for specific properties files, for instance "/config/database_" to resolve to "/config/database_prod.properties" given a environment variable value of "prod"
 *
 */
case class EnvironmentProperties(varName: String, default: Option[String] = None, configLocation: String = "/config/") extends Config{
  val props = PropertiesLoader(() => {
    this.getClass.getResourceAsStream(configLocation + {
      if(System.getenv(varName) != null) System.getenv(varName) + ".properties"
      else default.getOrElse("") + ".properties"
    })
  })
  def apply(key: String) = getProperty(key)
}

/**
 * Loads a properties file from the classpath in the location of [Class fully qualified name].properties,
 * traverses parent classes if no properties file exists.
 */
case class ContextProperties(cls: Class[_]) extends Config{
  val props = PropertiesLoader(() => traverseClassInheritance(cls))
  def apply(key: String) = getProperty(key)

  private def traverseClassInheritance(cls: Class[_]): InputStream = {
    val in = this.getClass.getResourceAsStream("/" + cls.getName.replace (".", "/") + ".properties")
    if(in != null && in.available() > 0) in
    else traverseClassInheritance(cls.getSuperclass)
  }
}

/**
 * Resolves properties from the given file system location
 */
case class FilePathProperties(path: String) extends Config{
  val props = PropertiesLoader(() => {new FileInputStream(path)})
  def apply(key: String) = getProperty(key)
}

/**
 * Resolves properties from the classpath based on a system property, such as a -D provided property during JVM startup.
 * @param property  the system property used to resolve the properties file
 * @param default default for the above property if no system property is found, defaults to None
 * @param configLocation  location of properties file. Defaults to "/config/", but can be changed. Can also be given a filename prefix for specific properties files, for instance "/config/database_" to resolve to "/config/database_prod.properties" given a system property value of "prod"
 *
 */
case class SystemProperties(property: String, default: Option[String] = None, configLocation: String = "/config/") extends Config{
  val props = PropertiesLoader(() => this.getClass.getResourceAsStream(configLocation + System.getProperty(property, default.getOrElse("")) + ".properties"))
  def apply(key: String) = getProperty(key)
}

trait Config{
  def props: Properties
  
  def getProperty(key: String): String = {
    if(props.get(key) == null) throw new IllegalArgumentException("no property " + key + "in properties loaded with " + this.getClass.getName + "!")
    else replacePlaceholders(props.get(key).toString)
  }
  
  private def replacePlaceholders(value: String): String = {
    val startIndex = value.indexOf("${") + 2
    val endIndex = value.substring(startIndex).indexOf("}") + startIndex
    if(startIndex > -1 && endIndex > startIndex){
      val key = value.substring(startIndex, endIndex)
      replacePlaceholders(value.replace("${" + key + "}", {
        try{
          getProperty(key)
        }catch{
          case e: IllegalArgumentException => System.getProperty(key, System.getenv(key))
        }  
      }))
    }else value
  }
}