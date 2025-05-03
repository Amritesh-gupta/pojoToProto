# POJO to Proto

![Build](https://github.com/Amritesh-gupta/pojoToProto/.github/workflows/build.yml/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

<!-- Plugin description -->
A simple IntelliJ IDEA plugin for converting Java POJOs to Protocol Buffer (protobuf) definitions.

* Supports conversion of Java classes to Protocol Buffer messages
* Handles nested objects and collections
* Supports both proto2 and proto3 syntax
* Configurable settings for customizing the output
<!-- Plugin description end -->

## Features

* Convert Java classes to Protocol Buffer definitions
* Support for nested classes and complex types
* Configurable settings for customization
* Generate file with Protocol Buffer definitions

## Usage

* Open a Java class file > Move cursor to Class/Variable/Parameter > Right click > Copy Proto > Protocol Buffer definition will be copied to clipboard
* Open a Java class file > Move cursor to Class/Variable/Parameter > Alt + Insert > Copy Proto > Protocol Buffer definition will be copied to clipboard
* Project view select a Java class file > Right click > Copy Proto > Protocol Buffer definition will be copied to clipboard

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "PojoToProto"</kbd> >
  <kbd>Install</kbd>

- Manually:

  Download the [latest release](https://github.com/Amritesh-gupta/pojoToProto/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Configuration

* IntelliJ IDEA > File > Settings > Tools > POJO To Proto
  * Use nested messages for nested classes - Whether to generate nested messages or separate messages for nested classes
  * Generate optional fields - Whether to add the 'optional' keyword to fields in proto3 syntax
  * Use Java package name as proto package - Whether to use the Java package name as the proto package name
  * Custom Proto Package - Custom package name to use if not using Java package name
  * Proto Syntax - Choose between proto2 and proto3 syntax

## Example

Java POJO:
```java
package com.example;

public class User {
    private String username;
    private String email;
    private int age;
    private Address address;

    public static class Address {
        private String street;
        private String city;
        private String zipCode;
    }
}
```

Generated Protocol Buffer:
```protobuf
syntax = "proto3";

package com.example;

message User {
  optional string username = 1;
  optional string email = 2;
  optional int32 age = 3;
  optional Address address = 4;
}

message Address {
  optional string street = 1;
  optional string city = 2;
  optional string zip_code = 3;
}
```

## License

This project is licensed under the MIT License - see the LICENSE file for details

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
