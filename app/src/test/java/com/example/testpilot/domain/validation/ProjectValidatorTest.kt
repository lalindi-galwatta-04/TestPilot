package com.example.testpilot.domain.validation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import com.example.testpilot.domain.validation.ProjectValidator

class ProjectValidatorTest {

    private val validStartDate = 1795219200000L
    private val validEndDate = 1795996800000L

    @Test
    fun validProjectDetails_returnsNull() {

        val result = ProjectValidator.validate(
            projectName = "E-Commerce Testing",
            applicationName = "ShopEasy",
            version = "1.0.0",
            startDate = validStartDate,
            endDate = validEndDate
        )

        assertNull(result)
    }

    @Test
    fun blankProjectName_returnsError() {

        val result = ProjectValidator.validate(
            projectName = "",
            applicationName = "ShopEasy",
            version = "1.0.0",
            startDate = validStartDate,
            endDate = validEndDate
        )

        assertEquals(
            "Project name is required.",
            result
        )
    }

    @Test
    fun shortProjectName_returnsError() {

        val result = ProjectValidator.validate(
            projectName = "QA",
            applicationName = "ShopEasy",
            version = "1.0.0",
            startDate = validStartDate,
            endDate = validEndDate
        )

        assertEquals(
            "Project name must contain at least 3 characters.",
            result
        )
    }

    @Test
    fun blankApplicationName_returnsError() {

        val result = ProjectValidator.validate(
            projectName = "E-Commerce Testing",
            applicationName = "",
            version = "1.0.0",
            startDate = validStartDate,
            endDate = validEndDate
        )

        assertEquals(
            "Application name is required.",
            result
        )
    }

    @Test
    fun blankVersion_returnsError() {

        val result = ProjectValidator.validate(
            projectName = "E-Commerce Testing",
            applicationName = "ShopEasy",
            version = "",
            startDate = validStartDate,
            endDate = validEndDate
        )

        assertEquals(
            "Application version is required.",
            result
        )
    }

    @Test
    fun endDateBeforeStartDate_returnsError() {

        val result = ProjectValidator.validate(
            projectName = "E-Commerce Testing",
            applicationName = "ShopEasy",
            version = "1.0.0",
            startDate = validEndDate,
            endDate = validStartDate
        )

        assertEquals(
            "End date cannot be before start date.",
            result
        )
    }

    @Test
    fun optionalEndDate_returnsNull() {

        val result = ProjectValidator.validate(
            projectName = "E-Commerce Testing",
            applicationName = "ShopEasy",
            version = "1.0.0",
            startDate = validStartDate,
            endDate = null
        )

        assertNull(result)
    }
}