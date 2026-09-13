package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.Investor
import com.example.data.model.StartupIdea
import com.example.data.repository.StartupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AiFeedbackResult(
    val title: String,
    val overallScore: Int, // e.g. 92%
    val investorHook: String,
    val strengths: List<String>,
    val friendQuestionsToPrepare: List<String>,
    val warmEncouragement: String
)

class StartupViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = StartupRepository(database)

    // Current navigation tab: 0=Ideas Feed, 1=Investors, 2=Friend Chats, 3=My Profile
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Current user persona: "Founder" or "Investor"
    private val _currentUserRole = MutableStateFlow("Founder")
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    // Filtering & search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedStage = MutableStateFlow("All")
    val selectedStage: StateFlow<String> = _selectedStage.asStateFlow()

    // Modal and sheet states
    private val _showShareIdeaSheet = MutableStateFlow(false)
    val showShareIdeaSheet: StateFlow<Boolean> = _showShareIdeaSheet.asStateFlow()

    private val _selectedIdeaDetails = MutableStateFlow<StartupIdea?>(null)
    val selectedIdeaDetails: StateFlow<StartupIdea?> = _selectedIdeaDetails.asStateFlow()

    private val _activeChatPeer = MutableStateFlow<Investor?>(null)
    val activeChatPeer: StateFlow<Investor?> = _activeChatPeer.asStateFlow()

    private val _coffeeInvitePeer = MutableStateFlow<Investor?>(null)
    val coffeeInvitePeer: StateFlow<Investor?> = _coffeeInvitePeer.asStateFlow()

    // AI Pitch Coach feedback state
    private val _aiReviewResult = MutableStateFlow<AiFeedbackResult?>(null)
    val aiReviewResult: StateFlow<AiFeedbackResult?> = _aiReviewResult.asStateFlow()

    private val _isAiReviewLoading = MutableStateFlow(false)
    val isAiReviewLoading: StateFlow<Boolean> = _isAiReviewLoading.asStateFlow()

    // Repository flows
    val rawIdeas: StateFlow<List<StartupIdea>> = repository.allIdeas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawInvestors: StateFlow<List<Investor>> = repository.allInvestors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedIdeas: StateFlow<List<StartupIdea>> = repository.bookmarkedIdeas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered ideas state
    val filteredIdeas: StateFlow<List<StartupIdea>> = combine(
        rawIdeas,
        _searchQuery,
        _selectedCategory,
        _selectedStage
    ) { ideas, query, category, stage ->
        ideas.filter { idea ->
            val matchesQuery = query.isBlank() ||
                    idea.title.contains(query, ignoreCase = true) ||
                    idea.tagline.contains(query, ignoreCase = true) ||
                    idea.problem.contains(query, ignoreCase = true) ||
                    idea.solution.contains(query, ignoreCase = true) ||
                    idea.category.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || idea.category.equals(category, ignoreCase = true)
            val matchesStage = stage == "All" || idea.stage.equals(stage, ignoreCase = true)

            matchesQuery && matchesCategory && matchesStage
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkAndSeedInitialData()
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setRole(role: String) {
        _currentUserRole.value = role
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedStage(stage: String) {
        _selectedStage.value = stage
    }

    fun setShowShareIdeaSheet(show: Boolean) {
        _showShareIdeaSheet.value = show
    }

    fun selectIdeaForDetails(idea: StartupIdea?) {
        _selectedIdeaDetails.value = idea
    }

    fun openChat(investor: Investor) {
        _activeChatPeer.value = investor
    }

    fun closeChat() {
        _activeChatPeer.value = null
    }

    fun openCoffeeInvite(investor: Investor) {
        _coffeeInvitePeer.value = investor
    }

    fun closeCoffeeInvite() {
        _coffeeInvitePeer.value = null
    }

    fun toggleLike(idea: StartupIdea) {
        viewModelScope.launch {
            repository.toggleLike(idea.id, idea.isLiked)
        }
    }

    fun toggleBookmark(idea: StartupIdea) {
        viewModelScope.launch {
            repository.toggleBookmark(idea.id, idea.isBookmarked)
        }
    }

    fun createIdea(
        title: String,
        tagline: String,
        problem: String,
        solution: String,
        category: String,
        stage: String,
        fundingAsk: String,
        lookingFor: String,
        friendlyNote: String
    ) {
        viewModelScope.launch {
            val newIdea = StartupIdea(
                title = title.trim(),
                founderName = if (_currentUserRole.value == "Founder") "You (Founder)" else "Partner Idea",
                tagline = tagline.trim(),
                problem = problem.trim(),
                solution = solution.trim(),
                category = category,
                stage = stage,
                fundingAsk = fundingAsk.ifBlank { "Advisory & Mentorship" },
                lookingFor = lookingFor.ifBlank { "Angel Investment & Friendly Advisor" },
                friendlyNote = friendlyNote.ifBlank { "Excited to meet passionate investors for friendly coffee chats!" },
                likesCount = 1
            )
            repository.insertIdea(newIdea)
            _showShareIdeaSheet.value = false
        }
    }

    fun deleteIdea(id: Long) {
        viewModelScope.launch {
            repository.deleteIdea(id)
            if (_selectedIdeaDetails.value?.id == id) {
                _selectedIdeaDetails.value = null
            }
        }
    }

    fun connectWithInvestor(investor: Investor) {
        viewModelScope.launch {
            repository.requestConnection(investor.id)
        }
    }

    fun confirmCoffeeInvite(investor: Investor, customNote: String) {
        viewModelScope.launch {
            repository.scheduleCoffee(investor.id, customNote)
            _coffeeInvitePeer.value = null
        }
    }

    fun sendChatMessage(peerId: Long, peerName: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(peerId, peerName, text)
        }
    }

    fun getMessagesForPeer(peerId: Long) = repository.getMessagesForPeer(peerId)

    fun requestAiPitchCoach(idea: StartupIdea) {
        viewModelScope.launch {
            _isAiReviewLoading.value = true
            _aiReviewResult.value = null
            delay(1200) // Realistic thoughtful review delay

            val strengthsList = mutableListOf(
                "High clarity on the pain point: solving real friction with direct measurable impact.",
                "Compelling founder-market fit with clear technical or domain passion.",
                "Strong appeal for mission-driven investors who value sustainable growth over vanity metrics."
            )

            val questionsList = mutableListOf(
                "How do you plan to acquire your first 50 early adopters without paid advertising?",
                "What is your moat when larger incumbents observe this category?",
                "What kind of non-financial advice or friendship do you most hope an investor brings?"
            )

            val feedback = AiFeedbackResult(
                title = "Friendly Pitch Feedback: ${idea.title}",
                overallScore = (88..96).random(),
                investorHook = "Your hook '${idea.tagline}' clearly articulates who you serve and the breakthrough insight.",
                strengths = strengthsList,
                friendQuestionsToPrepare = questionsList,
                warmEncouragement = "Investors invest in founders they like, trust, and can be friends with for 5-10 years. Share your genuine passion and don't be afraid to show vulnerability about what you are still learning!"
            )
            _aiReviewResult.value = feedback
            _isAiReviewLoading.value = false
        }
    }

    fun dismissAiReview() {
        _aiReviewResult.value = null
    }
}
