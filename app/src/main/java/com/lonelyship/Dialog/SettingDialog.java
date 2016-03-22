package com.lonelyship.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.lonelyship.Main.MainActivity;
import com.lonelyship.Main.R;

import object.TheAppInfo;

/**
 * 側拉式選單，相關設定的Dialog
 */
public class SettingDialog extends DialogFragment {

    static final String BUNDLE_KEY_ANIMATION_POSITION = "BUNDLE_KEY_ANIMATION_POSITION";
    static final String BUNDLE_KEY_PLAY_DURATION      = "BUNDLE_KEY_PLAY_DURATION";
    static final String BUNDLE_KEY_IS_AUTOPLAY        = "BUNDLE_KEY_IS_AUTOPLAY";

    int     m_iAnimationPosition;
    int m_iVoiceLanguagePosition;
    int     m_iPlayDuration;
    boolean m_bIsAutoPlay;

    private View m_dialogView;
    private Spinner m_spinnerAnimation;
    private Spinner m_spinnerVoiceLanguage;
    private SeekBar m_seekBar;
    private TextView m_tvPlayDuration;
    private CheckBox m_checkBox;
    private Button m_btnConfirm;
    private Button m_btnCancel;

    public static SettingDialog newInstance () {

        SettingDialog settingDialog = new SettingDialog();

//        Bundle args = new Bundle();
//        args.putInt(BUNDLE_KEY_ANIMATION_POSITION,iAnimationPosition);
//        args.putInt(BUNDLE_KEY_PLAY_DURATION,iPlayDuration);
//        args.putBoolean(BUNDLE_KEY_IS_AUTOPLAY, bIsAutoPlay);
//
//        settingDialog.setArguments(args);

        return settingDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_iAnimationPosition =  TheAppInfo.getInstance(getContext()).uiGetAnimationPosition();
        m_iVoiceLanguagePosition =  TheAppInfo.getInstance(getContext()).uiGetVoiceLanguagePosition();
        m_iPlayDuration      =  TheAppInfo.getInstance(getContext()).uiGetAnimationDuration();
        m_bIsAutoPlay        =  TheAppInfo.getInstance(getContext()).uiGetAnimationIsAutoPlay();

        setStyle(DialogFragment.STYLE_NORMAL,android.R.style.Theme_Holo_Dialog_MinWidth);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        m_dialogView = inflater.inflate(R.layout.dialog_settings,container,false);

        initialViews();
        initalListeners();

        return m_dialogView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().setTitle(getString(R.string.setting_dialog_text_title));

        return dialog;
    }

    private void initialViews () {

        m_spinnerAnimation = (Spinner) m_dialogView.findViewById(R.id.spinner_set_animation);
        m_spinnerVoiceLanguage = (Spinner) m_dialogView.findViewById(R.id.spinner_set_voice_language);
        m_tvPlayDuration = (TextView) m_dialogView.findViewById(R.id.textview_set_play_duration);
        m_checkBox = (CheckBox) m_dialogView.findViewById(R.id.checkbox_auto_play);
        m_seekBar = (SeekBar) m_dialogView.findViewById(R.id.seekbar_set_play_duration);
        m_btnConfirm = (Button) m_dialogView.findViewById(R.id.button_set_confirm);
        m_btnCancel = (Button) m_dialogView.findViewById(R.id.button_set_cancel);

        m_tvPlayDuration.setText(String.format(getString(R.string.setting_dialog_text_duration),m_iPlayDuration));
        m_spinnerAnimation.setSelection(m_iAnimationPosition, true);
        m_spinnerVoiceLanguage.setSelection(m_iVoiceLanguagePosition, true);
        m_seekBar.setProgress(m_iPlayDuration - 1);
        m_checkBox.setChecked(m_bIsAutoPlay);
    }

    private void initalListeners() {

        m_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_bIsAutoPlay = isChecked;
            }
        });

        m_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m_iPlayDuration = (progress + 1);
                m_tvPlayDuration.setText(String.format(getString(R.string.setting_dialog_text_duration), m_iPlayDuration));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        m_spinnerAnimation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_iAnimationPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_spinnerVoiceLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_iVoiceLanguagePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        m_btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TheAppInfo.getInstance(getContext()).uiSetAnimationPosition(m_iAnimationPosition);
                TheAppInfo.getInstance(getContext()).uiSetVoiceLanguagePosition(m_iVoiceLanguagePosition);
                TheAppInfo.getInstance(getContext()).uiSetAnimationDuration(m_iPlayDuration);
                TheAppInfo.getInstance(getContext()).uiSetAnimationIsAutoPlay(m_bIsAutoPlay);

               ((MainActivity) getActivity()).ResumePhotoFragment();
                getDialog().dismiss();
            }
        });

        m_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}
