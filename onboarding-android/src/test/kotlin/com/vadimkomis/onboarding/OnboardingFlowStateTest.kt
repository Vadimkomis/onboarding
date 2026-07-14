package com.vadimkomis.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingFlowStateTest {
    @Test
    fun initialStateStartsAtFirstPage() {
        val state = OnboardingFlowState(pageCount = 3)

        assertEquals(3, state.pageCount)
        assertEquals(0, state.selectedIndex)
        assertFalse(state.isLastPage)
    }

    @Test
    fun initialStateClampsSelectedIndexToAvailablePages() {
        assertEquals(0, OnboardingFlowState(pageCount = 3, selectedIndex = -1).selectedIndex)
        assertEquals(2, OnboardingFlowState(pageCount = 3, selectedIndex = 8).selectedIndex)
    }

    @Test
    fun emptyStateIsComplete() {
        val state = OnboardingFlowState(pageCount = 0)

        assertEquals(0, state.pageCount)
        assertEquals(0, state.selectedIndex)
        assertTrue(state.isLastPage)
    }

    @Test
    fun negativePageCountIsTreatedAsEmpty() {
        val state = OnboardingFlowState(pageCount = -2, selectedIndex = 1)

        assertEquals(0, state.pageCount)
        assertEquals(0, state.selectedIndex)
        assertTrue(state.isLastPage)
    }

    @Test
    fun singlePageStateIsComplete() {
        val state = OnboardingFlowState(pageCount = 1)

        assertTrue(state.isLastPage)
        assertFalse(state.advance())
        assertEquals(0, state.selectedIndex)
    }

    @Test
    fun advanceMovesForwardUntilLastPage() {
        val state = OnboardingFlowState(pageCount = 3)

        assertTrue(state.advance())
        assertEquals(1, state.selectedIndex)
        assertFalse(state.isLastPage)

        assertTrue(state.advance())
        assertEquals(2, state.selectedIndex)
        assertTrue(state.isLastPage)

        assertFalse(state.advance())
        assertEquals(2, state.selectedIndex)
    }
}
