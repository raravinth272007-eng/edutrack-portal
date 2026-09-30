package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.entity.DonorEntity
import com.example.model.BloodGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LifeLink", appName)
    }

    @Test
    fun `test blood compatibility rules`() {
        // Universal donor O- can donate to all
        BloodGroup.entries.forEach { recipient ->
            assertTrue("O- must be able to donate to ${recipient.label}", BloodGroup.O_NEG.canDonateRbcTo(recipient))
        }

        // AB+ is universal recipient
        BloodGroup.entries.forEach { donor ->
            assertTrue("AB+ must be able to receive from ${donor.label}", BloodGroup.AB_POS.canReceiveRbcFrom(donor))
        }

        // A- cannot donate to B+
        assertFalse(BloodGroup.A_NEG.canDonateRbcTo(BloodGroup.B_POS))
        // B+ can only donate to B+ and AB+
        assertTrue(BloodGroup.B_POS.canDonateRbcTo(BloodGroup.B_POS))
        assertTrue(BloodGroup.B_POS.canDonateRbcTo(BloodGroup.AB_POS))
        assertFalse(BloodGroup.B_POS.canDonateRbcTo(BloodGroup.O_POS))
    }

    @Test
    fun `test 90 day donor eligibility cooldown`() {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24L * 60L * 60L * 1000L

        // Donor who donated 30 days ago should be ineligible (60 days left)
        val recentDonor = DonorEntity(
            id = "d1",
            name = "Test Donor",
            age = 30,
            bloodGroup = "A+",
            city = "Central",
            phone = "1234567890",
            lastDonationDateMillis = now - (30L * oneDayMillis)
        )
        assertFalse("Donor who donated 30 days ago must be ineligible", recentDonor.isEligible(now))
        assertEquals(60, recentDonor.daysUntilEligible(now))

        // Donor who donated 100 days ago should be eligible (0 days left)
        val eligibleDonor = DonorEntity(
            id = "d2",
            name = "Ready Donor",
            age = 28,
            bloodGroup = "O+",
            city = "Central",
            phone = "1234567890",
            lastDonationDateMillis = now - (100L * oneDayMillis)
        )
        assertTrue("Donor who donated 100 days ago must be eligible", eligibleDonor.isEligible(now))
        assertEquals(0, eligibleDonor.daysUntilEligible(now))

        // First time donor (lastDonationDateMillis = 0)
        val firstTimeDonor = DonorEntity(
            id = "d3",
            name = "New Donor",
            age = 21,
            bloodGroup = "B+",
            city = "Central",
            phone = "1234567890",
            lastDonationDateMillis = 0L
        )
        assertTrue("First time donor must be eligible", firstTimeDonor.isEligible(now))
        assertEquals(0, firstTimeDonor.daysUntilEligible(now))
    }
}
