package com.recursivity.config

import java.util.Properties
import java.io.{FileInputStream, InputStream}

/**
 * Created by IntelliJ IDEA.
 * User: wfaler
 * Date: 12/02/2012
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */

object HomeDir {
  def apply() = System.getProperty("user.home")
}

object TmpDir{
  def apply() = System.getProperty("java.io.tmpdir")
}

object FilePath{
  def apply(path: List[String]) = path.mkString(System.getProperty("file.separator"))

  def apply(basePath: String, path: List[String]) = basePath + System.getProperty("file.separator") + path.mkString(System.getProperty("file.separator"))
}

object PropertiesLoader{
  def apply(inputStreamLoader: () => InputStream): Properties = {
    val props = new Properties
    val in = inputStreamLoader()
    props.load(in)
    in.close
    props
  }
}
