package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShareIdeaSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        tagline: String,
        problem: String,
        solution: String,
        category: String,
        stage: String,
        fundingAsk: String,
        lookingFor: String,
        friendlyNote: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var tagline by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("AI & ML") }
    var selectedStage by remember { mutableStateOf("Pre-Seed") }
    var fundingAsk by remember { mutableStateOf("$100,000") }
    var lookingFor by remember { mutableStateOf("Angel Investment & Friendly Advisor") }
    var friendlyNote by remember { mutableStateOf("Looking forward to casual coffee chats and genuine founder-investor dialogue!") }

    val categories = listOf("AI & ML", "ClimateTech", "HealthTech", "FinTech", "B2B SaaS", "AgriTech", "Consumer")
    val stages = listOf("Idea", "Prototype", "Pre-Seed", "Seed")

    val isFormValid = title.isNotBlank() && tagline.isNotBlank() && problem.isNotBlank() && solution.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Share Startup Idea & Pitch",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }

            Text(
                text = "Present your vision to angel investors and friendly mentors ready to back passionate builders.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Startup Name
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Startup / Idea Name") },
                placeholder = { Text("e.g. TerraVolt, BioGuard") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_idea_title"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = { Text("One-Sentence Pitch (Elevator Hook)") },
                placeholder = { Text("What makes your company unique in one line?") },
                singleLine = false,
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_idea_tagline"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Problem
            OutlinedTextField(
                value = problem,
                onValueChange = { problem = it },
                label = { Text("The Big Problem") },
                placeholder = { Text("What pain point are customers experiencing today?") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_idea_problem"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Solution
            OutlinedTextField(
                value = solution,
                onValueChange = { solution = it },
                label = { Text("Your Breakthrough Solution") },
                placeholder = { Text("How does your product uniquely solve this problem?") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_idea_solution"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sector / Category
            Text(
                text = "Industry Sector",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stage
            Text(
                text = "Current Stage",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stages.forEach { st ->
                    FilterChip(
                        selected = selectedStage == st,
                        onClick = { selectedStage = st },
                        label = { Text(st, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Funding Ask
            OutlinedTextField(
                value = fundingAsk,
                onValueChange = { fundingAsk = it },
                label = { Text("Funding Ask") },
                placeholder = { Text("e.g. $150,000 or Mentorship & Advisory") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_idea_funding"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Looking For
            OutlinedTextField(
                value = lookingFor,
                onValueChange = { lookingFor = it },
                label = { Text("What are you looking for?") },
                placeholder = { Text("e.g. Angel Backer & Friendly Mentor, Co-Founder") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_idea_looking_for"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Friendly Note
            OutlinedTextField(
                value = friendlyNote,
                onValueChange = { friendlyNote = it },
                label = { Text("Friendly Note for Investors & Friends ☕") },
                placeholder = { Text("Share your vibe or what you'd love to discuss over coffee") },
                minLines = 2,
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_idea_friendly_note"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    if (isFormValid) {
                        onSubmit(
                            title,
                            tagline,
                            problem,
                            solution,
                            selectedCategory,
                            selectedStage,
                            fundingAsk,
                            lookingFor,
                            friendlyNote
                        )
                    }
                },
                enabled = isFormValid,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_publish_idea_button")
            ) {
                Icon(imageVector = Icons.Filled.RocketLaunch, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Idea with Investors",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
