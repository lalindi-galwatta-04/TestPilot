
package com.example.testpilot.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "test_cases",
    foreignKeys = [
        ForeignKey(
            entity = TestSuiteEntity::class,
            parentColumns = ["id"],
            childColumns = ["suiteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("suiteId")]
)
data class TestCaseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val suiteId: Int,

    val title: String,

    val description: String = "",

    val preconditions: String = "",

    val priority: String = "Medium",

    val testType: String = "Functional",

    val status: String = "Draft",

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
