# SDK-Android
![Recast.AI Logo](https://github.com/RecastAI/SDK-NodeJs/blob/master/misc/logo-inline.png)
Recast.AI official SDK for Android.

## Synopsis
This module is an Android interface to the [Recast.AI](https://recast.ai) API. It allows you to make requests to your bots.

## Installation
Just update your build.gradle files:

In your module:
```gradle
dependencies {
	compile 'ai.recast.sdk_android:sdk-android:1.0.1'
}
```

## Usage
```java
import ai.recast.sdk_android.Client;
import ai.recast.sdk_android.Response;

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

This module contains 5 main classes as follows:
* Client is the client allowing you to make requests to Recast.AI API.
* Response contains the response from [Recast.AI](https://recast.ai).
* Sentence represents a sentence of the response.
* Entity represents an entity found in you user's input as defined in [Recast.AI Manual](https://man.recast.ai/#list-of-entities)
* RecastException is the error thrown by the module.

### Class Client
The Recast.AI Client can be instanciated with or without a token and provides the following methods:

#### Request:
* textRequest(String text)
* textRequest(String text, String token)
* fileRequest(String filename)
* fileRequest(String filename, String token)

These methods both return a Response object.
The token parameter, if provided, override the token given at the construction of the Client.

#### Audio Recording:
* startRecording() *Starts the audio recording to a file*
* stopRecording() *Stops the audio recording, sends the audio to Recast.Ai and returns a Response object*

Note that all these methods should be called in separated tasks because they do http requests.


```java
import ai.recast.sdk_android.Client;
import ai.recast.sdk_android.Response;

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
```

### Class Response
The Recast.AI Response is generated after a call with the Client methods and contains the following methods:
* getSentence() *Returns the first detected sentence*
* getSentences() *Returns an array of all the sentences*
* getEntity(String name) *Returns the first entity matching -name- or null*
* getEntities(String name) *Returns an array of all entities matching -name- or null*
* getEntities() *Returns a map<String, Entity[]> containing all the entities detected in the input*
* getStatus() *Returns the status of the Response*
* getSource() *Returns the source of the input*
* getIntent() *Returns the main intent detected by Recast.AI*
* getIntents() *Returns all intents ordered by probability*
* getVersion() *Returns the version of the JSON*

```java
resp = client.textRequest("Give me a recipe with Asparagus.");
Sentence s = resp.getSentence();
String intent = resp.getIntent();
if (intent != null && intent.equals("recipe")) {
	//get all the entities matching 'ingredient'
	Entities[] entities = resp.getEntities("ingredient");

	// ...
}
```

### Class Sentence
The Recast.AI sentence is returned by the the Recast.AI Response methods and provides the following methods:
* getSource() *Returns the source of the sentence*
* getType() *Returns the [type](https://man.recast.ai/#types-of-sentence) of the sentence*
* getAction() *Returns the action of the sentence*
* getAgent() *Returns the agent of the sentence, can return null*
* getPolarity() *Returns the polarity of the sentence (positive or negative)*
* getEntities() *Returns a map with entitiy name as key and arrays of Entity as values*
* getEntities(String name) *Returns all entities matching -name-*
* getEntity(String name) *Returns the first entity matching -name-*

```java
Response resp = client.textRequest("Hello you! How are you doing?");
Sentence[] s = resp.getSentences();
// This will print "Hello you!"
System.out.println(s[0].getSource());
// This will print "How are you doing?"
System.out.println(s[1].getSource());
```

### Class Entity
The Recast.AI Entity is returned by Response or Sentence and provides the following methods:
* String getName() *Returns the name of the entity*
* String getRaw() *Returns the raw text on which the entity was detected*
* Object getField(String fieldName)


In addition to getName and getRaw, more attributes can be accessed by the getField method which can be one of the following:
* hex
* value
* deg
* formated
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
		System.out.printf("You asked me for a weather in %s\n", (String)e.getField("formated"));
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

Copyright (c) [2016] [Recast.AI](https://recast.ai)

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
