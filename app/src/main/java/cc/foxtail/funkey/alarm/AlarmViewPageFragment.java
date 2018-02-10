package cc.foxtail.funkey.alarm;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import cc.foxtail.funkey.GlideApp;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;

public class AlarmViewPageFragment extends Fragment {

    private AlarmContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_view_pager, container, false);

        String name = getArguments().getString("NAME");
        String imageUrl = getArguments().getString("URL");

        TextView nameTextView = view.findViewById(R.id.calendar_day_text_view);
        nameTextView.setText(name + "'s Alarm");

        PorterShapeImageView imageView = view.findViewById(R.id.user_image_view);

//        GlideApp.with(imageView)
//                .load(imageUrl)
//                .centerCrop()
//                .placeholder(R.drawable.child_image)
//                .into(imageView);

        Glide.with(imageView)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.child_image)
                )
                .into(imageView);

        RecyclerView recyclerView = view.findViewById(R.id.alarm_inner_recycler_view);
        Query query = FirebaseFirestore.getInstance().collection("Alarm").whereEqualTo("userName", name);
        InnerAlarmAdapter innerAlarmAdapter = new InnerAlarmAdapter(query);

        LinearLayoutManager alarmLayoutManager = new LinearLayoutManager(getContext());
        alarmLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), alarmLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(alarmLayoutManager);

        recyclerView.setAdapter(innerAlarmAdapter);
        presenter.setAdapter(innerAlarmAdapter);

        return view;
    }

    public static AlarmViewPageFragment newInstance(Child child) {
        AlarmViewPageFragment alarmViewPageFragment = new AlarmViewPageFragment();
        Bundle bundle = new Bundle();

        bundle.putString("NAME", child.getUserName());
        bundle.putString("URL", child.getProfileImageUrl());

        alarmViewPageFragment.setArguments(bundle);
        return alarmViewPageFragment;
    }

    public void setPresenter(AlarmContract.Presenter presenter) {
        this.presenter = presenter;
    }
}