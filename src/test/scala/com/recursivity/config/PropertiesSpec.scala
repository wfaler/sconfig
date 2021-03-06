package com.recursivity.config

import org.specs2.Specification

/**
 * Created by IntelliJ IDEA.
 * User: wfaler
 * Date: 12/02/2012
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */

class PropertiesSpec extends Specification{  def is =
  "The Properties Spec" ^
  p^
    "The FilePathProperties should" ^
      "Find a file with a given name that exists" ! existingFileProperties^
      "Throw an Exception if the file does not exist" ! fileNotFound^
                                                endp^
    "The ClasspathProperties should" ^
      "Find the properties on the classpath"   ! classpathProperties^
                                                endp^
    "The PropertyGroupProperties should" ^
      "Replace the placeholder with the correct replacement"   ! propertyGroup^
                                                endp^
    "The SystemProperties should" ^
      "Resolve the right property" ! envResolve^
      "Fallback on the given default properties" ! defaultProps^
      "Throw an IllegalArgumentException if trying to get a non-existent property" ! nonExistentProp^
      "Throw an Exception if no properties are found" ! systemException^
                                                endp^
    "The ContextProperties should" ^
      "Resolve the right property for a concrete class with properties" ! propertiesForClass^
      "Traverse the classpath inheritance upwards to resolve properties if none exist for the given class" ! propertiesForParentClass^
      "Throw an Exception if no properties are found" ! noPropertiesForAnyClass^
                                                endp^
    "Placeholder properties should" ^
      "Be replaced properly if multiple exist in a property value" ! placeholderReplacement^
      "Fallback to system properties and environment if not present in property file" ! systemPlaceholder^
      end


  def placeholderReplacement = {
    System.setProperty("sconfig", "default")
    (SystemProperties("sconfig")("replacer") must be_==("Howdy how are the kids?"))
  }

  def systemPlaceholder = {
    System.setProperty("sconfig", "default")
    System.setProperty("myHost", "http://localhost")
    (SystemProperties("sconfig")("replace2") must be_==("http://localhost/someUri"))
  }

  def existingFileProperties = {
    FilePathProperties(FilePath("src" :: "test" :: "resources" :: "config" :: "default.properties" :: Nil))("myProp") must be_==("default")
  }

  def fileNotFound = {
    FilePathProperties(FilePath("src" :: "test" :: "resources" :: "config" :: "blabla.properties" :: Nil)) must throwAn[Exception]
  }

  def classpathProperties = {
    ClasspathProperties("/config/default.properties")("replacer") must be_==("Howdy how are the kids?")
  }

  def propertyGroup = {
    PropertyGroupProperties(ClasspathProperties("/config/default.properties") :: ClasspathProperties("/config/prod.properties") :: Nil)("replacer") must be_==("Hiya how are the kids?")
  }

  def envResolve = {
    System.setProperty("sconfig", "default")
    System.setProperty("sconfigprod", "prod")
    (SystemProperties("sconfig")("myProp") must be_==("default")) and (SystemProperties("sconfigprod")("myProp") must be_==("production"))
  }

  def defaultProps = {
    SystemProperties("sdsfd", Some("default"))("myProp") must be_==("default")
  }

  def nonExistentProp = {
    SystemProperties("sdsfd", Some("default"))("nonExistentProp") must throwAn[IllegalArgumentException]
  }

  def systemException = {
    SystemProperties("sdsfd") must throwAn[Exception]
  }

  def propertiesForClass = {
    ContextProperties(classOf[ChildWithProperties])("context") must be_==("childWithProperties")
  }

  def propertiesForParentClass = {
    ContextProperties(classOf[Child])("context") must be_==("parent")
  }

  def noPropertiesForAnyClass = {
    ContextProperties(classOf[PropertiesSpec]) must throwAn[Exception]
  }

}

class Parent

class Child extends Parent

class ChildWithProperties extends Parent