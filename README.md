#SConfig - Simple configuration resolution for Scala
People tend to write boilerplate code to deal with configuration of applications for different environments (dev, staging, production etc) - SConfig is a _very_ simple framework for removing the boilerplate and allow you to select properties files based on environment variables, system properties or file system locations.

## Basic Usage

	import com.recursivity.config._
	
	// Get a property based on a System property
	// looks for system property called "mySystemProperty",
	// looks for property file in classpath location /config/[propertyValue].properties
	val properties = SystemProperties("mySystemProperty") 
	val myPropertyValue = properties("somePropertyKey")
	
	// Get a property based on environment variable, such as those set in a .profile
	// otherwise same as above.
	val properties = EnvironmentProperties("myEnvironmentVariable") 
	val myPropertyValue = properties("somePropertyKey")
	
	// Get a property based on a file-system location
	// gets properties file from ${USER.HOME}/propertyDirectory/myproperties.properties
	val properties = FilePathProperties(FilePath(HomeDir(), "propertyDirectory" :: "myproperties.properties" :: Nil))
	val myPropertyValue = properties("somePropertyKey")
	
If you look at the constructors of the various Property classes, you can customize the defaults based on personal preference, but we provide a few sensible default values through Scalas default arguments.

## Property placeholders
Property placeholders are supported in your property files, for instance:
	
	myProperty=${host}/someUri
	
In this case, ${host} would be replaced in order of preference by a property named host in the same property file, OR a System property OR a environment property if no system property or normal property exist.
	
## Roadmap
There is no roadmap in particular: SConfig will not be loaded with lots of features, as the basics are easy enough to wrap in your own code. It only supports properties files because, well, XML configs etc are just a mess. SConfig does very little and doesn't intend to do much more in the future.

