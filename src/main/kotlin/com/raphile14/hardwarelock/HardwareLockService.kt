package com.raphile14.hardwarelock

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.NetworkInterface
import java.security.MessageDigest
import java.util.*

class HardwareLockService {
    companion object {
        @JvmStatic
        fun getCpuSerialNumber(): String? {
            return try {
                val os = System.getProperty("os.name").lowercase(Locale.getDefault())

                if (os.contains("win")) {
                    // Windows - Run WMIC command to get CPU serial
                    val process = ProcessBuilder("cmd", "/C", "wmic cpu get ProcessorId").start()
                    BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                        reader.lineSequence()
                            .filter { it.isNotBlank() }
                            .drop(1)
                            .firstOrNull()
                            ?.trim()
                    }
                } else {
                    // Raspberry Pi - Read from /proc/cpuinfo
                    val process = ProcessBuilder("cat", "/proc/cpuinfo").start()
                    BufferedReader(InputStreamReader(process.inputStream)).useLines { lines ->
                        lines.firstOrNull { it.startsWith("Serial") }?.split(":")?.get(1)?.trim()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun getMachineId(): String? {
            return try {
                val os = System.getProperty("os.name").lowercase(Locale.getDefault())

                if (os.contains("win")) {
                    // Windows - Use PowerShell command to get Machine GUID from registry
                    val process = ProcessBuilder(
                        "powershell", "Get-ItemProperty",
                        "-Path", "HKLM:\\SOFTWARE\\Microsoft\\Cryptography",
                        "-Name", "MachineGuid"
                    ).start()
                    BufferedReader(InputStreamReader(process.inputStream)).useLines { lines ->
                        lines.firstOrNull { it.contains("MachineGuid") }?.split(":")?.get(1)?.trim()
                    }
                } else {
                    // Linux/Raspberry Pi - Read from /etc/machine-id
                    val machineId = try {
                        File("/etc/machine-id").readText().trim()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }

                    machineId
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun getMacAddress(): String? {
            return try {
                val os = System.getProperty("os.name").lowercase(Locale.getDefault())
                var address: String? = null

                if (os.contains("win")) {
                    val networkInterfaces = NetworkInterface.getNetworkInterfaces().toList()
                    for (networkInterface in networkInterfaces) {
                        // Skip loopback and down interfaces
                        if (networkInterface.isLoopback || !networkInterface.isUp) continue
                        val macBytes = networkInterface.hardwareAddress ?: continue
                        // Convert MAC address bytes to hex string
                        address = macBytes.joinToString(":") { "%02X".format(it) }
                        break
                    }
                } else {
                    try {
                        address = File("/sys/class/net/eth0/address").readText().trim()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                address
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun hashIds(content: String, algorithm: String = "SHA-256"): String {
            val digest = MessageDigest.getInstance(algorithm)
            val hashedBytes = digest.digest(content.toByteArray())
            return hashedBytes.joinToString("") { "%02x".format(it) }
        }

        @JvmStatic
        fun validate(key: String, delimiter: String = "//") {
            val cpuSerialNumber = getCpuSerialNumber()
            val machineId = getMachineId()
            val macAddress = getMacAddress()
            val content = "$cpuSerialNumber $delimiter $macAddress $delimiter $machineId"
            val hashIds = hashIds(
                content = content
            )

            if (hashIds != key) {
                throw RuntimeException("Invalid session")
            }
        }
    }
}