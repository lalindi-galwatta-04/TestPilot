
package com.example.testpilot.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val projectName: String,

    val description: String,

    val applicationName: String,

    val version: String,

    val status: String = "Active",

    val startDate: Long,

    val endDate: Long? = null,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
