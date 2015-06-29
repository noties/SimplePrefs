# SimplePrefs
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SimplePrefs-green.svg?style=flat)](https://android-arsenal.com/details/1/2067)

SimplePrefs is an Android library that helps working with SharedPreferences.


## Core
The `core` module minimizes the ammount of code when working with SharedPreferences by overloading methods.

### Installation
[![Maven Central](https://img.shields.io/maven-central/v/ru.noties.simpleprefs/core.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties.simpleprefs%22%20AND%20a%3A%22core%22)
```groovy
compile 'ru.noties.simpleprefs:core:x.x.x'
```

Example:
```java
final SimplePref pref = new SimplePref(mContext, "some_pref");

// getters
final String someString = pref.get("string", null); // or just pref.get("string"); as long as String is the only Object supported by SimplePrefs library
final int someInt = pref.get("int", -1);
final long someLong = pref.get("long", -1L);
final float someFloat = pref.get("float", .0F);
final boolean someBool = pref.get("bool", false);

//setters
pref.set("string", null); // String
pref.set("int", 101);
pref.set("long", -1L);
pref.set("float", 15.F);
pref.set("bool", true);
```

When setting and getting values one should explicitly cast to desired type. For example, adding `L` after a decimal will create a value of type `long`; `F` will set a value to `float`

Sometimes there is a need to set multiple values at once (every call to `set` internally calls `Editor.apply()`), so there is a `Batch` helper class that helps to achive that:
```java
pref.batch() // returns an Object of Type Batch
    .set("string", null)
    .set("int", 0)
    .set("long", 0L)
    .set("float", 33.F)
    .set("bool", false)
    .apply(); // don't forget to call apply
```

## Data-binding
We may step further and create model objects around SharedPreferences data with support for Json serialization/deserialization

**Annotations**: [![Maven Central](https://img.shields.io/maven-central/v/ru.noties.simpleprefs/annotations.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties.simpleprefs%22%20AND%20a%3A%22annotations%22)

**Processor**: [![Maven Central](https://img.shields.io/maven-central/v/ru.noties.simpleprefs/processor.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties.simpleprefs%22%20AND%20a%3A%22processor%22)

```groovy
	compile 'ru.noties.simpleprefs:core:x.x.x'
    provided 'ru.noties.simpleprefs:annotations:x.x.x' // we may mark dependancy as provided, because we won't be needing annotations information at runtime
    apt('ru.noties.simpleprefs:processor:x.x.x') {
        transitive = false
    }
```
SimplePrefs using an awesome apt tool: [page](https://bitbucket.org/hvisser/android-apt)

To define a custom data model we will use java annotations.

### @Preference
The start defining data model we should create public, non-final, non-abstract class that extends `PrefsObject` and annotate it with `@Preference` annotation:
```java
@Preference
public class MySharedData extends PrefsObject {
...
}
```
@Preference has a number of optional parameters:
```java
@Preference(
    value = "my_super_prefs", // the name of SharedPreferences file. If left blank the class name will be used
    isSingleton = true, // if true - an object will be a singleton

    jsonLibrary = JsonLibrary.GSON, // currently only Gson is supported
    isJsonVariableStatic = true, // whether json library variable should be static (no much sence for a singleton)
    catchJsonExceptions = true, // whether the code that works with json should be wrapped around try/catch

    jsonTypes = { Date.class }, // additionally we could provide out own json serialization/deserialization policies, here is the serialized class
    jsonTypeSerializers = { DateJsonSerializer.class } // and here is the serializer
)
```
In order to achive working fuctionality with custom json serializers, we should enumerate them in the exact order in which serialized classes are enumerated. Also, serializer class should implement both `JsonSerializer<T>` and `JsonDeserializer<T>` (in case of Gson)

If `catchJsonExceptions` is set to `true`, then you could override the PrefsObject's method in your data model class to log or do whatever you wish with the catched exception:
```java
@Override
public void onJsonExceptionHandled(Throwable t) {
    ...
}
```

### @Key

The next thing to define keys for your data model is the `@Key` annotation
```java
@Key
private String someKeyString;
```
It has a number of optional parameters:
```java
@Key(
    value = "key_name", // a name for this key, if left blank - field's name will be used
    defaultValue = "default value for this key", // default value for this key as a string, for example "null", "0", "1L", "true"
    isJson = false // indicates that this key should be treated as json. If set to true - defaultValue would not be considered
)
```

After keys are defined we **must** create public non-final getters and setters methods for all of them. The reason behind this is simple: annotation processor will generate a subclass of your model class and will use getters/setters as a data transation pipe. No fields of your data-model will be changed, thus there is no need for accessing them directly. The main and only use for object's fields is to define getters and setters.

If standart pattern of getters and setters would change in IDEA or if there is a need for custom naming, there are two annotations to help with this: `@Setter` and `@Getter`. Both have one required parameter: name of the key. **It should be a real key's name: `name` that was passed to @Key annotation or a field's name**
```java
@Key("some_key")
String token;

@Setter("some_key")
public void noWayIWillTellWhatIDo(String value) {}

@Getter("some_key")
public String nahItsAVoidReally() {}
```

### @OnUpdate
Additionally a data object could be notified when some value is changed. Define a method inside your data object class which takes one parameter - type of the key it listens to
```java
@OnUpdate("some_key")
public void onSomethingInterestingHappend(String value) {
...
}
```
Further you could define a listener interface and call it (see a sample project for the example).


### Using of Data-binding
After your data model is defined you can obtain your data object via static method `PrefsObject.create()`:
```java
final MyPrefsModel pref = PrefsObject.create(MyPrefsModel.class, mContext);
```
Annotation processor will generate a `YourClassName$$SP` class. If you wish you could access it directly, but I discourage you to do so.

```java
final MyPrefsModel pref = PrefsObject.create(MyPrefsModel.class, mContext);
pref.setSomeKey(null);
final String key = pref.getSomeKey();
```

Note, that PrefsObject doesn't use any caching mechanism, so every call to `set...()` or `get...()` will call wrapped SimplePref.


## License

```
  Copyright 2015 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```