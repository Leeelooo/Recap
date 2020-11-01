package com.leeloo.post.create

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.format.DateUtils
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.leeloo.post.R
import com.leeloo.post.base.BaseActivity
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.bottom_sheet_mentions.*
import kotlinx.android.synthetic.main.bottom_sheet_settings.*
import java.util.*
import java.util.concurrent.TimeUnit

class CreateActivity : BaseActivity<CreateViewState, CreateIntent>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_create
    override val viewModel: CreateViewModel by viewModels()

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var datePicker: DatePickerDialog
    private lateinit var timePicker: TimePickerDialog

    override fun initViews() {
        val popup = PopupMenu(this, delay_chip)
        popup.inflate(R.menu.delay_context_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.change_date_item -> {
                    _intentLiveData.value = CreateIntent.OpenDelayPickerIntent
                    true
                }
                R.id.clear_date_item -> {
                    _intentLiveData.value = CreateIntent.ClearDelayTimeIntent
                    true
                }
                else -> false
            }
        }

        btn_share.setOnClickListener {
            _intentLiveData.value = CreateIntent.SendPostIntent(comment_edit.text?.toString() ?: "")
        }

        create_post_arrow_back.setOnClickListener {
            finish()
        }
        option_chip.setOnClickListener {
            _intentLiveData.value = CreateIntent.TogglePostPublicityIntent
        }
        delay_chip.setOnClickListener {
            if (delay_chip.text != resources.getString(R.string.chip_now)) {
                popup.show()
            } else {
                _intentLiveData.value = CreateIntent.OpenDelayPickerIntent
            }
        }

        attach_file.setOnClickListener {
            _intentLiveData.value = CreateIntent.OpenFilesToAttachIntent
        }
        at_source.setOnClickListener {
            _intentLiveData.value = CreateIntent.OpenMentionsOptionsIntent
        }
        post_settings.setOnClickListener {
            _intentLiveData.value = CreateIntent.OpenPostSettingsIntent
        }

        bottomSheetDialog = BottomSheetDialog(this).apply {
            setOnDismissListener {
                _intentLiveData.value = CreateIntent.DismissSettingsDialogIntent
            }
            dismissWithAnimation = true
        }

        datePicker = DatePickerDialog(this)
        datePicker.setOnDateSetListener { view, year, month, dayOfMonth ->
            pickedDateTimestamp = GregorianCalendar(year, month, dayOfMonth).timeInMillis
            timePicker.show()
            timePicker.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(this, R.color.colorAccent)
            )
            timePicker.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(this, R.color.colorAccent)
            )
        }
        datePicker.setOnDismissListener {
            _intentLiveData.value = CreateIntent.DismissSettingsDialogIntent
        }

        timePicker = TimePickerDialog(
            this,
            { _, hour: Int, minute: Int ->
                val lessonStartTime =
                    TimeUnit.HOURS.toMillis(hour.toLong()) + TimeUnit.MINUTES.toMillis(minute.toLong()) - TimeUnit.HOURS.toMillis(
                        3L
                    )
                _intentLiveData.value = CreateIntent.ChoosedDelayTimePicker(
                    (lessonStartTime + pickedDateTimestamp) / 1000L
                )
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        )
        timePicker.setOnDismissListener {
            _intentLiveData.value = CreateIntent.DismissSettingsDialogIntent
        }
    }

    override fun render(viewState: CreateViewState) {
        if (viewState.timeDelay != 0L) {
            delay_chip.text =
                DateUtils.getRelativeTimeSpanString(
                    viewState.timeDelay * 1000,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                )
        } else {
            delay_chip.text = resources.getString(R.string.chip_now)
        }

        if (viewState.isSettingsOpened) {
            setContentView(R.layout.bottom_sheet_settings)
            bottomSheetDialog.bottom_sheet_dismiss.setOnClickListener { bottomSheetDialog.dismiss() }
            bottomSheetDialog.option_disable_comment_switch.setOnClickListener {
                _intentLiveData.value = CreateIntent.ToggleCommentsIntent
            }
            bottomSheetDialog.option_disable_notifications_switch.setOnClickListener {
                _intentLiveData.value = CreateIntent.ToggleNotificationsIntent
            }
            bottomSheetDialog.option_disable_comment_switch.isChecked =
                viewState.isCommentsDisabled
            bottomSheetDialog.option_disable_notifications_switch.isChecked =
                viewState.isNotificationDisabled
            bottomSheetDialog.show()
        } else {
            bottomSheetDialog.dismiss()
        }

        if (viewState.isMentionListOpened) {
            setContentView(R.layout.bottom_sheet_mentions)
            bottomSheetDialog.bottom_sheet_mentions_dismiss.setOnClickListener { bottomSheetDialog.dismiss() }
            bottomSheetDialog.show()
        } else {
            bottomSheetDialog.dismiss()
        }

        if (viewState.attachmentMode != 0) {
            bottomSheetDialog.show()
        } else {
            bottomSheetDialog.dismiss()
        }

        if (viewState.isDelayPickerOpened) {
            datePicker.show()
            datePicker.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(this, R.color.colorAccent)
            )
            datePicker.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(this, R.color.colorAccent)
            )
        } else {
            datePicker.dismiss()
        }

        if (viewState.isPublicPost) {
            option_chip.text = resources.getString(R.string.chip_public_post)
            option_chip.setChipIconResource(R.drawable.ic_person)
        } else {
            option_chip.text = resources.getString(R.string.chip_private_post)
            option_chip.setChipIconResource(R.drawable.ic_private)
        }

    }

    companion object {
        private var pickedDateTimestamp = 0L
    }

}