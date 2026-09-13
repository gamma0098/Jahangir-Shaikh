package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiPitchReviewDialog
import com.example.ui.components.ChatDialog
import com.example.ui.components.CoffeeInviteDialog
import com.example.ui.components.IdeaDetailDialog
import com.example.ui.components.ShareIdeaSheet
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.IdeasFeedScreen
import com.example.ui.screens.InvestorsScreen
import com.example.ui.screens.ProfileScreen
import com.example.viewmodel.StartupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: StartupViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val ideas by viewModel.filteredIdeas.collectAsStateWithLifecycle()
    val allIdeas by viewModel.rawIdeas.collectAsStateWithLifecycle()
    val bookmarkedIdeas by viewModel.bookmarkedIdeas.collectAsStateWithLifecycle()
    val investors by viewModel.rawInvestors.collectAsStateWithLifecycle()
    val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
    val currentUserRole by viewModel.currentUserRole.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedStage by viewModel.selectedStage.collectAsStateWithLifecycle()

    val showShareSheet by viewModel.showShareIdeaSheet.collectAsStateWithLifecycle()
    val selectedIdeaForDetails by viewModel.selectedIdeaDetails.collectAsStateWithLifecycle()
    val activeChatPeer by viewModel.activeChatPeer.collectAsStateWithLifecycle()
    val coffeeInvitePeer by viewModel.coffeeInvitePeer.collectAsStateWithLifecycle()

    val aiReviewResult by viewModel.aiReviewResult.collectAsStateWithLifecycle()
    val isAiReviewLoading by viewModel.isAiReviewLoading.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val connectedCount = investors.count { it.isConnected }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.RocketLaunch else Icons.Outlined.RocketLaunch,
                            contentDescription = "Startup Ideas"
                        )
                    },
                    label = { Text("Ideas", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_ideas")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.Handshake else Icons.Outlined.Handshake,
                            contentDescription = "Investors"
                        )
                    },
                    label = { Text("Investors", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_investors")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Coffee Chats"
                        )
                    },
                    label = { Text("Chats & Coffee", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_chats")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_profile")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> IdeasFeedScreen(
                    ideas = ideas,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    selectedCategory = selectedCategory,
                    onSelectCategory = { viewModel.setSelectedCategory(it) },
                    selectedStage = selectedStage,
                    onSelectStage = { viewModel.setSelectedStage(it) },
                    onCardClick = { viewModel.selectIdeaForDetails(it) },
                    onLikeClick = { viewModel.toggleLike(it) },
                    onBookmarkClick = { viewModel.toggleBookmark(it) },
                    onAiCoachClick = { viewModel.requestAiPitchCoach(it) },
                    onShareIdeaClick = { viewModel.setShowShareIdeaSheet(true) }
                )

                1 -> InvestorsScreen(
                    investors = investors,
                    onConnectClick = { viewModel.connectWithInvestor(it) },
                    onCoffeeClick = { viewModel.openCoffeeInvite(it) },
                    onChatClick = { viewModel.openChat(it) }
                )

                2 -> ChatsScreen(
                    investors = investors,
                    allMessages = allMessages,
                    onOpenChat = { viewModel.openChat(it) }
                )

                3 -> ProfileScreen(
                    currentRole = currentUserRole,
                    onRoleChange = { viewModel.setRole(it) },
                    bookmarkedIdeas = bookmarkedIdeas,
                    allIdeas = allIdeas,
                    connectedInvestorsCount = connectedCount,
                    onIdeaClick = { viewModel.selectIdeaForDetails(it) },
                    onLikeClick = { viewModel.toggleLike(it) },
                    onBookmarkClick = { viewModel.toggleBookmark(it) },
                    onAiCoachClick = { viewModel.requestAiPitchCoach(it) },
                    onShareIdeaClick = { viewModel.setShowShareIdeaSheet(true) }
                )
            }
        }
    }

    // Share Idea Modal Bottom Sheet
    if (showShareSheet) {
        ShareIdeaSheet(
            sheetState = sheetState,
            onDismiss = { viewModel.setShowShareIdeaSheet(false) },
            onSubmit = { title, tagline, problem, solution, category, stage, fundingAsk, lookingFor, friendlyNote ->
                viewModel.createIdea(
                    title = title,
                    tagline = tagline,
                    problem = problem,
                    solution = solution,
                    category = category,
                    stage = stage,
                    fundingAsk = fundingAsk,
                    lookingFor = lookingFor,
                    friendlyNote = friendlyNote
                )
            }
        )
    }

    // Idea Detail Dialog
    selectedIdeaForDetails?.let { idea ->
        IdeaDetailDialog(
            idea = idea,
            onDismiss = { viewModel.selectIdeaForDetails(null) },
            onLikeClick = { viewModel.toggleLike(idea) },
            onBookmarkClick = { viewModel.toggleBookmark(idea) },
            onAiCoachClick = { viewModel.requestAiPitchCoach(idea) },
            onDeleteClick = if (idea.founderName.contains("You")) {
                { viewModel.deleteIdea(idea.id) }
            } else null
        )
    }

    // Coffee Invite Dialog
    coffeeInvitePeer?.let { investor ->
        CoffeeInviteDialog(
            investor = investor,
            onDismiss = { viewModel.closeCoffeeInvite() },
            onConfirmInvite = { note ->
                viewModel.confirmCoffeeInvite(investor, note)
            }
        )
    }

    // Chat Dialog
    activeChatPeer?.let { investor ->
        val peerMessages by viewModel.getMessagesForPeer(investor.id).collectAsStateWithLifecycle(emptyList())
        ChatDialog(
            investor = investor,
            messages = peerMessages,
            onSendMessage = { text ->
                viewModel.sendChatMessage(investor.id, investor.name, text)
            },
            onClose = { viewModel.closeChat() }
        )
    }

    // AI Pitch Review Dialog
    if (isAiReviewLoading || aiReviewResult != null) {
        AiPitchReviewDialog(
            isLoading = isAiReviewLoading,
            result = aiReviewResult,
            onDismiss = { viewModel.dismissAiReview() }
        )
    }
}
