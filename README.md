conflex
=======

Conflex is a small set of tools to help manage Java application configurations in the form of key-value properties.  Configurability increases runtime flexibility, but comes at a price.  The configuration keys must be in sync with those within the codebase and the values must be valid based on the context in which the configuration values are used.  For small projects, the impact is minor.  As projects grow and applications are composed from multiple libraries, it becomes more difficult to manage configuration without encountering runtime errors due to missing key-value pairs, misspelled keys, or unparseable values.  Conflex attacks these issues by simplifying the creation of tools that help manage these configurations.

Goals of conflex include the following :

1. Specification should be [DRY](http://en.wikipedia.org/wiki/Don't_repeat_yourself) to minimize errors when modifying a property specification.
2. Automatically generate documentation and default configuration files.
3. Facilitate validating configuration files and diagnosing potential errors.
4. Minimize boilerplate code (e.g. parsing logic)

## Maven Coordinates

Conflex is available on [maven central](http://search.maven.org/#artifactdetails%7Ccom.jwsphere%7Cconflex%7C0.0.1%7Cjar).  Add the following snippet to the dependencies section of your `pom.xml` file.

```
<dependency>
    <groupId>com.jwsphere</groupId>
    <artifactId>conflex</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Simple Example
The following example shows how application configuration might be achieved with and without conflex.  Both solutions have essentially the same number of lines of code.  The conflex solution provides the benefit that annotation processors can inspect the available configuration and perform a number of useful tasks such as configuration validation, documentation generation, and default configuration file generation.

### Example without conflex
```java
class FooServer {

    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";

    public static final String DEFAULT_HOST = "localhost";
    public static fianl String DEFAULT_PORT = "8080";

    /** The host for the server to bind to. **/
    String host;

    /** The port for the server to bind to. **/
    int port;

    public FooServer(Properties properties) {
        host = properties.get(HOST_KEY, DEFAULT_HOST);
        port = Integer.parseInt(properties.get(PORT_KEY, DEFAULT_PORT));
    };
};
```

### Example with conflex
```java
class FooServer {
    
    public static final Conflex conflex = Conflex.create(FooServer.class);

    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";

    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_PORT = "8080";

    @ConflexProperty(key = HOST_KEY, defaultValue = DEFAULT_HOST,
            description = "The host for the server to bind to.")
    String host;

    @ConflexProperty(key = PORT_KEY, defaultValue = DEFAULT_PORT,
            description = "The port for the server to bind to.")
    int port;

    public FooServer(Properties properties) {
        conflex.inject(this, properties);
    };
};
```

### Generating Default Configuration Files
The main benefit of using the conflex approach is that it becomes simple to write tools that process the annotations to perform work that would be non-trivial with the original example.  The following example shows how to generate a default configuration file from a single class, but this same technique can be combined with classpath scanning to generate a configuration for an entire project along with its dependencies.

```java
class Foo {
    public static void main(String[] args) {
        ConflexGenerator generator = new ConflexGenerator(FooServer.class);
        String content = generator.generatePropertiesFileTemplate();
        System.out.println(content);
    }
}
```

```
# A hostname for the server to bind to.
host=localhost

# The port for the server to bind to.
port=8080
```
