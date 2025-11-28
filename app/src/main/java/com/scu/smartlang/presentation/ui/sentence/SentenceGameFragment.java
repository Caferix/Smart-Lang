package com.scu.smartlang.presentation.ui.sentence;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.SentenceGameViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SentenceGameFragment extends Fragment {

    private SentenceGameViewModel viewModel;

    // UI Elemanları
    private TextView tvXp;
    private TextView tvLevel;
    private TextView tvQuestionIndex;
    private TextView tvSentence;
    private TextView tvFinished;
    private ProgressBar progressBarLevel;
    private LottieAnimationView lottieAnimation;

    private AppCompatButton btnOption1, btnOption2, btnOption3, btnOption4;
    private AppCompatButton lastClickedButton = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sentence_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel Başlatma
        viewModel = new ViewModelProvider(this).get(SentenceGameViewModel.class);

        // View Binding
        tvXp = view.findViewById(R.id.tvXp);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvQuestionIndex = view.findViewById(R.id.tvQuestionIndex);
        tvSentence = view.findViewById(R.id.tvSentence);
        tvFinished = view.findViewById(R.id.tvFinished);
        progressBarLevel = view.findViewById(R.id.progressBarLevel);
        lottieAnimation = view.findViewById(R.id.lottieAnimation);

        btnOption1 = view.findViewById(R.id.btnOption1);
        btnOption2 = view.findViewById(R.id.btnOption2);
        btnOption3 = view.findViewById(R.id.btnOption3);
        btnOption4 = view.findViewById(R.id.btnOption4);

        // Level Bar Maksimum Değeri (15 XP)
        progressBarLevel.setMax(15);

        // Tıklama Dinleyicisi
        View.OnClickListener optionClickListener = v -> {
            if (!(v instanceof AppCompatButton)) return;
            AppCompatButton button = (AppCompatButton) v;

            // Haptic feedback
            button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            // Spam önleme
            button.setEnabled(false);
            button.postDelayed(() -> {
                if (isAdded()) button.setEnabled(true);
            }, 1000);

            lastClickedButton = button;
            viewModel.onAnswerSelected(button.getText().toString());
        };

        btnOption1.setOnClickListener(optionClickListener);
        btnOption2.setOnClickListener(optionClickListener);
        btnOption3.setOnClickListener(optionClickListener);
        btnOption4.setOnClickListener(optionClickListener);

        // Gözlemciler
        observeViewModel();
    }

    private void observeViewModel() {
        // Soru
        viewModel.currentQuestion.observe(getViewLifecycleOwner(), question -> {
            if (question == null) {
                tvSentence.setText("");
                return;
            }
            resetButtonsVisualState();
            tvSentence.setText(question.getSentence());
            updateQuestionIndex();
        });

        // Şıklar
        viewModel.options.observe(getViewLifecycleOwner(), options -> {
            if (options != null) bindOptionsToButtons(options);
        });

        // XP
        viewModel.totalXp.observe(getViewLifecycleOwner(), xp ->
                tvXp.setText("XP: " + (xp != null ? xp : 0))
        );

        // Level
        viewModel.currentLevelLiveData.observe(getViewLifecycleOwner(), level -> {
            if (level != null) tvLevel.setText("LVL " + level);
        });

        // Level Bar
        viewModel.currentProgress.observe(getViewLifecycleOwner(), progress -> {
            if (progress != null) progressBarLevel.setProgress(progress);
        });

        // Renkler
        viewModel.answerStatus.observe(getViewLifecycleOwner(), isCorrect -> {
            if (isCorrect == null) {
                resetButtonsVisualState();
                return;
            }
            if (lastClickedButton != null) {
                if (isCorrect) {
                    lastClickedButton.setSelected(true);
                    lastClickedButton.setActivated(false);
                    Toast.makeText(requireContext(), "Correct! 🎉", Toast.LENGTH_SHORT).show();
                } else {
                    lastClickedButton.setSelected(false);
                    lastClickedButton.setActivated(true);
                    Toast.makeText(requireContext(), "Wrong 😔", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sesler
        viewModel.soundEvent.observe(getViewLifecycleOwner(), soundResId -> {
            if (soundResId != null && soundResId != 0) {
                playSound(soundResId);
                viewModel.resetSoundEvent();
            }
        });

        // Oyun Bitti (BURASI DÜZELTİLDİ)
        viewModel.quizFinished.observe(getViewLifecycleOwner(), finished -> {
            if (finished != null && finished) {
                tvFinished.setVisibility(View.VISIBLE);
                setButtonsEnabled(false);
                showFinishDialog(); // <-- Artık özel tasarımı çağırıyor
            } else {
                tvFinished.setVisibility(View.GONE);
                setButtonsEnabled(true);
            }
        });

        // Animasyonlar
        viewModel.animationEvent.observe(getViewLifecycleOwner(), eventCode -> {
            if (eventCode == null || eventCode == 0) return;

            if (eventCode == 1) {
                lottieAnimation.setAnimation(R.raw.anim_level_up);
                Toast.makeText(requireContext(), "LEVEL UP! 🆙", Toast.LENGTH_SHORT).show();
            } else if (eventCode == 2) {
                lottieAnimation.setAnimation(R.raw.anim_special_105);
                Toast.makeText(requireContext(), "TEBRİKLER! 75 XP! 🏆", Toast.LENGTH_LONG).show();
            }

            lottieAnimation.setVisibility(View.VISIBLE);
            lottieAnimation.playAnimation();

            lottieAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    lottieAnimation.setVisibility(View.GONE);
                    lottieAnimation.removeAllAnimatorListeners();
                    viewModel.resetAnimationEvent();
                }
            });
        });
    }

    private void playSound(int soundResId) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), soundResId);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- 🔥 ÖZEL TASARIMLI BİTİŞ EKRANI (Custom Dialog) 🔥 ---
    private void showFinishDialog() {
        // 1. Custom Layout'u Yükle
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Burada senin tasarladığın dialog_game_finished.xml kullanılıyor
        View dialogView = inflater.inflate(R.layout.dialog_game_finished, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Arka planı şeffaf yap (Köşeler yuvarlak görünsün diye)
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 2. Butonları Bul
        AppCompatButton btnRestart = dialogView.findViewById(R.id.btnDialogRestart);
        AppCompatButton btnMenu = dialogView.findViewById(R.id.btnDialogMenu);

        // 3. TEKRAR OYNA
        btnRestart.setOnClickListener(v -> {
            viewModel.restartGame();
            tvFinished.setVisibility(View.GONE);
            setButtonsEnabled(true);
            dialog.dismiss();
        });

        // 4. MENÜYE DÖN
        btnMenu.setOnClickListener(v -> {
            dialog.dismiss();
            if (getActivity() != null) {
                getActivity().onBackPressed(); // Ana sayfaya dön
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    // --- Helper Metotlar ---

    private void updateQuestionIndex() {
        int current = viewModel.getCurrentIndex() + 1;
        int total = viewModel.getTotalQuestionCount();
        tvQuestionIndex.setText(current + " / " + total);
    }

    private void bindOptionsToButtons(List<String> options) {
        setupButton(btnOption1, options, 0);
        setupButton(btnOption2, options, 1);
        setupButton(btnOption3, options, 2);
        setupButton(btnOption4, options, 3);
    }

    private void setupButton(AppCompatButton btn, List<String> options, int index) {
        if (index < options.size()) {
            btn.setText(options.get(index));
            btn.setVisibility(View.VISIBLE);
        } else {
            btn.setVisibility(View.GONE);
        }
    }

    private void resetButtonsVisualState() {
        AppCompatButton[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
        for (AppCompatButton b : buttons) {
            if (b == null) continue;
            b.setSelected(false);
            b.setActivated(false);
            b.setEnabled(true);
        }
        lastClickedButton = null;
    }

    private void setButtonsEnabled(boolean enabled) {
        btnOption1.setEnabled(enabled);
        btnOption2.setEnabled(enabled);
        btnOption3.setEnabled(enabled);
        btnOption4.setEnabled(enabled);
    }

    @Override
    public void onDestroyView() {
        if (lottieAnimation != null) {
            lottieAnimation.cancelAnimation();
            lottieAnimation.removeAllAnimatorListeners();
        }
        super.onDestroyView();
    }
}