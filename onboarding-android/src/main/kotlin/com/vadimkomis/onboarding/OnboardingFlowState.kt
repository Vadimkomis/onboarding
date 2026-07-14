package com.vadimkomis.onboarding

internal class OnboardingFlowState(
    pageCount: Int,
    selectedIndex: Int = 0,
) {
    val pageCount: Int = pageCount.coerceAtLeast(0)

    var selectedIndex: Int = selectedIndex.coerceIn(
        minimumValue = 0,
        maximumValue = (this.pageCount - 1).coerceAtLeast(0),
    )
        private set

    val isLastPage: Boolean
        get() = selectedIndex >= pageCount - 1

    fun advance(): Boolean {
        if (isLastPage) {
            return false
        }

        selectedIndex += 1
        return true
    }
}
