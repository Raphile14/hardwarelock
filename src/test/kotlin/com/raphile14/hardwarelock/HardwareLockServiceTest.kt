package com.raphile14.hardwarelock

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class HardwareLockServiceTest {
    @Test
    fun getCpuSerialNumber_ShouldBeSuccessful() {
        // When
        val result = HardwareLockService.getCpuSerialNumber()

        // Then
        assertNotNull(result)
    }

    @Test
    fun getMachineId_ShouldBeSuccessful() {
        // When
        val result = HardwareLockService.getMachineId()

        // Then
        assertNotNull(result)
    }

    @Test
    fun getMacAddress_ShouldBeSuccessful() {
        // When
        val result = HardwareLockService.getMacAddress()

        // Then
        assertNotNull(result)
    }

    @Test
    fun hashIds_ShouldBeSuccessful() {
        // When
        val result = HardwareLockService.hashIds("test")

        // Then
        assertNotNull(result)
    }
}