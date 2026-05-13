import Foundation

struct OnboardingFlowState: Equatable {
    let pageCount: Int
    var selectedIndex: Int

    init(pageCount: Int, selectedIndex: Int = 0) {
        let normalizedPageCount = max(0, pageCount)

        self.pageCount = normalizedPageCount
        self.selectedIndex = min(max(0, selectedIndex), max(0, normalizedPageCount - 1))
    }

    var isLastPage: Bool {
        selectedIndex >= pageCount - 1
    }

    @discardableResult
    mutating func advance() -> Bool {
        guard !isLastPage else {
            return false
        }

        selectedIndex += 1
        return true
    }
}
