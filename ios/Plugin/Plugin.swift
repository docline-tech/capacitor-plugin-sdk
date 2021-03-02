import Foundation
import Capacitor
import DoclineVideoSDK

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(DoclineSDK)
public class DoclineSDK: CAPPlugin {
    var call: CAPPluginCall?
    
    @objc func join(_ call: CAPPluginCall) {
        self.call = call
        
        guard let roomCode = call.getString("code"),
              let serverURL = call.getString("path") else {
            
            sendError(.unauthorizedError)
            return
        }
    
        let setupData = Docline.Setup (serverURL: serverURL)
        let options = Docline.Options(roomCode: roomCode)
        
        DispatchQueue.main.async { [weak self] in
            Docline.join(setupData, options: options, delegate: self)
        }
    }
    
    // MARK: Private Logic
    
    private func sendError(_ error: Docline.ResponseError) {
        let messageAsDictionary = errorToDictionary(error: error)
        call?.reject(error.localizedDescription, nil, error, messageAsDictionary)
        removeAllListeners(call)
        call = nil
    }
    
    private func errorToDictionary(error: Docline.ResponseError) -> [String: Any] {
        var dictionary: [String: Any] = [:]
        var errorType: ErrorType = .defaultError
        
        switch error {
        case .unauthorizedError:
            errorType = .unauthorizedError
        case .emptyCodeError:
            errorType = .emptyCodeError
        case .connectionError:
            errorType = .connectionError
        case .customError(let message):
            errorType = .customError
            dictionary["message"] = message
        default: break
        }
        dictionary["type"] = errorType.rawValue
        
        return dictionary
    }
    
    private func sendEvent(_ dictionary: [EventKeys: Any]) {
        guard let eventName = dictionary[.eventId] as? String else {
            return
        }
        
        let data: [String: Any] = tranform(dictionary)
        notifyListeners(eventName, data: data)                
    }
    
    private func tranform(_ dictionary: [EventKeys: Any] ) -> [String: Any] {
        var result: [String: Any] = [:]
        for (key, value) in dictionary {
            result[key.rawValue] = value
        }
        return result
    }
    
    private func eventToDictionary(id: String) -> [EventKeys: Any] {
        var dictionary: [EventKeys: Any] = [:]
        dictionary[.eventId] = id
        
        return dictionary
    }
    
    // MARK: Models
    
    enum ErrorType: String {
        case unauthorizedError,
             emptyCodeError,
             connectionError,
             customError,
             defaultError
    }
    
    enum EventKeys: String {
        case eventId
        case screenId
        case cameraSource
        case isEnabled
        case participantType
    }
}

extension DoclineSDK: DoclineDelegate {
    
    public func consultationJoinError(_ error: Docline.ResponseError) {
        sendError(error)
    }
    
    public func consultationJoinSuccess(_ sender: DoclineViewController) {
        sender.recordingDelegate = self
        sender.connectionDelegate = self
        sender.participantDelegate = self
        sender.chatDelegate = self
        
        DispatchQueue.main.async { [weak self] in
            self?.bridge.viewController.present(sender, animated: true, completion: nil)
        }
        
        sendGeneralEvent(.consultationJoinSuccess)
        call?.resolve()
        removeAllListeners(call)
        call = nil
    }
    
    public func consultationTerminated(_ screenView: Docline.ScreenView) {
        DispatchQueue.main.async { [weak self] in
            self?.bridge.viewController.dismiss(animated: true, completion: nil)
        }
        
        sendGeneralEvent(.consultationTerminated)                
    }
    
    public func show(_ screenView: DoclineVideoSDK.Docline.ScreenView) {
        var dictionary: [EventKeys: Any] = [:]
        let screenId = ScreenId(screenView: screenView)
        dictionary[.screenId] = screenId.rawValue
        sendGeneralEvent(.showScreenView, params: dictionary)
    }
    
    public func consultationJoined() {
        sendGeneralEvent(.consultationJoined)
    }
    
    public func updatedCamera(_ screenView: DoclineVideoSDK.Docline.ScreenView,
                              source: DoclineVideoSDK.Docline.CameraSource) {
        var dictionary: [EventKeys: Any] = [:]
        let screenId = ScreenId(screenView: screenView)
        dictionary[.screenId] = screenId.rawValue
        let cameraSource = CameraSource(source: source)
        dictionary[.cameraSource] = cameraSource.rawValue
        sendGeneralEvent(.updatedCameraSource, params: dictionary)
    }
    
    public func updatedCamera(_ screenView: DoclineVideoSDK.Docline.ScreenView,
                              isEnabled: Bool) {
        var dictionary: [EventKeys: Any] = [:]
        let screenId = ScreenId(screenView: screenView)
        dictionary[.screenId] = screenId.rawValue
        dictionary[.isEnabled] = isEnabled
        sendGeneralEvent(.updatedCameraStatus, params: dictionary)
    }
    
    public func updatedMicrophone(_ screenView: DoclineVideoSDK.Docline.ScreenView,
                                  isEnabled: Bool) {
        var dictionary: [EventKeys: Any] = [:]
        let screenId = ScreenId(screenView: screenView)
        dictionary[.screenId] = screenId.rawValue
        dictionary[.isEnabled] = isEnabled
        
        sendGeneralEvent(.updatedMicrophone, params: dictionary)
    }
    
