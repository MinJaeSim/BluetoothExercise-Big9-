package cc.foxtail.funkey.childInfo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cc.foxtail.funkey.OnChildChangeListener;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.data.ChildPhysicalData;
import cc.foxtail.funkey.data.ChildScoreData;
import cc.foxtail.funkey.dialog.InfoDialogFragment;
import cc.foxtail.funkey.navigation.NavigationActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChildInfoFragment extends Fragment implements OnChildChangeListener {
    private TextView weightTextView;
    private TextView heightTextView;
    private TextView bmiTextView;
    private List<DocumentSnapshot> childScoreData;
    private List<DocumentSnapshot> childrenData;
    private List<ChildScoreData> scoreData;
    private JSONArray jsonArray;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private RadarChart mChart;
    private CircleImageView profileImageView;
    private TextView predictTextView;
    private ImageView noChartImageView;

    private int averageHeight = 0;
    private int averageWeight = 0;
    private String age;
    private float weight;
    private float height;
    private String predictHeight;


    @SuppressLint("JavascriptInterface")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_info, container, false);
        setHasOptionsMenu(true);

        weight = getArguments().getInt("WEIGHT");
        height = getArguments().getInt("HEIGHT");
        predictHeight = getArguments().getString("PREDICT_HEIGHT");
        age = getArguments().getString("AGE");

        float bmi = (float) (weight / Math.pow((height / 100), 2));
        bmi = (float) (Math.round((bmi * 10)) / 10.0);
        final String name = getArguments().getString("NAME");

        noChartImageView = view.findViewById(R.id.no_chart_image_view);
        noChartImageView.setVisibility(View.GONE);

        predictTextView = view.findViewById(R.id.predict_height_value_text_view);
        predictTextView.setText(predictHeight);

        firebaseFirestore = FirebaseFirestore.getInstance();

        mChart = view.findViewById(R.id.radar_chart);
        mChart.setBackgroundColor(Color.parseColor("#EFEFEF"));

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.BLACK);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.BLACK);
        mChart.setWebAlpha(100);


        setData();

        mChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAxisMaximum(20);
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{getResources().getString(R.string.upper_body_strength_record),
                    getResources().getString(R.string.lower_body_strength_record), getResources().getString(R.string.core_record),
                    getResources().getString(R.string.muscle_endurance_record), getResources().getString(R.string.cardiopulmonary_endurance_record)};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.BLACK);


        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(20f);
        yAxis.setDrawLabels(false);

        mChart.getLegend().setEnabled(false);
        mChart.getDescription().setEnabled(false);

        readData(name);

        weightTextView = view.findViewById(R.id.weight_value_text_view);
        heightTextView = view.findViewById(R.id.height_value_text_view);
        bmiTextView = view.findViewById(R.id.bmi_value_text_view);

        ImageView bmiInfoButton = view.findViewById(R.id.bmi_detail_button);
        bmiInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DialogFragment dialogFragment = InfoDialogFragment.newInstance(R.layout.fragment_bmi_info_dialog);
                dialogFragment.show(fm, "InputDialog");
            }
        });

        ImageView predictHeightInfoButton = view.findViewById(R.id.height_detail_button);
        predictHeightInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DialogFragment dialogFragment = InfoDialogFragment.newInstance(R.layout.fragment_height_info_dialog);
                dialogFragment.show(fm, "InputDialog");
            }
        });

        profileImageView = view.findViewById(R.id.profile_image_view);


        Glide.with(profileImageView)
                .load(getArguments().getString("IMAGE_URL"))
                .apply(new RequestOptions().placeholder(R.drawable.child_image))
                .into(profileImageView);

        weightTextView.setText("" + weight);
        heightTextView.setText("" + height);
        bmiTextView.setText("" + bmi);
        return view;
    }

    private void setChildInfo(Child child) {

        float weight = child.getWeight();
        final float height = child.getHeight();
        float bmi = (float) Math.round(weight / Math.pow((height / 100), 2));


        weightTextView.setText("" + weight);
        heightTextView.setText("" + height);
        bmiTextView.setText("" + bmi);
    }

    public static ChildInfoFragment newInstance(Child child) {
        ChildInfoFragment childInfoFragment = new ChildInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("AGE", child.getAge());
        bundle.putInt("WEIGHT", child.getWeight());
        bundle.putInt("HEIGHT", child.getHeight());
        bundle.putString("PREDICT_HEIGHT", child.getPredictHeight());
        bundle.putString("NAME", child.getUserName());
        bundle.putString("IMAGE_URL", child.getProfileImageUrl());
        childInfoFragment.setArguments(bundle);
        return childInfoFragment;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_bluetooth).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
    }


    @Override
    public void onChange(Child child) {
        System.out.println("TEST info childName" + child.getUserName());
        setChildInfo(child);
        readData(child.getUserName());

        Glide.with(profileImageView)
                .load(child.getProfileImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.child_image)
                )
                .into(profileImageView);

    }

    private void readData(String name) {
        final int childAge = Integer.parseInt(age.substring(0, 1));
        Query childrenDataQuery = firebaseFirestore.collection("Children");

        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        Task<QuerySnapshot> task = childrenDataQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                childrenData = documentSnapshots.getDocuments();
                for (DocumentSnapshot d : childrenData) {
                    Child child = d.toObject(Child.class);
                    if (Integer.parseInt(child.getAge().substring(0, 1)) != childAge)
                        continue;
                    averageHeight += child.getHeight();
                    averageWeight += child.getWeight();
                }

                int num = childrenData.size() > 0 ? childrenData.size() : 1;

                averageHeight /= num;
                averageWeight /= num;

//                averageHeightTextView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (height >= averageHeight) {
//                            averageHeightTextView.setText("평균보다 " + (height - averageHeight) + "cm 큼");
//                        } else {
//                            averageHeightTextView.setText("평균보다 " + (averageHeight - height) + "cm 작음");
//                        }
//                    }
//                });
//                averageWeightTextView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (weight >= averageWeight) {
//                            averageWeightTextView.setText("평균보다 " + (weight - averageWeight) + "kg 무거움");
//                        } else {
//                            averageWeightTextView.setText("평균보다 " + (averageWeight - weight) + "kg 가벼움");
//                        }
//                    }
//                });
            }
        });


        tasks.add(task);

        Tasks.whenAll(tasks);
    }

    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }


    @Override
    public void onResume() {
        ((NavigationActivity) getActivity()).showToolbarSpinner();
        ((NavigationActivity) getActivity()).setNavigationBackListener();
        super.onResume();
    }

    public void setData() {
        DocumentSnapshot childPhysicalDataSnapshot = ((NavigationActivity) getActivity()).getChildPhysicalDocumentSnapShot();
        ChildPhysicalData childPhysicalData;
        if (childPhysicalDataSnapshot == null) {
            noChartImageView.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.GONE);
            return;
        } else {
            childPhysicalData = childPhysicalDataSnapshot.toObject(ChildPhysicalData.class);
            noChartImageView.setVisibility(View.GONE);
            mChart.setVisibility(View.VISIBLE);
        }


        ArrayList<RadarEntry> entries1 = new ArrayList<>();
        entries1.add(new RadarEntry(childPhysicalData.getStrengthUp()));
        entries1.add(new RadarEntry(childPhysicalData.getStrengthDown()));
        entries1.add(new RadarEntry(childPhysicalData.getCore()));
        entries1.add(new RadarEntry(childPhysicalData.getStrengthEndurance()));
        entries1.add(new RadarEntry(childPhysicalData.getLungCapacity()));

        RadarDataSet set1 = new RadarDataSet(entries1, "신체 정보");
        set1.setColor(Color.parseColor("#93D06B"));
        set1.setFillColor(Color.parseColor("#93D06B"));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.getXAxis().setAxisMaximum(20);
        mChart.getXAxis().setTextSize(9f);
        mChart.getXAxis().setYOffset(0f);
        mChart.getXAxis().setXOffset(0f);

        mChart.getYAxis().setTextSize(9f);
        mChart.getYAxis().setAxisMinimum(0f);
        mChart.getYAxis().setAxisMaximum(20f);

        mChart.setData(data);
        mChart.invalidate();


        System.out.println("chart Max" + mChart.getYChartMax());
    }

    @Override
    public void onDestroyView() {
        ((NavigationActivity) getActivity()).clearChildDataView();
        super.onDestroyView();
    }
}
