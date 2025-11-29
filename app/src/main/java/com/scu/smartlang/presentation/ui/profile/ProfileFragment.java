package com.scu.smartlang.presentation.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;
import com.scu.smartlang.presentation.viewmodel.SocialViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment implements FriendsAdapter.OnFriendClickListener {

    private TextView tvName;
    private TextView tvLevelLabel;
    private TextView tvXpLabel;
    private ProgressBar pbXp;
    private ImageView ivProfileImage; // EKLENDİ
    private RecyclerView rvFriends;
    private Button btnSendFriendRequest;
    private ImageView ivLanguageSettings;

    private FriendsAdapter friendsAdapter;

    private String viewUserId = null;
    private String currentUid = null;
    private boolean isMyProfile = false;
    private SocialViewModel socialViewModel;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        socialViewModel = new ViewModelProvider(requireActivity()).get(SocialViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        setupViews(view);

        if (getArguments() != null) {
            viewUserId = getArguments().getString("userId");
        }

        socialViewModel.getCurrentUserId().thenAccept(uid -> {
            currentUid = uid;
            if (!isAdded()) return;
            requireActivity().runOnUiThread(this::setupProfileView);
        });
    }

    private void setupViews(View view) {
        tvName = view.findViewById(R.id.tv_profile_name);
        tvLevelLabel = view.findViewById(R.id.tv_level_label);
        tvXpLabel = view.findViewById(R.id.tv_xp_label);
        pbXp = view.findViewById(R.id.pb_profile_xp);
        ivProfileImage = view.findViewById(R.id.iv_profile_image); // BAĞLANDI
        rvFriends = view.findViewById(R.id.rv_friends);
        btnSendFriendRequest = view.findViewById(R.id.btn_send_friend_request);
        ivLanguageSettings = view.findViewById(R.id.iv_language_settings);

        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsAdapter = new FriendsAdapter(new ArrayList<>(), this);
        rvFriends.setAdapter(friendsAdapter);
    }

    private void setupProfileView() {
        isMyProfile = (viewUserId == null || viewUserId.equals(currentUid));

        // Eğer kendi profilim ise dil ikonunu göster, başkasının profili ise gizle
        if (ivLanguageSettings != null) {
            ivLanguageSettings.setVisibility(isMyProfile ? View.VISIBLE : View.GONE);
            if (isMyProfile) {
                ivLanguageSettings.setOnClickListener(v ->
                        Toast.makeText(getContext(), "Dil seçme menüsü yakında!", Toast.LENGTH_SHORT).show()
                );
            }
        }

        observeViewModel();

        if (isMyProfile) {
            btnSendFriendRequest.setVisibility(View.GONE);
            profileViewModel.fetchUserProfile();
            if (currentUid != null) {
                socialViewModel.fetchFriends(currentUid);
            }
        } else {
            btnSendFriendRequest.setVisibility(View.VISIBLE);
            btnSendFriendRequest.setOnClickListener(v -> sendFriendRequest());
            socialViewModel.fetchUserById(viewUserId);
            socialViewModel.fetchFriends(viewUserId);
            if (currentUid != null) {
                socialViewModel.checkFriendshipStatus(currentUid, viewUserId);
            }
        }
    }

    private void observeViewModel() {
        if (isMyProfile) {
            profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), state -> {
                if (state instanceof AuthResultState.Success) {
                    User user = ((AuthResultState.Success) state).getUser();
                    updateUi(user);
                }
            });
        } else {
            socialViewModel.getViewedUser().observe(getViewLifecycleOwner(), user -> {
                if (user != null) updateUi(user);
            });

            socialViewModel.getFriendshipStatus().observe(
                    getViewLifecycleOwner(),
                    this::updateFriendRequestButton
            );
        }

        socialViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            if (friends != null) {
                friendsAdapter.updateList(friends);
            }
        });
    }

    private void updateUi(User user) {
        if (user == null || !isAdded()) return;

        String name = (user.getUserName() != null) ? user.getUserName() : "Kullanıcı";
        tvName.setText(name);

        int level = user.getLevel();
        int totalXp = user.getXp();

        int requiredXpForThisLevel = 100 + (level - 1) * 25;
        int previousLevelsXp = 0;
        for (int i = 1; i < level; i++) {
            previousLevelsXp += 100 + (i - 1) * 25;
        }
        int xpInCurrentLevel = Math.max(0, totalXp - previousLevelsXp);

        tvLevelLabel.setText("Level " + level);
        tvXpLabel.setText(xpInCurrentLevel + " / " + requiredXpForThisLevel + " XP");

        pbXp.setMax(requiredXpForThisLevel);
        pbXp.setProgress(Math.min(xpInCurrentLevel, requiredXpForThisLevel));

        // RESİM YÜKLEME KISMI (GÜNCELLENDİ)
        if (ivProfileImage != null) {
            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.ic_person_24dp)
                    .error(R.drawable.ic_person_24dp) // Hata olursa varsayılan ikon
                    .circleCrop()
                    .into(ivProfileImage);
        }
    }

    private void updateFriendRequestButton(String status) {
        if (!isAdded() || btnSendFriendRequest == null) return;

        String statusNonNull = (status == null) ? "NONE" : status;

        switch (statusNonNull) {
            case "FRIENDS":
                btnSendFriendRequest.setText("Arkadaşsınız");
                btnSendFriendRequest.setEnabled(false);
                break;
            case "REQUEST_SENT":
                btnSendFriendRequest.setText("İstek Gönderildi");
                btnSendFriendRequest.setEnabled(false);
                break;
            case "REQUEST_RECEIVED":
                btnSendFriendRequest.setText("İsteği Kabul Et");
                btnSendFriendRequest.setEnabled(true);
                btnSendFriendRequest.setOnClickListener(v ->
                        Toast.makeText(getContext(), "Lütfen isteği bildirimler sayfasından yönetin.", Toast.LENGTH_LONG).show()
                );
                break;
            case "NONE":
            default:
                btnSendFriendRequest.setText("Arkadaşlık İsteği Gönder");
                btnSendFriendRequest.setEnabled(true);
                btnSendFriendRequest.setOnClickListener(v -> sendFriendRequest());
                break;
        }
    }

    private void sendFriendRequest() {
        if (currentUid != null && viewUserId != null) {
            socialViewModel.sendFriendRequest(currentUid, viewUserId);
            Toast.makeText(getContext(), "Arkadaşlık isteği gönderildi.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "İstek gönderilemedi. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFriendClick(Friend friend) {
        Bundle args = new Bundle();
        args.putString("userId", friend.getUid());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_profile_to_otherUserFragment, args);
    }
}