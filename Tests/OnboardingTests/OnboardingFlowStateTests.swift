import XCTest

@testable import Onboarding

final class OnboardingFlowStateTests: XCTestCase {
    func testInitialStateStartsAtFirstPage() {
        let state = OnboardingFlowState(pageCount: 3)

        XCTAssertEqual(state.pageCount, 3)
        XCTAssertEqual(state.selectedIndex, 0)
        XCTAssertFalse(state.isLastPage)
    }

    func testInitialStateClampsSelectedIndexToAvailablePages() {
        XCTAssertEqual(OnboardingFlowState(pageCount: 3, selectedIndex: -1).selectedIndex, 0)
        XCTAssertEqual(OnboardingFlowState(pageCount: 3, selectedIndex: 8).selectedIndex, 2)
    }

    func testEmptyStateIsComplete() {
        let state = OnboardingFlowState(pageCount: 0)

        XCTAssertEqual(state.pageCount, 0)
        XCTAssertEqual(state.selectedIndex, 0)
        XCTAssertTrue(state.isLastPage)
    }

    func testNegativePageCountIsTreatedAsEmpty() {
        let state = OnboardingFlowState(pageCount: -2, selectedIndex: 1)

        XCTAssertEqual(state.pageCount, 0)
        XCTAssertEqual(state.selectedIndex, 0)
        XCTAssertTrue(state.isLastPage)
    }

    func testAdvanceMovesForwardUntilLastPage() {
        var state = OnboardingFlowState(pageCount: 3)

        XCTAssertTrue(state.advance())
        XCTAssertEqual(state.selectedIndex, 1)
        XCTAssertFalse(state.isLastPage)

        XCTAssertTrue(state.advance())
        XCTAssertEqual(state.selectedIndex, 2)
        XCTAssertTrue(state.isLastPage)

        XCTAssertFalse(state.advance())
        XCTAssertEqual(state.selectedIndex, 2)
    }
}
