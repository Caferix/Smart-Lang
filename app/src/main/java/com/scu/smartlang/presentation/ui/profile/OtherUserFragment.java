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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.viewmodel.SocialViewModel;
import java.util.ArrayList;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OtherUserFragment extends Fragment implements FriendsAdapter.OnFriendClickListener {

    private TextView tvName, tvLevelLabel, tvXpLabel;
    private ProgressBar pbXp;
    private RecyclerView rvFriends;
    private Button btnSendFriendRequest;
    private FriendsAdapter friendsAdapter;
    private SocialViewModel socialViewModel;
    private String viewUserId;
    private String currentUid;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socialViewModel = new ViewModelProvider(requireActivity()).get(SocialViewModel.class);

        if (getArguments() != null) {
            viewUserId = getArguments().getString("userId");
        }

        if (viewUserId == null) {
            Toast.makeText(getContext(), "Kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        setupViews(view);
        observeViewModel();

        socialViewModel.getCurrentUserId().thenAccept(uid -> {
            currentUid = uid;
            if (!isAdded()) return;

            if (viewUserId.equals(currentUid)) {
                requireActivity().runOnUiThread(() -> {
                    if (btnSendFriendRequest != null) {
                        btnSendFriendRequest.setVisibility(View.GONE);
                    }
                });
            }

            requireActivity().runOnUiThread(this::fetchData);
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
        // Adaptörü tıklama dinleyicisi ile başlat
        friendsAdapter = new FriendsAdapter(new ArrayList<>(), this);
        rvFriends.setAdapter(friendsAdapter);

        btnSendFriendRequest.setVisibility(View.VISIBLE);
    }

    private void fetchData() {
        // Görüntülenen kullanıcı mevcut kullanıcı değilse arkadaşlık durumunu kontrol et
        if (currentUid != null && !currentUid.equals(viewUserId)) {
            socialViewModel.checkFriendshipStatus(currentUid, viewUserId);
        }
        socialViewModel.fetchUserById(viewUserId);
        socialViewModel.fetchFriends(viewUserId);
    }

    private void observeViewModel() {
        socialViewModel.getViewedUser().observe(getViewLifecycleOwner(), this::updateUi);
        socialViewModel.getFriendshipStatus().observe(getViewLifecycleOwner(), this::updateFriendRequestButton);

        // Adaptörü doğrudan Friend listesi ile güncelle
        socialViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            if (friends == null || !isAdded()) {
                friendsAdapter.updateList(new ArrayList<>());
                return;
            }
            friendsAdapter.updateList(friends);
        });

        socialViewModel.getFriendshipActionCompleted().observe(getViewLifecycleOwner(), aVoid -> {
            Toast.makeText(getContext(), "İşlem tamamlandı.", Toast.LENGTH_SHORT).show();
            fetchData();
        });
    }

    private void updateUi(User user) {
        if (user == null || !isAdded()) return;
        tvName.setText(user.getUserName() != null ? user.getUserName() : "Kullanıcı");
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

        ImageView ivProfileImage = getView().findViewById(R.id.iv_profile_image);
        Glide.with(this)
                .load(user.getProfileImageUrl())
                .placeholder(R.drawable.ic_person_24dp)
                .circleCrop()
                .into(ivProfileImage);
    }

    private void updateFriendRequestButton(String status) {
        if (!isAdded() || btnSendFriendRequest == null) return;

        String statusNonNull = (status == null) ? "NONE" : status;

        switch (statusNonNull) {
            case "FRIENDS":
                btnSendFriendRequest.setText("Arkadaşlıktan Çıkar");
                btnSendFriendRequest.setEnabled(true);
                btnSendFriendRequest.setOnClickListener(v -> showRemoveFriendDialog());
                break;
            case "REQUEST_SENT":
                btnSendFriendRequest.setText("İstek Gönderildi");
                btnSendFriendRequest.setEnabled(false);
                btnSendFriendRequest.setOnClickListener(null);
                break;
            case "REQUEST_RECEIVED":
                btnSendFriendRequest.setText("İsteği Yönet");
                btnSendFriendRequest.setEnabled(true);
                btnSendFriendRequest.setOnClickListener(v -> {
                    NavHostFragment.findNavController(this).navigate(R.id.friendRequestsFragment);
                });
                break;
            case "NONE":
            default:
                btnSendFriendRequest.setText("Arkadaşlık İsteği Gönder");
                btnSendFriendRequest.setEnabled(true);
                btnSendFriendRequest.setOnClickListener(v -> sendFriendRequest());
                break;
        }
    }

    private void showRemoveFriendDialog() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Arkadaşlıktan Çıkar")
                .setMessage("Bu kullanıcıyı arkadaşlıktan çıkarmak istediğinizden emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    if (currentUid != null && viewUserId != null) {
                        socialViewModel.removeFriend(currentUid, viewUserId);
                    }
                })
                .setNegativeButton("Hayır", null)
                .show();
    }

    private void sendFriendRequest() {
        if (currentUid != null && viewUserId != null) {
            socialViewModel.sendFriendRequest(currentUid, viewUserId);
        } else {
            Toast.makeText(getContext(), "İstek gönderilemedi. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
        }
    }

    // Arkadaş listesindeki bir öğeye tıklandığında çalışacak metot
    @Override
    public void onFriendClick(Friend friend) {
        // Tıklanan arkadaşın kendi profili ise navigasyon yapma
        if (friend.getUid().equals(viewUserId)) {
            return;
        }
        Bundle args = new Bundle();
        args.putString("userId", friend.getUid());
        // NavController ile aynı hedefe (kendi üzerine) yeni argümanlarla git
        NavHostFragment.findNavController(this)
                .navigate(R.id.navigation_profile, args);
    }
}
