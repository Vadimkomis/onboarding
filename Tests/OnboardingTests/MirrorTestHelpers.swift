import XCTest

func mirroredValue<Value>(
    _ label: String,
    in value: some Any,
    file: StaticString = #filePath,
    line: UInt = #line
) throws -> Value {
    let mirror = Mirror(reflecting: value)

    guard let child = mirror.children.first(where: { $0.label == label }) else {
        XCTFail("Expected to find mirrored value named \(label)", file: file, line: line)
        throw MirrorTestError.missingValue(label)
    }

    guard let value = child.value as? Value else {
        XCTFail("Expected \(label) to be \(Value.self)", file: file, line: line)
        throw MirrorTestError.unexpectedType(label)
    }

    return value
}

private enum MirrorTestError: Error {
    case missingValue(String)
    case unexpectedType(String)
}
