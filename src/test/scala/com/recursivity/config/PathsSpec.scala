package com.recursivity.config

import org.specs2.Specification

/**
 * Created by IntelliJ IDEA.
 * User: wfaler
 * Date: 12/02/2012
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */

class PathsSpec extends Specification{ def is =

  "A FilePath should" ^
  p^
    "Concatenate a List as expected" ! concatenateList^
    "Concatenate a list with a dir as expetect" ! concatWithDir^
                                                end


  def concatenateList = {
    FilePath("hello" :: "world" :: Nil) must be_==("hello/world")
  }

  def concatWithDir = {
    FilePath("/hello/world","hello" :: "world" :: Nil) must be_==("/hello/world/hello/world")
  }

}