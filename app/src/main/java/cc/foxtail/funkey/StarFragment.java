package cc.foxtail.funkey;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import cc.foxtail.funkey.navigation.NavigationActivity;
import de.hdodenhof.circleimageview.CircleImageView;


public class StarFragment extends Fragment {

    private String title;
    private int levelNumber;
    private String levelName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star, container, false);
        setHasOptionsMenu(true);

        CircleImageView profileImageView = view.findViewById(R.id.star_fragment_profile_image_view);
        TextView nameTextView = view.findViewById(R.id.star_fragment_name_text_view);
        TextView levelTextView = view.findViewById(R.id.star_fragment_level_text_view);
        TextView starCountTextView = view.findViewById(R.id.star_count_text_view);

        title = getArguments().getString("TITLE");
        String name = getArguments().getString("NAME");
        String profileImageUrl = getArguments().getString("IMAGE_URL");
        int starCount = getArguments().getInt("STAR_COUNT", 0);

        getLevelNumber(starCount);
        setLevelName();

        levelTextView.setText(levelName + "(lv " + levelNumber + ")");

        Glide.with(profileImageView)
                .load(profileImageUrl)
                .apply(new RequestOptions()
                        .override(150, 150)
                        .placeholder(R.drawable.child_image)
                )
                .into(profileImageView);
        nameTextView.setText(name);
        starCountTextView.setText(String.valueOf(starCount));

        return view;
    }

    private void getLevelNumber(int totalStarCount) {
        if (totalStarCount > 0)
            levelNumber = 1;
        else if (totalStarCount >= 100)
            levelNumber = 2;
        else if (totalStarCount >= 210)
            levelNumber = 3;
        else if (totalStarCount >= 321)
            levelNumber = 4;
        else if (totalStarCount >= 454)
            levelNumber = 5;
        else if (totalStarCount >= 600)
            levelNumber = 6;
    }

    private void setLevelName() {
        if (levelNumber <= 5)
            levelName = "White Level";
        else if (levelNumber <= 10)
            levelName = "Blue Level";
        else if (levelNumber <= 15)
            levelName = "Purple";
        else if (levelNumber <= 20)
            levelName = "Brown Level";
        else if (levelNumber <= 25)
            levelName = "Black Level";
        else if (levelNumber <= 30)
            levelName = "Silver Level";
        else
            levelName = "Gold";


    }

    public static StarFragment newInstance(String title, String userName, String profileImageUrl, int starCount) {
        StarFragment starFragment = new StarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);
        bundle.putString("NAME", userName);
        bundle.putString("IMAGE_URL", profileImageUrl);
        bundle.putInt("STAR_COUNT", starCount);
        starFragment.setArguments(bundle);
        return starFragment;
    }

    @Override
    public void onResume() {
        ((NavigationActivity) getActivity()).setNavigationBackListener();
        ((NavigationActivity) getActivity()).showToolbarTextView(title);
        super.onResume();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_bluetooth).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
    }
}
