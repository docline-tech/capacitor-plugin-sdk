declare global {
  interface PluginRegistry {
    DoclineSDK: DoclineSDKPlugin;
  }
}

export interface DoclineSDKPlugin {
  join(options: { code: string, path: string }): Promise<void>;
}


export enum ErrorType { 
  unauthorizedError = "unauthorizedError", 
  emptyCodeError = "emptyCodeError", 
  connectionError = "connectionError", 
  customError = "customError", 
  defaultError = "defaultError" 
}

export enum EventId {
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
