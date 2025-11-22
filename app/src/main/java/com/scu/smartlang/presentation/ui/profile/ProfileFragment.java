// java
package com.scu.smartlang.presentation.ui.profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;

    private TextView tvName;
    private TextView tvLevelLabel;
    private TextView tvXpLabel;
    private ProgressBar pbXp;
    private RecyclerView rvFriends;
    private Button btnSendFriendRequest;

    private FriendsAdapter friendsAdapter;

    private String viewUserId = null;
    private String currentUid = null;
    private boolean isMyProfile = false;

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

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setupViews(view);

        if (getArguments() != null) {
            viewUserId = getArguments().getString("userId");
        }

        userViewModel.getCurrentUserId().thenAccept(uid -> {
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
        rvFriends = view.findViewById(R.id.rv_friends);
        btnSendFriendRequest = view.findViewById(R.id.btn_send_friend_request);

        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsAdapter = new FriendsAdapter(new ArrayList<>());
        rvFriends.setAdapter(friendsAdapter);
    }

    private void setupProfileView() {
        isMyProfile = (viewUserId == null || viewUserId.equals(currentUid));

        observeViewModel();

        if (isMyProfile) {
            btnSendFriendRequest.setVisibility(View.GONE);
            userViewModel.fetchUserProfile();
            if (currentUid != null) {
                userViewModel.fetchFriends(currentUid);
            }
        } else {
            btnSendFriendRequest.setVisibility(View.VISIBLE);
            btnSendFriendRequest.setOnClickListener(v -> sendFriendRequest());
            userViewModel.fetchUserById(viewUserId);
            userViewModel.fetchFriends(viewUserId);
            if (currentUid != null) {
                userViewModel.checkFriendshipStatus(currentUid, viewUserId);
            }
        }
    }

    private void observeViewModel() {
        if (isMyProfile) {
            userViewModel.getUserProfile().observe(getViewLifecycleOwner(), state -> {
                if (state instanceof AuthResultState.Success) {
                    User user = ((AuthResultState.Success) state).getUser();
                    updateUi(user);
                }
            });
        } else {
            userViewModel.getViewedUser().observe(getViewLifecycleOwner(), user -> {
                if (user != null) updateUi(user);
            });

            userViewModel.getFriendshipStatus().observe(
                    getViewLifecycleOwner(),
                    this::updateFriendRequestButton
            );
        }

        userViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            if (friends == null) return;
            List<FriendsAdapter.FriendModel> models = new ArrayList<>();
            for (Friend f : friends) {
                models.add(new FriendsAdapter.FriendModel(
                        f.getUserName(),   // name
                        f.getLevel()       // level
                ));
            }
            friendsAdapter.updateList(models);
        });
    }

    private void updateUi(User user) {
        if (user == null || !isAdded()) return;

        String name = (user.getUserName() != null) ? user.getUserName() : "Kullanıcı";
        tvName.setText(name);

        int level = user.getLevel();
        int totalXp = user.getXp();

        // Basit level / XP hesabı
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
    }

    private void updateFriendRequestButton(String status) {
        if (!isAdded() || btnSendFriendRequest == null) return;

        if (status == null) {
            btnSendFriendRequest.setText("Arkadaşlık İsteği Gönder");
            btnSendFriendRequest.setEnabled(true);
            btnSendFriendRequest.setOnClickListener(v -> sendFriendRequest());
            return;
        }

        switch (status) {
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
                // Şimdilik sadece pasif bırakıyoruz.
                break;
            case "NONE":
            default:
                btnSendFriendRequest.setText("Arkadaşlık İsteği Gönder");
                btnSendFriendRequest.setEnabled(true);
                btnSendFriendRequest.setOnClickListener(v -> sendFriendRequest());
                break;
        }
    }

    private void sendFriendRequest(){
        if (currentUid == null || viewUserId == null || currentUid.equals(viewUserId)) return;

        btnSendFriendRequest.setEnabled(false);
        btnSendFriendRequest.setText("Gönderiliyor...");

        userViewModel.sendFriendRequestAndUpdateStatus(currentUid, viewUserId)
                .exceptionally(throwable -> {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            // Tam hata mesajını logla
                            android.util.Log.e("ProfileFragment", "Friend request failed", throwable);

                            // Root cause'u al
                            Throwable cause = throwable;
                            while (cause.getCause() != null) {
                                cause = cause.getCause();
                            }

                            String errorMsg = cause.getMessage() != null ? cause.getMessage() : "Bilinmeyen hata";
                            Toast.makeText(getContext(), "Hata: " + errorMsg, Toast.LENGTH_LONG).show();

                            updateFriendRequestButton("NONE");
                        });
                    }
                    return null;
                });
    }

}