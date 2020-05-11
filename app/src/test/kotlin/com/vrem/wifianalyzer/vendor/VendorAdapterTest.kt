/*
 * WiFiAnalyzer
 * Copyright (C) 2015 - 2020 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.vrem.wifianalyzer.vendor

import android.os.Build
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.whenever
import com.vrem.wifianalyzer.MainActivity
import com.vrem.wifianalyzer.MainContextHelper
import com.vrem.wifianalyzer.R
import com.vrem.wifianalyzer.RobolectricUtil
import com.vrem.wifianalyzer.vendor.model.VendorService
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@LooperMode(LooperMode.Mode.PAUSED)
class VendorAdapterTest {
    private val vendorName1 = "N1"
    private val vendorName2 = "N2"
    private val vendorName3 = "N3"

    private lateinit var fixture: VendorAdapter

    private val mainActivity: MainActivity = RobolectricUtil.INSTANCE.activity
    private val vendorService: VendorService = MainContextHelper.INSTANCE.vendorService
    private val vendors: List<String> = listOf(vendorName1, vendorName2, vendorName3)

    @Before
    fun setUp() {
        whenever(vendorService.findVendors()).thenReturn(vendors)
        fixture = VendorAdapter(mainActivity, vendorService)
    }

    @After
    fun tearDown() {
        MainContextHelper.INSTANCE.restore()
    }

    @Test
    fun testConstructor() {
        // validate
        assertEquals(vendors.size, fixture.count)
        assertEquals(vendors[0], fixture.getItem(0))
        assertEquals(vendors[1], fixture.getItem(1))
        assertEquals(vendors[2], fixture.getItem(2))
        verify(vendorService).findVendors()
    }

    @Test
    fun testGetView() {
        // setup
        whenever(vendorService.findMacAddresses(vendorName2)).thenReturn(listOf("VALUE1", "VALUE2", "VALUE3"))
        val expected = "VALUE1, VALUE2, VALUE3"
        val viewGroup = mainActivity.findViewById<ViewGroup>(android.R.id.content)
        // execute
        val actual = fixture.getView(1, null, viewGroup)
        // validate
        assertNotNull(actual)
        assertEquals(vendorName2, actual.findViewById<TextView>(R.id.vendor_name).text.toString())
        assertEquals(expected, actual.findViewById<TextView>(R.id.vendor_macs).text.toString())
        verify(vendorService).findMacAddresses(vendorName2)
        verify(vendorService, Mockito.never()).findVendorName(vendorName1)
        verify(vendorService, Mockito.never()).findVendorName(vendorName3)
    }

}