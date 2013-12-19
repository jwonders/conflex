conflex
=======

Conflex is a small set of tools to help with managing and injecting configuration properties for Java.  Its development is motivated by the observation that the implicit coupling between the key-value pairs in configuration files and the strings used as keys and default values in Java code leads to configuration management difficulties as code bases grow.

Since the coupling is implicit, the compiler cannot help us.  There is a need for tools that support operations such as generating configuration file templates, checking configuration files for extra and missing key-value pairs, misspelled keys, and unparseable values.

The primary goal of conflex is to make it easy to write tools to simplify mangement of configuration properties.  A secondary goal is to reduce the boilerplate for parsing, transforming, and validating values.

### Typical but Contrived Example
```java
class FooServer {

    /** A hostname for the server to bind to. **/
    String host;

    /** The port for the server to bind to. **/
    int port;

    public FooServer(Properties properties) {
        host = properties.get("host", "localhost");
        port = Integer.parseInt(properties.get("port", "8080"));
    }; 
};
```

```
host=www.example.com
port=8180
```

### Contrived Example with Conflex
```java
class FooServer {
    
    public static final Conflex conflex = new Conflex(FooServer.class);

    @ConflexProperty(key = "host", defaultValue = "localhost",
                     description = "A hostname for the server to bind to.")
    String host;

    @ConflexProperty(key = "port", defaultValue = "8080",
                     description = "The port for the server to bind to.")
    int port;

    public FooServer(Properties properties) {
        conflex.inject(this, properties);
    }; 
};
```

```
host=www.example.com
port=8180
```

### Generating Configuration Templates
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