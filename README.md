# JsonConfig

JsonConfig is a lightweight Java library for reading and editing JSON configuration files.

Most JSON libraries (like Gson or Jackson) force you to map your JSON data directly to Java objects. This works well for APIs, but it makes handling dynamic configuration files difficult. JsonConfig works differently: it treats JSON like a file system. You can read, write, and modify values using simple paths like `server.database.port` or `users[0].name`, without creating a single new class.

## Features

*   **Zero Dependencies**: The core module contains its own strict JSON parser. You don't need to shade or bundle any other libraries.
*   **Path Access**: Access deeply nested values using dot notation. Arrays are supported using standard bracket syntax (`[index]`).
*   **Thread Safety**: If you need to access configs from multiple threads, use the provided `ConcurrentJsonConfiguration`. It uses ReadWriteLocks to ensure safe reads and writes.
*   **Config Merging**: Easily update user configurations by merging them with bundled defaults. Strategies include:
    *   `OVERWRITE`: Replace values completely.
    *   `DEEP_MERGE_OBJECTS`: recurses into objects to merge new keys while preserving existing ones.
    *   `CONCAT_ARRAYS`: Appends new array elements to existing lists.
*   **Strict Compliance**: The parser adheres strictly to the JSON standard, ensuring files are always valid JSON.
*   **Optional Integrations**: If you already use Gson or Jackson in your project, optional modules allow seamless conversion between their types and JsonConfig types.


## Installation

This library is available via [JitPack](https://jitpack.io/#frosxt/JsonConfig).

### Gradle (Groovy)
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.frosxt:JsonConfig:v1.0.1'
}
```

### Gradle (Kotlin)
```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.frosxt:JsonConfig:v1.0.1")
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.frosxt</groupId>
    <artifactId>JsonConfig</artifactId>
    <version>v1.0.1</version>
</dependency>
```

### Individual Modules
If you only want specific modules (e.g., just the core library vs the full repo):

**Gradle:**
```kotlin
implementation("com.github.frosxt.JsonConfig:jsonconfig-core:v1.0.1")
implementation("com.github.frosxt.JsonConfig:jsonconfig-gson:v1.0.1")
```

**Maven:**
```xml
<dependency>
    <groupId>com.github.frosxt.JsonConfig</groupId>
    <artifactId>jsonconfig-core</artifactId>
    <version>v1.0.1</version>
</dependency>
```

## Modules

*   `jsonconfig-core`: Main library (Parser, Path Logic, API).
*   `jsonconfig-gson`: Adapters for Google Gson.
*   `jsonconfig-jackson`: Adapters for Jackson Databind.

## Requirements

*   Java 21 or newer
*   Gradle or Maven

## Documentation

Documentation is available on the [Wiki](https://github.com/frosxt/JsonConfig/wiki)

## License

MIT License. See [LICENSE](LICENSE).