    // MARK: Private Logic
    
    private func sendGeneralEvent(_ event: GeneralEventId,
                                  params: [EventKeys: Any] = [:]) {
        var dictionary = eventToDictionary(id: event.rawValue)
        dictionary.merge(params, uniquingKeysWith: { (_, new) in new })
        sendEvent(dictionary)
    }
    
    // MARK: General Event Model
    
    enum GeneralEventId: String {
        case consultationJoinSuccess,
             consultationTerminated,
             showScreenView,
             updatedCameraSource,
             updatedCameraStatus,
             updatedMicrophone,
             consultationJoined
    }
    
    enum ScreenId: String {
        case setupScreen,
             waitingRoom,
             videoConsultation,
             permissionsScreen
        
        init(screenView: DoclineVideoSDK.Docline.ScreenView) {
            switch screenView {
            case .setupScreen:
                self = .setupScreen
            case .waitingRoom:
                self = .waitingRoom
            case .videoConsultation:
                self = .videoConsultation
            case .permissionsScreen:
                self = .permissionsScreen
            @unknown default:
                self = .setupScreen
            }
        }
    }
    
    enum CameraSource: String {
        case front, back
        
        init(source: DoclineVideoSDK.Docline.CameraSource) {
            switch source {
            case .front:
                self = .front
            case .back:
                self = .back
            @unknown default:
                self = .front
            }
        }
    }
}

extension DoclineSDK: DoclineRecordingDelegate {
    
    public func screenRecordingStarted() {
        sendRecordingEvent(.screenRecordingStarted)
    }
    
    public func screenRecordingFinished() {
        sendRecordingEvent(.screenRecordingFinished)
    }
    
    public func screenRecordingApproved() {
        sendRecordingEvent(.screenRecordingApproved)
    }
    
    public func screenRecordingDenied() {
        sendRecordingEvent(.screenRecordingDenied)
    }
    
    // MARK: Private Logic
    
    private func sendRecordingEvent(_ event: RecordingEventId) {
        let dictionary = eventToDictionary(id: event.rawValue)
        sendEvent(dictionary)
    }
    
    // MARK: Recording Event Model
    
    enum RecordingEventId: String {
        case screenRecordingStarted,
             screenRecordingFinished,
             screenRecordingApproved,
             screenRecordingDenied
    }
}

extension DoclineSDK: DoclineConnectionDelegate {
    
    public func consultationReconnecting() {
        sendConnectionEvent(.consultationReconnecting)
    }
    
    public func consultationReconnected() {
        sendConnectionEvent(.consultationReconnected)
    }
    
    public func discconectedByError() {
        sendConnectionEvent(.discconectedByError)
    }
    
    public func userSelectExit() {
        sendConnectionEvent(.userSelectExit)
    }
    
    public func userTryReconnect() {
        sendConnectionEvent(.userTryReconnect)
    }
    
    // MARK: Private Logic
    
    private func sendConnectionEvent(_ event: ConnectionEventId) {
        let dictionary = eventToDictionary(id: event.rawValue)
        sendEvent(dictionary)
    }
    
    // MARK: Recording Event Model
    
    enum ConnectionEventId: String {
        case consultationReconnecting,
             consultationReconnected,
             discconectedByError,
             userSelectExit,
             userTryReconnect
    }
}

extension DoclineSDK: DoclineParticipantDelegate {
    
    public func participantConnected(type: DoclineVideoSDK.Docline.StreamType) {
        sendParticipantEvent(.participantConnected, type: type)
    }
    
    public func participantDisconnected(type: DoclineVideoSDK.Docline.StreamType) {
        sendParticipantEvent(.participantDisconnected, type: type)
    }
    
    public func participantSelected(type: DoclineVideoSDK.Docline.StreamType) {
        sendParticipantEvent(.participantSelected, type: type)
    }
    
    // MARK: Private Logic
    
    private func sendParticipantEvent(_ event: ParticipantEventId,
                                      type: DoclineVideoSDK.Docline.StreamType) {
        var dictionary = eventToDictionary(id: event.rawValue)
        let participantType = ParticipantType(type: type)
        dictionary[.participantType] = participantType.rawValue
        sendEvent(dictionary)
    }
    
    // MARK: Recording Event Model
    
    enum ParticipantEventId: String {
        case participantConnected,
             participantDisconnected,
             participantSelected
    }
    
    enum ParticipantType: String {
        case camera, screen
        
        init(type: DoclineVideoSDK.Docline.StreamType) {
            switch type {
            case .camera:
                self = .camera
            case .screen:
                self = .screen
            @unknown default:
                self = .camera
            }
        }
    }
}

extension DoclineSDK: DoclineChatDelegate {
    
    public func messageSent() {
        sendChatEvent(.messageSent)
    }
    
    public func messageReceived() {
        sendChatEvent(.messageReceived)
    }
    
    // MARK: Private Logic
    
    private func sendChatEvent(_ event: ChatEventId) {
        let dictionary = eventToDictionary(id: event.rawValue)
        sendEvent(dictionary)
    }
    
    // MARK: Recording Event Model
    
    enum ChatEventId: String {
        case messageSent,
             messageReceived
    }
}
