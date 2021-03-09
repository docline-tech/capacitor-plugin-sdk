
# Capacitor Docline SDK plugin
![](https://drive.google.com/uc?export=view&id=1uPXUx1rg8sYWyRu2Q9UNvW2nwaQEVRMS)

This [plugin](https://www.npmjs.com/package/capacitor-plugin-docline-sdk) for Capacitor, allows you to use the Docline SDK.

The Docline SDK makes it easy and simple to integrate Docline video consultation services.

This app needs access to the camera and microphone to make video consultations.

Also available [for Cordova](https://www.npmjs.com/package/cordova-plugin-docline-sdk).

## Supported Platforms
- __iOS 11.0 or higher__ 
- __Android__ (Coming soon)

## Example projects
The example projects are [here](https://gitlab.com/dev-docline/exampledoclinesdk).  

## Contents
- [Installation](#installation)
- [Additional settings](#additional-settings)
- [Usage](#usage)
- [Core Module](#core-module)
- [Error Module](#error-module)
- [Event Module](#event-module)

# Installation
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

# Usage

You need to add this lines to use it.
```javascript
import { Plugins } from '@capacitor/core';
const { DoclineSDK } = Plugins;
```

You need to add this line to use the interfaces and enums defined in the plugin:
```javascript
import { EventId, ErrorType, ... } from 'capacitor-plugin-docline-sdk';
```

## Core Module
Purpose: Generic and miscellaneous functionality.

Remember you can customize the permissions description if needed.

### join()

This method works async. It allows us to connect to the Docline video consultation with the video consultation code.

#### Parameters
- { String } code - The video consultation code

#### Example usage

```javascript
/**
 * Set Listener
 * @param {String} code - The code param is the video consultation code
 * @param {String} path - The path param is the api url
 * @param {Fuction} handleError - The error handler
 * 
 */	
    let code = document.getElementById("code").value;
    let apiURL = "https://api-url";
    
    DoclineSDK.join({
      code: this.code,
      path: apiURL
    })
```
If no error occurrs, we will connect to the video consultation. In both cases events will be emitted, if we want to be notified we will have to subscribe to the event.

#### Example usage
```javascript
import { Plugins } from '@capacitor/core';
const { DoclineSDK } = Plugins;
import { EventId } from 'capacitor-plugin-docline-sdk';

...

code: string = "";

onClick() {  
    let eventId = EventId.consultationJoinSuccess;    
    DoclineSDK.addEventListener(eventId, this.consultationJoinSuccess);
    
    let apiURL = "https://api-url";    
    DoclineSDK.join({
      code: this.code,
      path: apiURL
    })
}

consultationJoinSuccess(data) {
    console.log(`consultationJoinSuccess: ${JSON.stringify(data)}`);
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

#### Example usage
```javascript
import { Plugins } from '@capacitor/core';
const { DoclineSDK } = Plugins;
import { EventId, ErrorType } from 'capacitor-plugin-docline-sdk';
...

join() {
    let errorEvent = EventId.error;
    DoclineSDK.addEventListener(errorEvent, this.handleError);
    
    DoclineSDK.join({
      code: this.code,
      path: apiURL
    })    
}

handleError(error) {    
    console.log(`error: ${JSON.stringify(error)}`);
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


### __Event Consts__
#### Event Id
##### Definition
```javascript
/**
 * General Event Id
 */
enum EventId {
  // General Events
  consultationJoinSuccess = "consultationJoinSuccess", 
  consultationTerminated = "consultationTerminated", 
  showScreenView = "showScreenView", 
  updatedCameraSource = "updatedCameraSource", 
  updatedCameraStatus = "updatedCameraStatus",
  updatedMicrophone = "updatedMicrophone",		
  consultationJoined = "consultationJoined",
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

### addEventListener()
Sets a listener for the indicated event.
#### Parameters
- { String } [eventId](#event-id) - The event id
- { (data: { eventId: string, screenId: string?, cameraSource: string?, isEnabled: Boolean? }) => void } listener - The event listener

#### Example usage
```javascript
import { Plugins } from '@capacitor/core';
const { DoclineSDK } = Plugins;
import { EventId, ErrorType } from 'capacitor-plugin-docline-sdk';

...

configureListener() {
    let eventId = EventId.consultationJoinSuccess;
    DoclineSDK.addEventListener(eventId, this.consultationJoinSuccess);

    let eventId2 = EventId.showScreenView;
    DoclineSDK.addEventListener(eventId2, this.showScreenView);

    let eventId3 = EventId.updatedCameraSource;
    DoclineSDK.addEventListener(eventId3, this.updatedCameraSource);

    let eventId4 = EventId.updatedCameraStatus;
    DoclineSDK.addEventListener(eventId4, this.updatedCameraStatus);

    let eventId5 = EventId.participantConnected;
    DoclineSDK.addEventListener(eventId5, this.participantConnected);
}

// Listeners

consultationJoinSuccess(event) {
    console.log(`{eventId: ${event.eventId}}`);
}

showScreenView(event) {
    console.log(`{eventId: ${event.eventId}, screenId: ${event.screenId}}`);
}

updatedCameraSource(event) {
    console.log(`{eventId: ${event.eventId}, cameraSource: ${event.cameraSource}}`);
}

updatedCameraStatus(event) {
    console.log(`{eventId: ${event.eventId}, isEnabled: ${event.isEnabled}}`);
}

participantConnected(event) {
    console.log(`{eventId: ${event.eventId}, type: ${event.type}}`);
}
```

### General Events

### consultationJoinSuccess
Sent when the [`join()`](#join) method completes execution without errors.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### consultationTerminated
Sent when the query has finished, this will release the native component.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### consultationJoined
Sent upon entering the waiting room.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id
### showScreenView
Sent when a screen loads and indicates the screen is loaded.
#### Parameters
- { eventId: String, screenId: String } event - The event data
    - { String } [eventId](#event-id) - The event id
    - { String } [screenId](#screen-id) - The screen id where the event occurred

### updatedCameraSource
Sent when the source of the camera (front or back) is modified and indicates the screen that generated the event.
#### Parameters
- { eventId: String, screenId: String } event - The event data
    - { String } [eventId](#event-id) - The event id
    - { String } [screenId](#screen-id) - The screen id where the event occurred
    - { String } [cameraSource](#camera-source) - The source of the selected camera

### updatedCameraStatus
Sent when the status of the camera is modified (enabled or disabled) and indicates the screen that generated the event.
#### Parameters
- { eventId: String, screenId: String } event - The event data
    - { String } [eventId](#event-id) - The event id
    - { String } [screenId](#screen-id) - The screen id where the event occurred
    - { Boolean } isEnabled - Indicates whether the camera has been enabled or disabled

### updatedMicrophone
Sent when the status of the microphone is modified (enabled or disabled) and indicates the screen that generated the event.
#### Parameters
- { eventId: String, screenId: String } event - The event data
    - { String } [eventId](#event-id) - The event id
    - { String } [screenId](#screen-id) - The screen id where the event occurred
    - { Boolean } isEnabled - Indicates whether the microphone has been enabled or disabled

&nbsp;

### Recording Events

### screenRecordingStarted
Sent when the video consultation recording begins.
The SDK will automatically ask for user consent and nothing will be recorded until accepted. If the user doesn´t agree, they can exit.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### screenRecordingFinished
Sent when the video consultation recording is finished.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### screenRecordingApproved
Sent when the user accepts the consent of the recording.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### screenRecordingDenied
Sent when the user does not accept the consent of the recording.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

&nbsp;

### Connection Events

### consultationReconnecting
Sent when the connection is lost and the plugin tries an automatic recovery.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### consultationReconnected
Sent after the launch of `consultationReconnecting` event, if the connection is recovered.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### discconectedByError
Sent after the launch of the `consultationReconnecting` event, if the connection isn´t recovered.

The SDK will automatically ask users if they want to retry the reconnection or want to exit the video consultation.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### userSelectExit
Sent after the launch of the `discconectedByError` event, if the user decides to exit the video consultation.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### userTryReconnect
Sent after the launch of the `discconectedByError` event, if the user decides to attempt the reconnection.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

&nbsp;

### __Participant Events__

### participantConnected
Sent when a new participant is connected, this participant can be of camera or screen type.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id
    - { String } [type](#participant-type) - The participant type, can be camera or screen

### participantDisconnected
Sent when a participant disconnects, it can be of camera or screen type.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id
    - { String } [type](#participant-type) - The participant type, can be camera or screen

### participantSelected
Sent when a participant is selected in the list of participants, this participant can be of camera or screen type. The selected participant will go to the main screen of the video consultation.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id
    - { String } [type](#participant-type) - The participant type, can be camera or screen


&nbsp;

### __Chat Events__

### messageSent
Sent when a message is sent in chat.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id

### messageSent
Sent when a message is received in the chat.
#### Parameters
- { eventId: String } event - The event data
    - { String } [eventId](#event-id) - The event id


[CLI]: https://docs.npmjs.com/cli/v7/commands/npm-install

[NPM]: https://www.npmjs.com/package/capacitor-plugin-docline-sdk
