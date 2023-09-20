import Foundation

@objc public class DoclineSDK: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
