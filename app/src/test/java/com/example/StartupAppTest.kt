package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.StartupIdea
import com.example.data.repository.StartupRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StartupAppTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: StartupRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = AppDatabase.getDatabase(context)
        repository = StartupRepository(database)
    }

    @Test
    fun testSeedAndInsertIdea() = runBlocking {
        repository.checkAndSeedInitialData()
        val initialIdeas = repository.allIdeas.first()
        assertTrue("Seed ideas should be present", initialIdeas.isNotEmpty())

        val testIdea = StartupIdea(
            title = "EcoRobo",
            founderName = "Sam Builder",
            tagline = "Autonomous solar weed management",
            problem = "Chemical pesticides deplete soil",
            solution = "Laser robotic weed detection",
            category = "ClimateTech",
            stage = "Pre-Seed",
            fundingAsk = "$100,000",
            lookingFor = "Angel Investor & Friend"
        )
        val insertedId = repository.insertIdea(testIdea)
        assertTrue("Inserted ID should be valid", insertedId > 0)

        val updatedIdeas = repository.allIdeas.first()
        val found = updatedIdeas.find { it.title == "EcoRobo" }
        assertNotNull("Newly inserted idea must be found", found)
        assertEquals("Pre-Seed", found?.stage)
    }

    @Test
    fun testInvestorConnectionAndCoffeeInvite() = runBlocking {
        repository.checkAndSeedInitialData()
        val investors = repository.allInvestors.first()
        assertTrue("Seed investors should be present", investors.isNotEmpty())

        val firstInvestor = investors.first()
        repository.requestConnection(firstInvestor.id)

        val messages = repository.getMessagesForPeer(firstInvestor.id).first()
        assertTrue("A welcome connection message should exist", messages.isNotEmpty())

        repository.scheduleCoffee(firstInvestor.id, "Let's grab a coffee at 2 PM!")
        val updatedMessages = repository.getMessagesForPeer(firstInvestor.id).first()
        val coffeeInvite = updatedMessages.find { it.isCoffeeInvite }
        assertNotNull("Coffee invite message should be recorded", coffeeInvite)
    }
}
