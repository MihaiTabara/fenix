/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.sessioncontrol.viewholders.onboarding

import android.view.View
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.onboarding_tracking_protection.view.*
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.onboarding.OnboardingRadioButton

class OnboardingTrackingProtectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var standardTrackingProtection: OnboardingRadioButton
    private var strictTrackingProtection: OnboardingRadioButton
    private var trackingProtectionToggle: Switch

    init {
        view.header_text.setOnboardingIcon(R.drawable.ic_onboarding_tracking_protection)

        trackingProtectionToggle = view.tracking_protection_toggle
        standardTrackingProtection = view.tracking_protection_standard_option
        strictTrackingProtection = view.tracking_protection_strict_default

        val appName = view.context.getString(R.string.app_name)
        view.description_text.text = view.context.getString(
            R.string.onboarding_tracking_protection_description1,
            appName
        )

        trackingProtectionToggle.apply {
            isChecked = view.context.settings().shouldUseTrackingProtection
            setOnCheckedChangeListener { _, isChecked ->
                updateTrackingProtectionSetting(isChecked)
                updateRadioGroupState(view, isChecked)
            }
        }

        setupRadioGroup(view, trackingProtectionToggle.isChecked)
        updateRadioGroupState(view, trackingProtectionToggle.isChecked)
    }

    private fun setupRadioGroup(view: View, isChecked: Boolean) {

        updateRadioGroupState(view, isChecked)

        standardTrackingProtection.addToRadioGroup(strictTrackingProtection)
        strictTrackingProtection.addToRadioGroup(standardTrackingProtection)

        strictTrackingProtection.isChecked =
            itemView.context.settings().useStrictTrackingProtection
        standardTrackingProtection.isChecked =
            !itemView.context.settings().useStrictTrackingProtection

        standardTrackingProtection.onClickListener {
            updateTrackingProtectionPolicy()
        }

        view.clickable_region_standard.setOnClickListener {
            standardTrackingProtection.performClick()
        }

        strictTrackingProtection.onClickListener {
            updateTrackingProtectionPolicy()
        }

        view.clickable_region_strict.setOnClickListener {
            strictTrackingProtection.performClick()
        }
    }

    private fun updateRadioGroupState(view: View, isChecked: Boolean) {
        standardTrackingProtection.isEnabled = isChecked
        strictTrackingProtection.isEnabled = isChecked

        view.protection_standard_description.isEnabled = isChecked
        view.protection_strict_description.isEnabled = isChecked
        view.clickable_region_standard.isClickable = isChecked

        view.protection_standard_title.isEnabled = isChecked
        view.protection_strict_title.isEnabled = isChecked
        view.clickable_region_strict.isClickable = isChecked
    }

    private fun updateTrackingProtectionSetting(enabled: Boolean) {
        itemView.context.settings().shouldUseTrackingProtection = enabled
        with(itemView.context.components) {
            val policy = core.createTrackingProtectionPolicy(enabled)
            useCases.settingsUseCases.updateTrackingProtection.invoke(policy)
            useCases.sessionUseCases.reload.invoke()
        }
    }

    private fun updateTrackingProtectionPolicy() {
        itemView.context?.components?.let {
            val policy = it.core.createTrackingProtectionPolicy()
            it.useCases.settingsUseCases.updateTrackingProtection.invoke(policy)
            it.useCases.sessionUseCases.reload.invoke()
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.onboarding_tracking_protection
    }
}
