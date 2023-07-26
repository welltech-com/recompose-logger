# Recomposition Logger

This plugin makes it easy to debug composable functions by adding logs to each function.
The plugin also highlights composable functions during recomposition



https://user-images.githubusercontent.com/105854390/235658540-90d394cd-9154-4a20-83ae-e94e718da580.mov



## requirements:
- v1.6.x: kotlin 1.8.21
- v1.5.0: kotlin 1.8.20
- v1.4.0: kotlin 1.8.10
- v1.3.0: kotlin 1.8.0
- v1.2.0: kotlin 1.7.20
- v1.1.0: kotlin 1.7.0

# Integration
### root build.gradle
```kotlin
buildscript {
    repositories {
       // ...
       mavenCentral()  
    }
    dependencies {
        // ...
      classpath("com.welltech:recomposition-logger-plugin:$version")
    }
}
```

### app build.gradle.kts
```kotlin
plugins {
  id("com.welltech.recomposition-logger-plugin")
}
```

Also, you can configure plugin in your app:build.gradle.kts
```kotlin
recompositionLogger {
  tag = "SampleRecomposition" // tag for recomposition logs
}
```

Other plugin options:
- `enabled [Boolean]` - when false, plugin don't add any additional code for debug functionality. It false for release and true for debug by default
- `supportLibConfigurationName [String, default: debugImplementation]` - configuration name of support lib dependency. 
  Should be changed if you change the logic for `enabled`. For example if you have flavor `dev` and `enabled` configured 
on this flavor you should set `supportLibConfigurationName = "devImplementation"`
- `tag [String, default: "RecompositionLog"]` - tag for recomposition logs.
- `useRebugger [Boolean]` - use [Rebugger](https://github.com/theapache64/rebugger) for logging (experimental)

# Features

## Highlighting recomposition

To enable highlight you should update `debugHighlightOptions` property

```kotlin
class YourApplication : Application() {

    override fun onCreate() {
      super.onCreate()
      debugHighlightOptions = debugHighlightOptions.copy(enabled = true)
    }
}
```

You can change `debugHighlightOptions` in runtime:
```kotlin
@Composable
fun ScreenContent() {
  //...
  //...
  Button(
    onClick = { debugHighlightOptions = debugHighlightOptions.copy(enabled = !debugHighlightOptions.enabled) },
    content = { Text(text = "Change highlighting") }
  )
}
```

You can disable logs and highlighting for specific function using annotation `@DisableLogs`.
It may be useful for root composables (like scaffold)
```kotlin
@Composable
@DisableLogs
fun AppScaffold(
  //...
) {
   //... 
}
```

You can improve logs by `LogArgument` annotation:
```kotlin
@Composable
fun Item(
  @LogArgument title: String
) {
    Text(text = title)
}
```

