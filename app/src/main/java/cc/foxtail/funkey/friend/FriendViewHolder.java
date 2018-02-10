package cc.foxtail.funkey.friend;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.dialog.FriendDeleteDialogFragment;

public class FriendViewHolder extends RecyclerView.ViewHolder {
    private TextView nameTextView;
    private TextView dateTextView;
    private TextView weightTextView;
    private TextView heightTextView;
    private String documentKey;
    private ImageView genderImageView;
    private ImageView profileImageView;
    private Child child;
    private FirebaseFirestore firebaseFirestore;
    private OnFriendClickListener onFriendClickListener;

    public FriendViewHolder(final View itemView) {
        super(itemView);

        firebaseFirestore = FirebaseFirestore.getInstance();
        nameTextView = itemView.findViewById(R.id.user_view_holder_name_text_view);
        dateTextView = itemView.findViewById(R.id.user_view_holder_birth_text_view);
        weightTextView = itemView.findViewById(R.id.user_view_holder_weight_text_view);
        heightTextView = itemView.findViewById(R.id.user_view_holder_height_text_view);
        genderImageView = itemView.findViewById(R.id.sex_image_view);
        profileImageView = itemView.findViewById(R.id.child_list_profile_image_view);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FriendAddDialogFragment dialog = FriendAddDialogFragment.newInstance(documentKey, child.getUserName());
                onFriendClickListener.onClick(documentKey, child);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FriendDeleteDialogFragment dialog = FriendDeleteDialogFragment.newInstance(documentKey, child.getUserName());
                onFriendClickListener.onLongClick(dialog);
                return true;
            }
        });
    }

    public void bindDate(Child child, String documentKey) {

        this.child = child;
        nameTextView.setText(child.getUserName());

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        Date date = (new Date(child.getBirthTimeStamp()));
        simpleDateFormat.format(date);
        dateTextView.setText(simpleDateFormat.format(date) + "(" + child.getAge() + ")");

        this.documentKey = documentKey;

        weightTextView.setText(child.getWeight() + "Kg");
        heightTextView.setText(child.getHeight() + "Cm");

        int imageId = Objects.equals(child.getSex(), "남") ? R.drawable.ic_new_male : R.drawable.ic_new_female;
        genderImageView.setImageResource(imageId);



        Glide.with(profileImageView)
                .load(child.getProfileImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.child_image))
                .into(profileImageView);


    }


    public void setOnFriendClickListener(OnFriendClickListener onFriendClickListener) {
        this.onFriendClickListener = onFriendClickListener;
    }
}