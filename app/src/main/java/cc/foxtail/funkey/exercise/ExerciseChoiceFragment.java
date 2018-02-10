package cc.foxtail.funkey.exercise;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.deviceConnect.DeviceConnectActivity;
import cc.foxtail.funkey.navigation.NavigationActivity;

import static android.app.Activity.RESULT_OK;
import static cc.foxtail.funkey.navigation.NavigationActivity.deviceAddress;
import static cc.foxtail.funkey.navigation.NavigationActivity.soundId;
import static cc.foxtail.funkey.navigation.NavigationActivity.soundPool;

public class ExerciseChoiceFragment extends Fragment {
    private static final int REQUEST_ENABLE_BLUETOOTH = 100;
    private static final int REQUEST_LOCATION_ACCESS = 101;

    private PieChart basicCourseChart;
    private PieChart personalCourseChart;
    private static final int BMI_MODEL = 3;
    private static final int PT_MODEL = 4;
    private static final int DEVICE_CONNECT_REQUEST_CODE = 444;
    private static final int PLAY_MODE_REQUEST_CODE = 700;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_choice, container, false);

        basicCourseChart = view.findViewById(R.id.basic_course_pie_chart);
        personalCourseChart = view.findViewById(R.id.personal_course_pie_chart);
        Button funkeyCourseButton = view.findViewById(R.id.funkey_course_button);
        final FragmentManager fragmentManager = getFragmentManager();

        funkeyCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                if (deviceAddress == null) {
                    System.out.println("address is null");
                    enableBlueTooth();
                    return;
                }

                Fragment exerciseFragment = new ExerciseFragment();
                fragmentManager.beginTransaction().replace(R.id.content_fragment, exerciseFragment).addToBackStack(null).commit();

                ((NavigationActivity) getActivity()).setNavigationBackListener();
            }
        });

        Button bmiCourseButton = view.findViewById(R.id.bmi_course_button);
        bmiCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                if (deviceAddress == null) {
                    System.out.println("address is null");
                    enableBlueTooth();
                    return;
                }

                Fragment bmiExerciseFragment = new BmiExerciseFragment();
                fragmentManager.beginTransaction().replace(R.id.content_fragment, bmiExerciseFragment).addToBackStack(null).commit();

            }
        });

        Button personalCourseButton = view.findViewById(R.id.personal_course_button);
        personalCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                if (deviceAddress == null) {
                    System.out.println("address is null");
                    enableBlueTooth();
                    return;
                }

                Fragment exerciseFragment = new PTExerciseFragment();
                fragmentManager.beginTransaction().replace(R.id.content_fragment, exerciseFragment).addToBackStack(null).commit();
            }
        });

        float percent = 0;

        Child child = ((NavigationActivity) getActivity()).getCurrentChild();
//        for (Integer score : child.getStageScore()) {
//            if (score > 0)
//                percent += 3.5;
//        }

        for (int i = 0; i <= 27; i++) {
            int score = child.getStageScore().get(i);
            if (score > 0)
                percent += 3.5;

            if (percent >= 100)
                percent = 100;
        }

        setChart(basicCourseChart, percent, Color.parseColor("#93D06B"), Color.parseColor("#c1c2be"));
        basicCourseChart.setCenterText(generateCenterSpannableText(percent + "%"));
//        setChart(personalCourseChart, 50, Color.parseColor("#8f649a"), Color.parseColor("#1176AA"));
        setChart(personalCourseChart, 50, Color.parseColor("#454b8a"), Color.parseColor("#8f649a"));
        personalCourseChart.setCenterText(generatePersonalTrainingCenterSpannableText("BMI/Personal"));
        return view;
    }

    private void enableBlueTooth() {
        checkPermissions();
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_ACCESS);
            }
        }
    }

    private void startBmiUnityActivity(float bmi, String age) {
        Intent intent = new Intent(getContext(), UnityActivity.class);
        intent.putExtra("DEVICE_ADDRESS", deviceAddress);
        intent.putExtra("EXERCISE_MODEL_TYPE", BMI_MODEL);
        intent.putExtra("BMI_TYPE", getBmiStatus(bmi));
        intent.putExtra("ENABLE_STAGE", 5);
        intent.putExtra("CHILD_AGE", age);
        String gender = ((NavigationActivity) getActivity()).getCurrentChild().getSex();
        intent.putExtra("GENDER", gender);
        startActivityForResult(intent, PLAY_MODE_REQUEST_CODE);
    }

    private void startPTUnityActivity(String age) {
        Intent intent = new Intent(getContext(), UnityActivity.class);
        intent.putExtra("DEVICE_ADDRESS", deviceAddress);
        intent.putExtra("EXERCISE_MODEL_TYPE", PT_MODEL);
        intent.putExtra("CHILD_AGE", age);
        String gender = ((NavigationActivity) getActivity()).getCurrentChild().getSex();
        intent.putExtra("GENDER", gender);
        startActivityForResult(intent, PLAY_MODE_REQUEST_CODE);
    }

    @Override
    public void onResume() {
        ((NavigationActivity) getActivity()).showToolbarTextView(getResources().getString(R.string.choose_exercise));
        super.onResume();
    }

    private void setChart(PieChart chart, float percent, int color1, int color2) {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

//        chart.setCenterText(generateCenterSpannableText(percent + "%"));

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(75f);
        chart.setTransparentCircleRadius(77f);
        chart.setDrawCenterText(true);

        chart.setRotationAngle(-90);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener

        setData(chart, percent, color1, color2);

        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = chart.getLegend();
        l.setEnabled(false);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
    }

    private void setData(PieChart chart, float percent, int color1, int color2) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        entries.add(new PieEntry(percent, getResources().getDrawable(R.drawable.chart_gradient_color)));
        entries.add(new PieEntry(100 - percent, getResources().getDrawable(R.drawable.chart_gradient_color)));

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setDrawIcons(true);
        dataSet.setSliceSpace(1f);
        dataSet.setIconsOffset(new MPPointF(0, 0));
        dataSet.setSelectionShift(1f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
//        Color.parseColor("#93D06B")
        colors.add(color1);
        colors.add(color2);


        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    private SpannableString generateCenterSpannableText(String percent) {

        SpannableString s = new SpannableString(percent + "\nComplete");
        s.setSpan(new RelativeSizeSpan(2.5f), 0, 5, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, 5, 0);

        s.setSpan(new ForegroundColorSpan(Color.BLACK), 6, s.length(), 0); //developed by
        s.setSpan(new RelativeSizeSpan(0.7f), 6, s.length(), 0);
        return s;
    }

    private SpannableString generatePersonalTrainingCenterSpannableText(String text) {
        SpannableString s = new SpannableString(text);
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 12, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, 12, 0);

        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 12, 0); //developed by
//        s.setSpan(new RelativeSizeSpan(0.7f), 6, s.length(), 0);
        return s;
    }

    private String getBmiStatus(float bmi) {
        if (bmi > 35) {
            return "bmi_extremely_obese";
        } else if (bmi > 30) {
            return "bmi_obese";
        } else if (bmi > 25) {
            return "bmi_over_weight";
        } else if (bmi > 18.5) {
            return "bmi_normal";
        } else {
            return "bmi_under_weight";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                startActivityForResult(new Intent(getContext(), DeviceConnectActivity.class), DEVICE_CONNECT_REQUEST_CODE);
            }
        }

        if (requestCode == DEVICE_CONNECT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                deviceAddress = data.getStringExtra("ADDRESS");
            } else {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        }
    }
}
