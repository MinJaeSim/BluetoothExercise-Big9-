package cc.foxtail.funkey.childAdd;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.R;

import static cc.foxtail.funkey.navigation.NavigationActivity.soundId;
import static cc.foxtail.funkey.navigation.NavigationActivity.soundPool;

public class ChildViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTextView;
    private TextView dateTextView;
    private TextView weightTextView;
    private TextView heightTextView;
    private String documentKey;
    private ImageView genderImageView;
    private ImageView profileImageView;
    private Child child;

    public ChildViewHolder(final View itemView) {
        super(itemView);

        final Context context = itemView.getContext();
        final FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        nameTextView = itemView.findViewById(R.id.user_view_holder_name_text_view);
        dateTextView = itemView.findViewById(R.id.user_view_holder_birth_text_view);
        weightTextView = itemView.findViewById(R.id.user_view_holder_weight_text_view);
        heightTextView = itemView.findViewById(R.id.user_view_holder_height_text_view);
        genderImageView = itemView.findViewById(R.id.sex_image_view);
        profileImageView = itemView.findViewById(R.id.child_list_profile_image_view);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ChildDeleteDialogFragment dialog = ChildDeleteDialogFragment.newInstance(documentKey, child.getUserName());
                dialog.show(fm, "deleteDialog");
                return true;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
//                Fragment fragment = ChildInfoFragment.newInstance(child);
//                fm.beginTransaction().replace(R.id.content_fragment,fragment).addToBackStack(null).commit();
                fm.beginTransaction().replace(R.id.content_fragment, ChildAddFragment.newInstance(child,documentKey)).addToBackStack(null).commit();
            }
        });


    }

    public void bindDate(Child child, String documentKey) {

        this.child = child;
        nameTextView.setText(child.getUserName());

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        Date date = (new Date(child.getBirthTimeStamp()));
        simpleDateFormat.format(date);
        dateTextView.setText(simpleDateFormat.format(date)+"("+child.getAge()+")");

        this.documentKey = documentKey;

        weightTextView.setText(child.getWeight() + "Kg");
        heightTextView.setText(child.getHeight() + "Cm");

        int imageId = Objects.equals(child.getSex(), "남") ? R.drawable.ic_new_male : R.drawable.ic_new_female;
        genderImageView.setImageResource(imageId);

//        Glide.with(profileImageView)
//                .load(child.getProfileImageUrl())
//                .placeholder(R.drawable.child_image)
//                .into(profileImageView);

        Glide.with(profileImageView)
                .load(child.getProfileImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.child_image))
                .into(profileImageView);


    }


}
