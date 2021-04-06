import type { PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/core' {
  interface PluginRegistry {
    DoclineSDK: DoclineSDKPlugin;
  }
}

export interface DoclineSDKPlugin {
  join(options: { code: string, path: string, color: string }): Promise<void>;

  // Error Add Listener
  
  addListener(
    eventName: EventId.error,
    listenerFunc: (error: ErrorData) => void,
  ): PluginListenerHandle;

  // Events Add Listener

  addListener(
    eventName: EventId,
    listenerFunc: (data: EventData) => void,
  ): PluginListenerHandle;
}

export interface ErrorData {
  // The error type
  type: ErrorType; 
  // The customError message
  message?: string;
}

export interface EventData {
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

export enum ErrorType { 
  unauthorizedError = "unauthorizedError", 
  emptyCodeError = "emptyCodeError", 
  connectionError = "connectionError", 
  customError = "customError", 
  defaultError = "defaultError" 
}

export enum EventId {
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

export enum ScreenId { 
  setupScreen = "setupScreen", 
  waitingRoom = "waitingRoom", 
  videoConsultation = "videoConsultation", 
  permissionsScreen = "permissionsScreen"
}

export enum CameraSource { 
  front = "front", 
  back = "back"
}

export enum ParticipantType { 
  camera = "camera", 
  screen = "screen"
}
