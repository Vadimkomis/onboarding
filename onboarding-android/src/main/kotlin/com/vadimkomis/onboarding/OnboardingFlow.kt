package com.vadimkomis.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

internal object OnboardingTestTags {
    const val flow: String = "onboarding_flow"
    const val pager: String = "onboarding_pager"
    const val progress: String = "onboarding_progress"
    const val primaryButton: String = "onboarding_primary_button"
    const val skipButton: String = "onboarding_skip_button"
    const val gateContent: String = "onboarding_gate_content"
    const val videoPoster: String = "onboarding_video_poster"

    fun page(index: Int): String = "onboarding_page_$index"
    fun media(pageId: String): String = "onboarding_media_$pageId"
}

@Composable
public fun OnboardingFlow(
    pages: List<OnboardingPage>,
    modifier: Modifier = Modifier,
    theme: OnboardingTheme = OnboardingTheme.standard,
    continueTitle: String = "Continue",
    completeTitle: String = "Get Started",
    skipTitle: String = "Skip",
    allowsSkipping: Boolean = true,
    onComplete: () -> Unit,
) {
    val pagerState = rememberOnboardingPagerState(pages)
    val completeOnce = rememberCompletionCallback(pages, onComplete)
    val state = OnboardingFlowState(pages.size, pagerState.currentPage)
    val coroutineScope = rememberCoroutineScope()

    PreloadOnboardingMedia(pages.map(OnboardingPage::media))
    OnboardingSurface(modifier, theme) {
        OnboardingProgress(pages.size, state.selectedIndex, theme)
        PagePager(pages, pagerState, theme, Modifier.weight(1f))
        OnboardingControls(
            primaryTitle = if (state.isLastPage) completeTitle else continueTitle,
            skipTitle = skipTitle,
            showSkip = allowsSkipping && !state.isLastPage,
            theme = theme,
            onPrimary = {
                if (state.isLastPage) completeOnce()
                else coroutineScope.launch {
                    pagerState.animateScrollToPage(state.selectedIndex + 1)
                }
            },
            onSkip = completeOnce,
        )
    }
}

@Composable
private fun OnboardingSurface(
    modifier: Modifier,
    theme: OnboardingTheme,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag(OnboardingTestTags.flow),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = content,
        )
    }
}

@Composable
private fun rememberOnboardingPagerState(pages: List<OnboardingPage>): PagerState {
    val pageIds = pages.map(OnboardingPage::id)
    var savedPage by rememberSaveable(pageIds) { mutableIntStateOf(0) }
    val initialPage = savedPage.coerceIn(0, pages.lastIndex.coerceAtLeast(0))
    val pagerState = rememberPagerState(initialPage = initialPage) { pages.size }

    LaunchedEffect(pagerState, pageIds) {
        val targetPage = savedPage.coerceIn(0, pages.lastIndex.coerceAtLeast(0))
        if (pages.isNotEmpty() && pagerState.currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { savedPage = it }
    }
    return pagerState
}

@Composable
private fun rememberCompletionCallback(
    pages: List<OnboardingPage>,
    onComplete: () -> Unit,
): () -> Unit {
    val latestCallback = rememberUpdatedState(onComplete)
    val wasInvoked = rememberSaveable(pages.map(OnboardingPage::id)) { mutableStateOf(false) }
    return remember(wasInvoked) {
        {
            wasInvoked.value = invokeCompletionOnce(wasInvoked.value) {
                latestCallback.value()
            }
        }
    }
}

internal fun invokeCompletionOnce(
    wasInvoked: Boolean,
    onComplete: () -> Unit,
): Boolean {
    if (wasInvoked) return true
    onComplete()
    return true
}

@Composable
private fun OnboardingProgress(
    pageCount: Int,
    selectedIndex: Int,
    theme: OnboardingTheme,
) {
    val description = if (pageCount == 0) {
        "No onboarding pages"
    } else {
        "Page ${selectedIndex + 1} of $pageCount"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = description }
            .testTag(OnboardingTestTags.progress),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        repeat(pageCount) { index -> ProgressSegment(index == selectedIndex, theme) }
    }
}

@Composable
private fun ProgressSegment(selected: Boolean, theme: OnboardingTheme) {
    val width by animateDpAsState(
        targetValue = if (selected) 30.dp else 8.dp,
        label = "onboarding progress width",
    )
    Box(
        modifier = Modifier
            .width(width)
            .height(8.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) theme.accentColor else theme.raisedColor),
    )
}

@Composable
private fun PagePager(
    pages: List<OnboardingPage>,
    pagerState: PagerState,
    theme: OnboardingTheme,
    modifier: Modifier,
) {
    if (pages.isEmpty()) {
        Spacer(modifier = modifier)
        return
    }
    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .testTag(OnboardingTestTags.pager),
        key = { index -> pages[index].id to index },
    ) { index ->
        OnboardingPageContent(
            page = pages[index],
            theme = theme,
            isActive = index == pagerState.currentPage,
            modifier = Modifier.testTag(OnboardingTestTags.page(index)),
        )
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    theme: OnboardingTheme,
    isActive: Boolean,
    modifier: Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val mediaSize = onboardingMediaSize(maxWidth, maxHeight)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))
            OnboardingMedia(
                media = page.media,
                theme = theme,
                isActive = isActive,
                modifier = Modifier
                    .size(mediaSize.width, mediaSize.height)
                    .testTag(OnboardingTestTags.media(page.id)),
            )
            Spacer(Modifier.height(18.dp))
            PageText(page, theme)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PageText(page: OnboardingPage, theme: OnboardingTheme) {
    Text(
        text = page.accentLabel.uppercase(),
        color = theme.warningColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(28.dp))
    Text(
        text = page.title,
        color = theme.primaryTextColor,
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = page.subtitle,
        color = theme.secondaryTextColor,
        fontSize = 16.sp,
        lineHeight = 23.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

internal data class OnboardingMediaSize(val width: Dp, val height: Dp)

internal fun onboardingMediaSize(availableWidth: Dp, availableHeight: Dp): OnboardingMediaSize {
    val safeWidth = availableWidth.coerceAtLeast(0.dp)
    val safeHeight = availableHeight.coerceAtLeast(0.dp)
    val height = (safeHeight * 0.55f)
        .coerceIn(180.dp, 540.dp)
        .coerceAtMost(safeHeight)
    val width = minOf(
        (safeWidth - 32.dp).coerceAtLeast(0.dp),
        height * 0.62f,
        320.dp,
    )
    return OnboardingMediaSize(width = width, height = height)
}

@Composable
private fun OnboardingControls(
    primaryTitle: String,
    skipTitle: String,
    showSkip: Boolean,
    theme: OnboardingTheme,
    onPrimary: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OnboardingButton(primaryTitle, theme, onPrimary)
        if (showSkip) {
            OnboardingSkipButton(skipTitle, theme, onSkip)
        }
    }
}

@Composable
private fun OnboardingButton(
    title: String,
    theme: OnboardingTheme,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.99f else 1f, label = "button scale")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(theme.buttonGradient)
            .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
            )
            .testTag(OnboardingTestTags.primaryButton)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OnboardingSkipButton(
    title: String,
    theme: OnboardingTheme,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .testTag(OnboardingTestTags.skipButton),
        contentAlignment = Alignment.Center,
    ) {
        Text(title, color = theme.secondaryTextColor, fontWeight = FontWeight.SemiBold)
    }
}
