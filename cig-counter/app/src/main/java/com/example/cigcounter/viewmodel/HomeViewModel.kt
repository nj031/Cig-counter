package com.example.cigcounter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cigcounter.data.cigarette.CigaretteEntity
import com.example.cigcounter.data.cigarette.CigaretteRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class TimelineItemUi(
    val id: String,
    val number: Int,
    val timeLabel: String,
    val gapLabel: String,
    val timestamp: Long
)

data class HomeUiState(
    val todayCount: Int = 0,
    val todayDateLabel: String = "",
    val timelineItems: List<TimelineItemUi> = emptyList(),
    val lastSmokedLabel: String? = null,
    val elapsedLabel: String = "00:00:00"
)

class HomeViewModel(private val repository: CigaretteRepository) : ViewModel() {

    private val ticker: Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(1000)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeToday(),
        ticker
    ) { cigarettes, _ -> buildState(cigarettes) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = buildState(emptyList())
        )

    fun onAddCig() {
        viewModelScope.launch {
            repository.addCigarette()
        }
    }

    fun onEditTimestamp(id: String, newTimestamp: Long) {
        viewModelScope.launch {
            repository.updateTimestamp(id, newTimestamp)
        }
    }

    fun onDelete(id: String) {
        viewModelScope.launch {
            repository.softDelete(id)
        }
    }

    private fun buildState(cigarettes: List<CigaretteEntity>): HomeUiState {
        val sorted = cigarettes.sortedBy { it.timestamp }
        val timelineItems = sorted.mapIndexed { index, entity ->
            val gapLabel = if (index == 0) {
                "—"
            } else {
                formatGap(entity.timestamp - sorted[index - 1].timestamp)
            }
            TimelineItemUi(
                id = entity.id,
                number = index + 1,
                timeLabel = formatTime(entity.timestamp),
                gapLabel = gapLabel,
                timestamp = entity.timestamp
            )
        }
        val latest = sorted.lastOrNull()
        val now = System.currentTimeMillis()
        return HomeUiState(
            todayCount = sorted.size,
            todayDateLabel = formatDate(now),
            timelineItems = timelineItems,
            lastSmokedLabel = latest?.let { formatTime(it.timestamp) },
            elapsedLabel = latest?.let { formatElapsed(now - it.timestamp) } ?: "00:00:00"
        )
    }

    class Factory(private val repository: CigaretteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }

    companion object {
        private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        private val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())

        fun formatTime(timestamp: Long): String =
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(timeFormatter)

        fun formatDate(timestamp: Long): String =
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(dateFormatter)

        fun formatElapsed(millis: Long): String {
            val totalSeconds = (millis / 1000).coerceAtLeast(0)
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        }

        fun formatGap(millis: Long): String {
            val totalMinutes = (millis / 60000).coerceAtLeast(0)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            return if (hours > 0) {
                "${hours}h ${minutes.toString().padStart(2, '0')}m"
            } else {
                "${minutes}m"
            }
        }
    }
}
