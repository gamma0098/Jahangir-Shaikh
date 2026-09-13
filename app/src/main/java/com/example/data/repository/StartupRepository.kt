package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.Investor
import com.example.data.model.StartupIdea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StartupRepository(private val database: AppDatabase) {
    private val ideaDao = database.startupIdeaDao()
    private val investorDao = database.investorDao()
    private val chatDao = database.chatDao()

    val allIdeas: Flow<List<StartupIdea>> = ideaDao.getAllIdeas()
    val bookmarkedIdeas: Flow<List<StartupIdea>> = ideaDao.getBookmarkedIdeas()
    val allInvestors: Flow<List<Investor>> = investorDao.getAllInvestors()
    val allMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages()

    fun getMessagesForPeer(peerId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForPeer(peerId)
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        if (ideaDao.getCount() == 0) {
            ideaDao.insertAll(getSeedIdeas())
        }
        if (investorDao.getCount() == 0) {
            investorDao.insertAll(getSeedInvestors())
        }
        if (chatDao.getCount() == 0) {
            chatDao.insertAll(getSeedChats())
        }
    }

    suspend fun insertIdea(idea: StartupIdea): Long = withContext(Dispatchers.IO) {
        ideaDao.insertIdea(idea)
    }

    suspend fun toggleLike(id: Long, currentlyLiked: Boolean) = withContext(Dispatchers.IO) {
        val newLiked = !currentlyLiked
        val delta = if (newLiked) 1 else -1
        ideaDao.toggleLike(id, newLiked, delta)
    }

    suspend fun toggleBookmark(id: Long, currentlyBookmarked: Boolean) = withContext(Dispatchers.IO) {
        ideaDao.toggleBookmark(id, !currentlyBookmarked)
    }

    suspend fun deleteIdea(id: Long) = withContext(Dispatchers.IO) {
        ideaDao.deleteIdea(id)
    }

    suspend fun requestConnection(investorId: Long) = withContext(Dispatchers.IO) {
        val investor = investorDao.getInvestorById(investorId) ?: return@withContext
        investorDao.updateConnectionStatus(investorId, "connected", true)

        // Add welcoming friendly message from investor
        val replyText = "Hey there! Thanks for reaching out. I love connecting with ambitious founders. Let's exchange thoughts on your startup or grab a coffee whenever you're ready! ☕"
        chatDao.insertMessage(
            ChatMessage(
                peerId = investorId,
                peerName = investor.name,
                isFromMe = false,
                message = replyText,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun scheduleCoffee(investorId: Long, customNote: String) = withContext(Dispatchers.IO) {
        val investor = investorDao.getInvestorById(investorId) ?: return@withContext
        investorDao.updateConnectionStatus(investorId, "coffee_scheduled", true)

        // Insert coffee invite message
        chatDao.insertMessage(
            ChatMessage(
                peerId = investorId,
                peerName = investor.name,
                isFromMe = true,
                message = customNote.ifBlank { "Hey ${investor.name}! Would love to grab a virtual coffee and share what we're building. ☕" },
                timestamp = System.currentTimeMillis(),
                isCoffeeInvite = true,
                inviteAccepted = true
            )
        )

        // Simulated friendly investor acceptance response
        chatDao.insertMessage(
            ChatMessage(
                peerId = investorId,
                peerName = investor.name,
                isFromMe = false,
                message = "That sounds awesome! I'd be happy to share notes and be your sounding board. My favorite is ${investor.favoriteCoffee}. Let's make it happen!",
                timestamp = System.currentTimeMillis() + 1000,
                isCoffeeInvite = false
            )
        )
    }

    suspend fun sendMessage(peerId: Long, peerName: String, text: String, isCoffee: Boolean = false) = withContext(Dispatchers.IO) {
        chatDao.insertMessage(
            ChatMessage(
                peerId = peerId,
                peerName = peerName,
                isFromMe = true,
                message = text,
                timestamp = System.currentTimeMillis(),
                isCoffeeInvite = isCoffee
            )
        )
    }

    suspend fun acceptCoffeeInvite(messageId: Long) = withContext(Dispatchers.IO) {
        chatDao.acceptCoffeeInvite(messageId)
    }

    private fun getSeedIdeas(): List<StartupIdea> = listOf(
        StartupIdea(
            title = "AetherGrid",
            founderName = "Elena Vance",
            founderRole = "Founder & AI Engineer",
            tagline = "Autonomous decentralized microgrid balancing powered by edge AI.",
            problem = "Renewable energy grids suffer from 28% peak curtailment and fragile battery dispatch scheduling.",
            solution = "A lightweight reinforcement learning controller that runs on community battery inverters, optimizing dynamic load sharing locally.",
            category = "ClimateTech",
            stage = "Pre-Seed",
            fundingAsk = "$200,000",
            lookingFor = "Angel Backer & CleanTech Mentor",
            traction = "Pilot running in 3 microgrid communities, 40MWh energy redistributed",
            friendlyNote = "Searching for an investor who shares our obsession for green energy and honest founder dialogue.",
            likesCount = 34,
            isLiked = false,
            isBookmarked = false,
            createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        StartupIdea(
            title = "PulseCompanion",
            founderName = "David Kalu",
            founderRole = "Co-Founder & Cardiologist",
            tagline = "Non-invasive early cardiovascular anomaly prediction for everyday smart wearables.",
            problem = "Over 60% of arrhythmias are detected too late, leading to emergency interventions.",
            solution = "Proprietary photoplethysmogram wave pattern recognition model trained on 100k+ clinical hours, running entirely on-device for privacy.",
            category = "HealthTech",
            stage = "Seed",
            fundingAsk = "$500,000",
            lookingFor = "Healthcare Investor & Strategic Advisor",
            traction = "IRB approval granted, 94.2% sensitivity across 800 patient test sessions",
            friendlyNote = "We value long-term partnership, patient impact, and candid advisory feedback.",
            likesCount = 52,
            isLiked = true,
            isBookmarked = true,
            createdAt = System.currentTimeMillis() - 86400000L * 4
        ),
        StartupIdea(
            title = "OmniFlow",
            founderName = "Maya Lin",
            founderRole = "Founder & Product Architect",
            tagline = "Zero-overhead semantic search & knowledge graphs for remote engineering squads.",
            problem = "Engineers waste 7 hours weekly searching through fragmented Slack, Notion, Jira, and GitHub threads.",
            solution = "Instant federated context retrieval indexing code reviews, PR discussions, and architecture docs into interactive graphs.",
            category = "B2B SaaS",
            stage = "Prototype",
            fundingAsk = "$150,000",
            lookingFor = "Developer-tool Angel & Good Friend",
            traction = "18 beta teams onboarded, 4.8/5 CSAT score, 45 daily active dev queries per seat",
            friendlyNote = "Always down for a quick virtual coffee! Looking for someone who gets developer tools.",
            likesCount = 27,
            isLiked = false,
            isBookmarked = false,
            createdAt = System.currentTimeMillis() - 86400000L * 1
        ),
        StartupIdea(
            title = "NanoHarvest",
            founderName = "Mateo Silva",
            founderRole = "Agronomist & Roboticist",
            tagline = "Solar-powered micro-weed elimination drones with optical precision targeting.",
            problem = "Herbicide runoff ruins topsoil quality and farmers spend up to $60/acre on manual crop labor.",
            solution = "Ultra-compact wheeled rovers equipped with multispectral cameras that vaporize invasive weeds with pinpoint lasers, zero chemicals.",
            category = "AgriTech",
            stage = "Idea",
            fundingAsk = "Mentorship & Angel Guidance",
            lookingFor = "Hardware Angel & Friendly Advisory",
            traction = "Bench-scale prototype built, validated on 2 test organic vineyards in California",
            friendlyNote = "Looking for an investor friend who loves hardware and sustainable farming.",
            likesCount = 41,
            isLiked = false,
            isBookmarked = false,
            createdAt = System.currentTimeMillis() - 86400000L * 5
        )
    )

    private fun getSeedInvestors(): List<Investor> = listOf(
        Investor(
            name = "Marcus Chen",
            title = "Angel Investor & 2x Exited Founder",
            firm = "Chen Ventures / Angel",
            focusSectors = "AI & ML, B2B SaaS, Developer Tools",
            checkSize = "$25k - $100k",
            bio = "Built and sold two cloud-infrastructure startups. Passionate about helping first-time founders navigate early product-market fit and staying grounded.",
            friendlyPhilosophy = "I invest in human beings, not just slide decks. I'm the investor friend you can call at 10 PM when things get tough.",
            favoriteCoffee = "Oat Cortado ☕",
            location = "San Francisco, CA",
            isConnected = true,
            connectionStatus = "connected"
        ),
        Investor(
            name = "Sarah Jenkins",
            title = "General Partner",
            firm = "Apex Climate Fund",
            focusSectors = "ClimateTech, Clean Energy, Circular Economy",
            checkSize = "$100k - $500k",
            bio = "12+ years in venture capital and energy transition policy. Strong advocate for scalable hardware solutions tackling the climate crisis.",
            friendlyPhilosophy = "Founder mental wellness and transparent collaboration matter as much as monthly recurring revenue.",
            favoriteCoffee = "Matcha Latte 🍵",
            location = "New York, NY",
            isConnected = false,
            connectionStatus = "none"
        ),
        Investor(
            name = "Liam O'Connor",
            title = "Seed Angel & Community Builder",
            firm = "FounderFriend Syndicate",
            focusSectors = "FinTech, Web3, Future of Work",
            checkSize = "$10k - $50k",
            bio = "Early operator at Stripe and AngelList. Runs a supportive angel community dedicated to friendly founder mentorship and transparent feedback.",
            friendlyPhilosophy = "A warm coffee and an honest brainstorm are worth 100 formal pitch meetings. Let's be friends first.",
            favoriteCoffee = "Cold Brew with Vanilla ☕",
            location = "Austin, TX & Remote",
            isConnected = false,
            connectionStatus = "none"
        ),
        Investor(
            name = "Dr. Aris Thorne",
            title = "DeepTech & Bio Angel",
            firm = "Thorne BioSciences Capital",
            focusSectors = "HealthTech, BioTech, Medical Devices",
            checkSize = "$50k - $250k",
            bio = "Physician turned health-tech venture partner. Backed 15+ FDA-approved digital health diagnostic companies.",
            friendlyPhilosophy = "High conviction, high empathy. I help medical founders speak the language of sustainable businesses.",
            favoriteCoffee = "Pour-over Ethiopian Roast ☕",
            location = "Boston, MA",
            isConnected = false,
            connectionStatus = "none"
        )
    )

    private fun getSeedChats(): List<ChatMessage> = listOf(
        ChatMessage(
            peerId = 1,
            peerName = "Marcus Chen",
            isFromMe = false,
            message = "Hi! Welcome to the network. I reviewed your startup profile and loved the product focus. Would love to grab a coffee or jump on a quick call!",
            timestamp = System.currentTimeMillis() - 3600000L * 3
        ),
        ChatMessage(
            peerId = 1,
            peerName = "Marcus Chen",
            isFromMe = true,
            message = "Hey Marcus! Thanks so much for reaching out. We'd love to chat about our roadmap and get your thoughts on early sales channels.",
            timestamp = System.currentTimeMillis() - 3600000L * 2
        ),
        ChatMessage(
            peerId = 1,
            peerName = "Marcus Chen",
            isFromMe = false,
            message = "Sounds like a plan. Count me in for that virtual coffee ☕! Keep up the momentum.",
            timestamp = System.currentTimeMillis() - 3600000L * 1
        )
    )
}
