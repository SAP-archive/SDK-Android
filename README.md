# Recast.AI - SDK-Android
![Recast.AI Logo](https://cdn.recast.ai/brand/recast-ai-logo-inline.png)
Recast.AI official SDK for Android.

## Synopsis
This module is an Android interface to the [Recast.AI](https://recast.ai) API. It allows you to make requests to your bots.

## Installation
Just update your build.gradle files:

In your module:
```gradle
dependencies {
  compile 'ai.recast.sdk_android:sdk-android:4.0.0'
}
```

## Usage
```java
import ai.recast.sdk_android.Client;

Client client = new Client(YOUR_TOKEN);
Response resp;

try {
  resp = client.textRequest(YOUR_TEXT);
} catch (RecastException e) {
  // error
}
```

## Specs
### Classes

This module contains 9 main classes as follows:
* Client is the client allowing you to make requests to Recast.AI API.
* Response contains the response from [Recast.AI](https://recast.ai).
* Entity represents an entity found in you user's input as defined in [Recast.AI Manual](https://man.recast.ai/#list-of-entities)
* Intent represents the intent of your user
* Action represents the actions of a conversation
* Memory represents the memory of a conversation
* MemoryEntity represents the entity inside the memory object
* Conversation allowing you to handle a conversation
* RecastException is the error thrown by the module.

### Class Client
The Recast.AI Client can be instanciated with a token and provides the following methods:

#### Request:
* textRequest(String text)
* fileRequest(String filename)

These methods both return a Response object.

#### Conversation
* textConverse(String text)

This method return a Conversation object.

#### Audio Recording:
* startRecording() *Starts the audio recording to a file*
* stopRecording() *Stops the audio recording, sends the audio to Recast.Ai and returns a Response object*

Note that all these methods should be called in separated tasks because they do http requests.


```java
import ai.recast.sdk_android.Client;

Client client = new Client(YOUR_TOKEN);
Response resp;

try {
  resp = client.textRequest("Hello World!");
  // Do your code...
} catch (Exception e) {
  // Handle error
}

try {
  resp = client.fileRequest("my_file.wav");
  // Do you code..
} catch (Exception e) {
  // Handle error
}

try {
  Conversation conversation = client.textConverse("Hello, my name is Paul");
} catch (Exception e) {
  // Handle error
}
```

### Class Response
The Recast.AI Response is generated after a call with the Client methods and contains the following methods:
* getAct() *Returns the act of the sentence*
* getType() *Returns the type of the sentence*
* getSentiment() *Returns the sentiment of the sentence*
* getEntity(String name) *Returns the first entity matching -name- or null*
* getEntities(String name) *Returns an array of all entities matching -name- or null*
* getStatus() *Returns the status of the Response*
* getSource() *Returns the source of the input*
* getIntent() *Returns the main intent detected by Recast.AI*
* getIntents() *Returns all intents ordered by probability*
* getVersion() *Returns the version of the JSON*
* getUuid() *Returns the uuid of the response*
* IsAbbreviation() *IsAbbreviation returns whether or not the sentence is asking for an abbreviation*
* IsEntity() *IsEntity returns whether or not the sentence is asking for an entity*
* IsDescription() *IsDescription returns whether or not the sentence is asking for an description*
* IsHuman() *IsHuman returns whether or not the sentence is asking for an human*
* IsLocation() *IsLocation returns whether or not the sentence is asking for an location*
* IsNumber() *IsNumber returns whether or not the sentence is asking for an number*
* IsPositive() *IsPositive returns whether or not the sentiment is positive*
* IsVeryPositive() *IsVeryPositive returns whether or not the sentiment is very positive*
* IsNeutral() *IsNeutral returns whether or not the sentiment is neutral*
* IsNegative() *IsNegative returns whether or not the sentiment is negative*
* IsVeryNegative() *IsVeryNegative returns whether or not the sentiment is very negative*
* IsAssert() *IsAssert returns whether or not the sentence is an assertion*
* IsCommand() *IsCommand returns whether or not the sentence is a command*
* IsWhQuery() *IsWhQuery returns whether or not the sentence is a wh query*
* IsYnQuery() *IsYnQuery returns whether or not the sentence is a yes-no question*
* Get(name string) *Returns the first entity matching -name-*
* All(name string) *Returns all entities matching -name-*


```java
resp = client.textRequest("Give me a recipe with Asparagus.");
String intent = resp.getIntent();
if (intent != null && intent.equals("recipe")) {
  //get all the entities matching 'ingredient'
  Entities[] entities = resp.getEntities("ingredient");

  // ...
}
```

### Class Entity
The Recast.AI Entity is returned by Response and provides the following methods:
* String getName() *Returns the name of the entity*
* String getRaw() *Returns the raw text on which the entity was detected*
* Object getField(String fieldName)


In addition to getName and getRaw, more attributes can be accessed by the getField method which can be one of the following:
* hex
* value
* deg
* formatted
* lng
* lat
* unit
* code
* person
* number
* gender
* next
* grain
* order

Recast.AI entity fields types are dependant on the entity itself, so value returned must be casted depending on the entity you wait.

Refer to [Recast.Ai Entities Manual](https://man.recast.ai/#list-of-entities) for details about entities

```java
Response resp = client.textRequest("What's the weather in San Francisco?");
if (resp.getIntent() != null && resp.getIntent().equals("weather")) {
  Entity e = resp.getEntity("location");
  if (e != null) {
    System.out.printf("You asked me for a weather in %s\n", (String)e.getField("formatted"));
  }
}
```

### Class RecastException
This exception is thrown when an error occurs during the request

# More

You can view the whole API reference at [man.recast.ai](https://man.recast.ai).


## Author

François Triquet, francois.triquet@recast.ai

You can follow us on Twitter at [@recastai](https://twitter.com/recastai) for updates and releases.

## License

Copyright (c) [2017] [Recast.AI](https://recast.ai)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
