# Hardware Lock

> Note: Currently only works on Windows and Raspberry Pi

Secure your JAR file distribution by binding execution to machine-specific hardware
identifiers:

- `CPU Serial Number`
- `Machine Id`
- `Mac Address`

This project's goal is to prevent non-technical users from distributing and running your JAR on unknown devices. This is
particularly useful if you develop hardware and software projects with Java or Kotlin and are looking for a way to
protect your software even a little bit. This project is not meant to be an all-in-one solution for your protection
needs.

## Getting Started

Import the dependency into your project as a maven dependency:

```
<dependency>
  <groupId>com.raphile14</groupId>
  <artifactId>hardwarelock</artifactId>
  <version>1.0.0</version>
</dependency>
```

_Check this [link](https://github.com/Raphile14/hardwarelock/packages/2296280) for latest version._

## Using Hardware Lock

**Hardware Lock** verifies the session of your JAR's current environment everytime the `validate` function is called by
comparing the hashed values of your device's hardware identifiers from your env's stored hashed key. If the values not
equal then the function will throw a `RuntimeException`.

The hash value generated on runtime follows the following format:

```kotlin
val content = "$cpuSerialNumber $delimiter $macAddress $delimiter $machineId" 
```

_Feel free to choose your delimiter._

Be sure to follow the same format when generating the hashed value for your env.

The function below will return your device's CPU serial number.

```kotlin
val result = HardwareLockService.getCpuSerialNumber()
```

This one returns your device's machine id.

```kotlin
val result = HardwareLockService.getMachineId()
```

While this one returns your device's MAC address.

```kotlin
val result = HardwareLockService.getMacAddress()
```

There is also a `hashIds` function that accepts a string and an optional delimiter. This function will return a hashed
value of the string. You can use this to generate the hashed value for your env.

```kotlin
val content = "$cpuSerialNumber $delimiter $macAddress $delimiter $machineId"
val algorithm = "SHA-256"
val result = HardwareLockService.hashIds(content, algorithm)
```

To perform the validation, call the `validate` function with the hashed value you got from the `hashIds` function. I
recommend calling this function on your `main` class.

```kotlin
val key = "hashedKey" // from your env
HardwareLockService.validate(key)
```

## License

This project is licensed under the [MIT license](https://github.com/Raphile14/hardwarelock/blob/main/LICENSE).