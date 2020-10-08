# SAP Conversational AI - SDK-Android
![SAP Conversational AI Logo](https://cdn.cai.tools.sap/brand/sapcai/sap-cai-black.svg)

# 🚨 Sunset of Open Source SDKs for SAP Conversational AI 
 
SAP Conversational AI provides several SDKs, which are all open-source and hosted on GitHub.  
Starting from January 2021, please note that we inform you that the SDKs will not be available anymore and the public repository of the project will be archived from GitHub.  

## ✨ Why are we sunsetting our SDKs? 
 
Firstly, we noticed over the past year that these SDKs were not used much by our users.  
This is because our platform usage has become easier, including the APIs. 

Secondly, our APIs have undergone major changes. We would need to adapt the SDKs in order to keep them working, which will lead to a significant cost from our side. 

Hence, we decided to sunset this open source version starting from January 2021.  
 
## ✨ What does it mean for me as a user? 
 
Any changes in our API’s will not be reflected in our SDKs. Hence, the code might not work unless you adjust the same.  

## ✨ What are the next steps? 
 
If you are interested in taking the ownership of the project on GitHub, please get in touch with us and we can discuss the process. Otherwise, if there are no objections from anyone, we would archive the project following the open source sunset process.  

Please use the platform SAP Answers if you have any other questions related to this topic. 
 
Happy bot building! 
 
The SAP Conversational AI team

---


## Synopsis
This module is an Android interface to the [SAP Conversational AI](https://cai.tools.sap) API. It allows you to make requests to your bots.

## Installation
Just update your build.gradle files:

In your module:
```gradle
dependencies {
  compile 'ai.sapcai.sdk_android:sdk-android:4.0.0'
}
```

## Usage
```java
import ai.sapcai.sdk_android.Client;

Client client = new Client(YOUR_TOKEN);
Response resp;

try {
  resp = client.textRequest(YOUR_TEXT);
} catch (SapcaiException e) {
  // error
}
```

## Specs
### Classes

This module contains 9 main classes as follows:
* Client is the client allowing you to make requests to SAP Conversational AI API.
* Response contains the response from [SAP Conversational AI](https://cai.tools.sap).
* Entity represents an entity found in you user's input as defined in [SAP Conversational AI Manual](https://cai.tools.sap/docs/#list-of-entities)
* Intent represents the intent of your user
* Action represents the actions of a conversation
* Memory represents the memory of a conversation
* MemoryEntity represents the entity inside the memory object
* Conversation allowing you to handle a conversation
* SapcaiException is the error thrown by the module.

### Class Client
The SAP Conversational AI Client can be instanciated with a token and provides the following methods:

#### Request:
* textRequest(String text)
* fileRequest(String filename)

These methods both return a Response object.

#### Conversation
* textConverse(String text)

This method return a Conversation object.

#### Audio Recording:
* startRecording() *Starts the audio recording to a file*
* stopRecording() *Stops the audio recording, sends the audio to SAP Conversational AI and returns a Response object*

Note that all these methods should be called in separated tasks because they do http requests.


```java
import ai.sapcai.sdk_android.Client;

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
The SAP Conversational AI Response is generated after a call with the Client methods and contains the following methods:
* getAct() *Returns the act of the sentence*
* getType() *Returns the type of the sentence*
* getSentiment() *Returns the sentiment of the sentence*
* getEntity(String name) *Returns the first entity matching -name- or null*
* getEntities(String name) *Returns an array of all entities matching -name- or null*
* getStatus() *Returns the status of the Response*
* getSource() *Returns the source of the input*
* getIntent() *Returns the main intent detected by SAP Conversational AI*
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
The SAP Conversational AI Entity is returned by Response and provides the following methods:
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

SAP Conversational AI entity fields types are dependant on the entity itself, so value returned must be casted depending on the entity you wait.

Refer to [SAP Conversational AI Entities Manual](https://cai.tools.sap/docs/#list-of-entities) for details about entities

```java
Response resp = client.textRequest("What's the weather in San Francisco?");
if (resp.getIntent() != null && resp.getIntent().equals("weather")) {
  Entity e = resp.getEntity("location");
  if (e != null) {
    System.out.printf("You asked me for a weather in %s\n", (String)e.getField("formated"));
  }
}
```

### Class SapcaiException
This exception is thrown when an error occurs during the request

# More

You can view the whole API reference at [cai.tools.sap/docs/api-reference](https://cai.tools.sap/docs/api-reference).

You can follow us on Twitter at [@sapcai](https://twitter.com/sapcai) for updates and releases.

## License

Copyright (c) [2019] [SAP Conversational AI](https://cai.tools.sap)

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
