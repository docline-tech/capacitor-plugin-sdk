
# Capacitor Docline SDK plugin
![](https://drive.google.com/uc?export=view&id=1_VGN5i9_djalUq5SLYMeOCKvZgXteNSI)

This [plugin](https://www.npmjs.com/package/capacitor-plugin-docline-sdk) for Capacitor, allows you to use the Docline SDK.

The Docline SDK makes it easy and simple to integrate Docline video consultation services.

This app needs access to the camera and microphone to make video consultations.

## Supported
- __iOS 12.0 or higher__ 
- __Android5 or higher__
- __Capacitor v2__ (plugin version 1.0.4)
- __Capacitor v3__ (plugin version 1.0.8 or higher)

## Example projects
The example projects are [here](https://github.com/docline-tech/sdk-example).  

## Contents
- [Installation](#installation)
- [Additional settings](#additional-settings)
- [Usage](#usage)
- [Core Module](#core-module)
- [Error Module](#error-module)
- [Event Module](#event-module)

# Installation
cristian@MacBook-Pro-16 ~ % node --version
v16.16.0
cristian@MacBook-Pro-16 ~ % npm --version
8.11.0

The plugin can be installed via [NPM-CLI][CLI] and is publicly available on [NPM][npm].

Execute from the project's root folder:

    $ npm install capacitor-plugin-docline-sdk
    
## Removing the Plugin from the project

Using [NPM-CLI][CLI].

Execute from the project's root folder:

    $ npm uninstall capacitor-plugin-docline-sdk


[comment]: <> (TODO: Configure Permissions description)

## __Additional settings__
## iOS
### Add the description of use of the microphone and the camera in the `info.plist` file.
You need to add two entries in the file:
- `NSCameraUsageDescription`, description of the use of the camera.
- ` NSMicrophoneUsageDescription`, description of the use of the microphone.

__info.plist__
![](https://drive.google.com/uc?export=view&id=1QOg43SRokTZ8O9RT1hYoIpsroUYXQkUm)

If your application supports multiple languages, you need to add the translated descriptions in the `Localizable.string` file.

English
```swift
"NSCameraUsageDescription" = "This app needs access to the camera to make video consultations.";
"NSMicrophoneUsageDescription" = "This app needs access to the microphone to make video consultations.";
```

Spanish
```swift
"NSCameraUsageDescription" = "Esta aplicación necesita acceder a la cámara para realizar videoconsultas.";
"NSMicrophoneUsageDescription" = "Esta aplicación necesita acceder al micrófono para realizar videoconsultas.";
```


## Android

In order to complete the setup on DoclineSDK for capacitor you need to follow few more next steps.

### Enabled databinding to project level gradle

By default its android.app module gradle file.

```gradle
android {
    ...
    buildFeatures {
        dataBinding true
    }

}
```

### Add repositories to app level gradle

You need to add the following repositories to the top app level so dependencies can be resolved.

```gradle
...
allprojects {

    repositories {
        ...
        maven { url "https://nexus.docline.com/repository/maven-public/" }
        maven { url 'https://tokbox.bintray.com/maven' }
    }
}
...
```
### Import capacitor plugin inside BridgeActivity

You need to add our SDK interface inside BridgeActivity generated by ionic capacitor build.

```java
package docline.sdk.example;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
import capacitor.plugin.docline.sdk.DoclineSDKPlugin;

public class MainActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // register plugin Docline
      registerPlugin(DoclineSDKPlugin.class);

    }
}
```

### How to setup brand colors

To setup brand colors in Android you need to create and overwrite colors in your colors.xml project level.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">#PRIMARY_COLOR</color>
    <color name="colorSecondary">#SECONDARY_COLOR</color>
    <color name="colorPrimaryDark">#STATUSBAR_COLOR</color>
    <color name="colorAccent">#PRIMARY_COLOR</color>
</resources>
```


# Usage

You need to add this lines to use it.
```javascript
import { DoclineSDKPlugin } from 'capacitor-plugin-docline-sdk';
import { DoclineSDK } from 'capacitor-plugin-docline-sdk';
const docline: DoclineSDKPlugin = DoclineSDK as DoclineSDKPlugin;
```

You need to add this line to use the interfaces and enums defined in the plugin:
```javascript
import { DoclineSDKPlugin, ErrorData, ErrorType, EventData, EventId } from 'capacitor-plugin-docline-sdk';
```

## Core Module
Purpose: Generic and miscellaneous functionality.

Remember you can customize the permissions description if needed.

### join()

This method works async. It allows us to connect to the Docline video consultation with the video consultation code.

#### Parameters
- { String } code - The video consultation code
- { String } path - The API url
- { String } color - The corporate color

#### Example usage

```javascript
/**
 * Join
 * @param {String} code - The code param is the video consultation code
 * @param {String} path - The path param is the api url
 * @param {String} color - The color param is the corporate color
 */	
    let code = "videoConsultationCode";
    let apiURL = "https://api-url";
    let color = "#0a73ba"
    
    docline.join({
      code: this.code,
      path: apiURL,
      color: color
    });
```
If no error occurrs, we will connect to the video consultation. In both cases events will be emitted, if we want to be notified we will have to subscribe to the event.

#### Example usage
```javascript
import { DoclineSDKPlugin, EventData, EventId } from 'capacitor-plugin-docline-sdk';
import { DoclineSDK } from 'capacitor-plugin-docline-sdk';
const docline: DoclineSDKPlugin = DoclineSDK as DoclineSDKPlugin;

...

code: string = "";

onClick() {  
    docline.addListener(EventId.consultationJoinSuccess, this.consultationJoinSuccess);
    
    let apiURL = "https://api-url";    
    let color = "#0a73ba"
    
    docline.join({
      code: this.code,
      path: apiURL,
      color: color
    });
}

consultationJoinSuccess(event: EventData) {
    console.log(`consultationJoinSuccess: ${JSON.stringify(event)}`);
}
```

## Error Module
Purpose: Manage the errors that the join method can return.

### Error Type
#### Definition
```javascript
/**
 * Error Types
 */
enum ErrorType { 
  unauthorizedError = "unauthorizedError", 
  emptyCodeError = "emptyCodeError", 
  connectionError = "connectionError", 
  customError = "customError", 
  defaultError = "defaultError" 
}
```

### Error Data
#### Definition
```javascript
/**
 * Error Data
 */
interface ErrorData {
  // The error type
  type: ErrorType; 
  // The customError message
  message?: string;
}
```

### addListener()
Sets a listener to handle errors.
#### Parameters
- { EventId.error } eventName - The event name
- { (error: ErrorData) => void } listenerFunc - The event error listener

#### Example usage
```javascript
import { DoclineSDKPlugin, ErrorData, ErrorType } from 'capacitor-plugin-docline-sdk';
import { DoclineSDK } from 'capacitor-plugin-docline-sdk';
const docline: DoclineSDKPlugin = DoclineSDK as DoclineSDKPlugin;
...

join() {
    docline.addListener(EventId.error, this.handleError);    
    
    docline.join({
      code: this.code,
      path: apiURL,
      color: color
    });   
}

handleError(error: ErrorData) {
    switch (error.type) {
        case ErrorType.unauthorizedError:
        console.log("unauthorizedError");
        alert("unauthorizedError");
        break;
        case ErrorType.emptyCodeError:
        console.log("emptyCodeError");
        alert("emptyCodeError");
        break;
        case ErrorType.connectionError:
        console.log("connectionError");
        alert("connectionError");
        break;
        case ErrorType.customError:
        console.log(`customError(${error.message})`);
        alert("customError");
        break;
        case ErrorType.defaultError:
        console.log("defaultError");
        alert("defaultError");
        break;
        default: 
        break;
    }    
}
```

## Event Module
Purpose: Manage the events that are emitted in the background, while the plugin is running.

The events have been grouped into five groups:

  * [General Events.](#general-events)
  * [Recording Events.](#recording-events)
  * [Connection Events.](#connection-events)
  * [Participant Events.](#participant-events)
  * [Chat Events.](#chat-events)


### __Event Models__
#### Event Id
##### Definition
```javascript
/**
 * General Event Id
 */
enum EventId {
  // Error Event
  error = "error",
  // General Events
  consultationJoinSuccess = "consultationJoinSuccess", 
  consultationTerminated = "consultationTerminated", 
  showScreenView = "showScreenView", 
  updatedCameraSource = "updatedCameraSource", 
  updatedCameraStatus = "updatedCameraStatus",
  updatedMicrophone = "updatedMicrophone",		
  consultationJoined = "consultationJoined",
  consultationRejoin = "consultationRejoin",
  consultationExit = "consultationExit",
  // Recording Events
  screenRecordingStarted = "screenRecordingStarted", 
  screenRecordingFinished = "screenRecordingFinished", 
  screenRecordingApproved = "screenRecordingApproved", 
  screenRecordingDenied = "screenRecordingDenied",
  // Connection Events
  consultationReconnecting = "consultationReconnecting", 
  consultationReconnected = "consultationReconnected", 
  discconectedByError = "discconectedByError",
  userSelectExit = "userSelectExit",
  userTryReconnect = "userTryReconnect",
  // Participant Events
  participantConnected = "participantConnected", 
  participantDisconnected = "participantDisconnected", 
  participantSelected = "participantSelected",
  // Chat Events
  messageSent = "messageSent", 
  messageReceived = "messageReceived"
}
```
#### Event Data
##### Definition
```javascript
/**
 * Event Data
 */
interface EventData {
  // The event id 
  eventId: EventId;
  // The screen id where the event occurred
  screenId?: ScreenId; 
  // The source of the selected camera
  cameraSource?: CameraSource;
  // Indicates whether the microphone/camera has been enabled or disabled
  isEnabled?: boolean;
  // The participant type, can be camera or screen
  participantType?: ParticipantType;
}
```

#### Screen Id
##### Definition
```javascript
/**
 * Screen Id
 */
enum ScreenId { 
  setupScreen = "setupScreen", 
  waitingRoom = "waitingRoom", 
  videoConsultation = "videoConsultation", 
  permissionsScreen = "permissionsScreen"
}
```

#### Camera Source
##### Definition
```javascript
/**
 * Camera Source
 */
enum CameraSource { 
  front = "front", 
  back = "back"
}
```
#### Participant Type
##### Definition
```javascript
/**
 * Participant Type
 */
enum ParticipantType { 
  camera = "camera", 
  screen = "screen"
}
```
#### User Type
##### Definition
```javascript
/**
 * User Type
 */
enum UserType { 
  patient = "patient", 
  professional = "professional"
}
```

### addListener()
Sets a listener for the indicated event.
#### Parameters
- { [EventId](#event-id) } eventName - The event name
- { (event: [EventData](#event-data)) => void } listenerFunc - The event listener

#### Example usage
```javascript
import { DoclineSDKPlugin, EventData, EventId } from 'capacitor-plugin-docline-sdk';
import { DoclineSDK } from 'capacitor-plugin-docline-sdk';
const docline: DoclineSDKPlugin = DoclineSDK as DoclineSDKPlugin;

...

configureListener() {
    docline.addListener(EventId.consultationJoinSuccess, this.consultationJoinSuccess);
    docline.addListener(EventId.showScreenView, this.showScreenView);
    docline.addListener(EventId.updatedCameraSource, this.updatedCameraSource);
    docline.addListener(EventId.updatedCameraStatus, this.updatedCameraStatus);
    docline.addListener(EventId.participantConnected, this.participantConnected);
}

// Listeners

consultationJoinSuccess(event: EventData) {
    console.log(`{eventId: ${event.eventId}}`);
}

showScreenView(event: EventData) {
    console.log(`{eventId: ${event.eventId}, screenId: ${event.screenId}}`);
}

updatedCameraSource(event: EventData) {
    console.log(`{eventId: ${event.eventId}, cameraSource: ${event.cameraSource}}`);
}

updatedCameraStatus(event: EventData) {
    console.log(`{eventId: ${event.eventId}, isEnabled: ${event.isEnabled}}`);
}

participantConnected(event: EventData) {
    console.log(`{eventId: ${event.eventId}, type: ${event.participantType}}`);
}
```

### General Events

### consultationJoinSuccess
Sent when the [`join()`](#join) method completes execution without errors.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### consultationTerminated
Sent when the query has finished, this will release the native component.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### consultationJoined
Sent upon entering the waiting room.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
### showScreenView
Sent when a screen loads and indicates the screen is loaded.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [ScreenId](#screen-id) } screenId - The screen id where the event occurred

### updatedCameraSource
Sent when the source of the camera (front or back) is modified and indicates the screen that generated the event.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [ScreenId](#screen-id) } screenId - The screen id where the event occurred
    - { [CameraSource](#camera-source) } cameraSource - The source of the selected camera

### updatedCameraStatus
Sent when the status of the camera is modified (enabled or disabled) and indicates the screen that generated the event.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [ScreenId](#screen-id) } screenId - The screen id where the event occurred
    - { Boolean } isEnabled - Indicates whether the camera has been enabled or disabled

### updatedMicrophone
Sent when the status of the microphone is modified (enabled or disabled) and indicates the screen that generated the event.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [ScreenId](#screen-id) } screenId - The screen id where the event occurred
    - { Boolean } isEnabled - Indicates whether the microphone has been enabled or disabled

### consultationRejoin
Sent when the user decides to rejoin the video consultation from finish screen.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [UserType](#user-type) } userType - The user type

### consultationExit
Sent when the user decides to exit the video consultation from finish screen.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [UserType](#user-type) } userType - The user type


&nbsp;

### Recording Events

### screenRecordingStarted
Sent when the video consultation recording begins.
The SDK will automatically ask for user consent and nothing will be recorded until accepted. If the user doesn´t agree, they can exit.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### screenRecordingFinished
Sent when the video consultation recording is finished.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### screenRecordingApproved
Sent when the user accepts the consent of the recording.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### screenRecordingDenied
Sent when the user does not accept the consent of the recording.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

&nbsp;

### Connection Events

### consultationReconnecting
Sent when the connection is lost and the plugin tries an automatic recovery.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### consultationReconnected
Sent after the launch of `consultationReconnecting` event, if the connection is recovered.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### discconectedByError
Sent after the launch of the `consultationReconnecting` event, if the connection isn´t recovered.

The SDK will automatically ask users if they want to retry the reconnection or want to exit the video consultation.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### userSelectExit
Sent after the launch of the `discconectedByError` event, if the user decides to exit the video consultation.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### userTryReconnect
Sent after the launch of the `discconectedByError` event, if the user decides to attempt the reconnection.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

&nbsp;

### __Participant Events__

### participantConnected
Sent when a new participant is connected, this participant can be of camera or screen type.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [ParticipantType](#participant-type) } participantType - The participant type, can be camera or screen

### participantDisconnected
Sent when a participant disconnects, it can be of camera or screen type.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [ParticipantType](#participant-type) } participantType - The participant type, can be camera or screen

### participantSelected
Sent when a participant is selected in the list of participants, this participant can be of camera or screen type. The selected participant will go to the main screen of the video consultation.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id
    - { [ParticipantType](#participant-type) } participantType - The participant type, can be camera or screen


&nbsp;

### __Chat Events__

### messageSent
Sent when a message is sent in chat.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id

### messageSent
Sent when a message is received in the chat.
#### Parameters
- { [EventData](#event-data) } event - The event data
    - { [EventId](#event-id) } eventId - The event id


[CLI]: https://docs.npmjs.com/cli/v7/commands/npm-install

[NPM]: https://www.npmjs.com/package/capacitor-plugin-docline-sdk
