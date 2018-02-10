package cc.foxtail.funkey.record;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.data.ChildPhysicalData;
import cc.foxtail.funkey.data.ExerciseTime;
import cc.foxtail.funkey.navigation.NavigationActivity;
import cc.foxtail.funkey.OnChildChangeListener;
import cc.foxtail.funkey.R;

public class RecordFragment extends Fragment implements OnChildChangeListener {
    private LineChart chart2;
    private LineChart chart1;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private List<DocumentSnapshot> data;

    private boolean isFriend;
    private ArrayList<Entry> firstEntryList;
    private ArrayList<Entry> secondEntryList;
    private ArrayList<String> xVal;
    private String name;
    private String uid;
    private List<ExerciseTime> exerciseTime;
    private List<ChildPhysicalData> childPhysicalDataList;
    private TextView commentTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        setHasOptionsMenu(true);

        name = getArguments().getString("NAME");
        uid = getArguments().getString("UID");
        isFriend = getArguments().getBoolean("FRIEND");

        progressDialog = new ProgressDialog(getContext());
        showProgressDialog("잠시만 기다려 주세요");

        firebaseFirestore = FirebaseFirestore.getInstance();

        firstEntryList = new ArrayList<>();
        secondEntryList = new ArrayList<>();
        xVal = new ArrayList<>();
        exerciseTime = new ArrayList<>();
        childPhysicalDataList = new ArrayList<>();

        chart1 = view.findViewById(R.id.score_graph);
        chart2 = view.findViewById(R.id.calorie_graph);
        commentTextView = view.findViewById(R.id.comment_text_view);

        readData(name, uid);

        return view;
    }

    private void initChart(final LineChart chart, int type) {
        chart.setViewPortOffsets(-40, 0, -40, 0);
        chart.setBackgroundColor(Color.WHITE);

        chart.getDescription().setEnabled(false);

        chart.setTouchEnabled(true);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setMaxHighlightDistance(100);

        XAxis x = chart.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x.setDrawAxisLine(false);
        x.setDrawGridLines(false);

        YAxis y = chart.getAxisLeft();
        y.setEnabled(false);
        y.setSpaceTop(30);
//        y.setSpaceBottom(10);


        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        chart.getAxisRight().setEnabled(false);

        if (type == 1) {
            setData(exerciseTime.size(), 1, chart);
            if (exerciseTime.size() < 6)
                chart.setVisibleXRange(0, exerciseTime.size() + 1);
            else
                chart.setVisibleXRange(0, 6);

            chart.moveViewToX(exerciseTime.size() - 2);
        } else if (type == 2) {
            setData(childPhysicalDataList.size(), 2, chart);

            if (childPhysicalDataList.size() < 6)
                chart.setVisibleXRange(0, childPhysicalDataList.size() + 1);
            else
                chart.setVisibleXRange(0, 6);

            chart.moveViewToX(childPhysicalDataList.size() - 2);
        }

        chart.getLegend().setEnabled(false);


        chart.invalidate();
    }

    private void setData(int count, int type, LineChart mChart) {
        if (type == 1) {
            firstEntryList.clear();
            xVal.clear();

            firstEntryList.add(new Entry(0, exerciseTime.get(0).getTime() / 1000 * 0.1f));
            xVal.add("");
            for (int i = 0; i < count; i++) {
                String month = exerciseTime.get(i).getDate().substring(2, 4);
                String day = exerciseTime.get(i).getDate().substring(4, 6);
                xVal.add(month + "월" + day + "일");
            }

            for (int i = 0; i < count; i++) {
                firstEntryList.add(new Entry(i + 1, exerciseTime.get(i).getTime() / 1000 * 0.1f, xVal.get(i)));
            }

            firstEntryList.add(new Entry(count + 1, exerciseTime.get(count - 1).getTime() / 1000 * 0.1f));
            xVal.add("");

            XAxis x = mChart.getXAxis();
            x.setValueFormatter(new IndexAxisValueFormatter(xVal));
        } else {
            secondEntryList.clear();
            xVal.clear();

            Collections.reverse(childPhysicalDataList);

            ChildPhysicalData fakeData = childPhysicalDataList.get(0);
            int fakeVal = fakeData.getCore() + fakeData.getLungCapacity() + fakeData.getStrengthDown() + fakeData.getStrengthUp() + fakeData.getStrengthEndurance();
            secondEntryList.add(new Entry(0, fakeVal));


            xVal.add("");
            SimpleDateFormat dayTime = new SimpleDateFormat("MM월 dd일", Locale.KOREA);
            for (int i = 0; i < count; i++) {
                xVal.add(dayTime.format(new Date(childPhysicalDataList.get(i).getDate())));
            }

            for (int i = 0; i < count; i++) {
                ChildPhysicalData data = childPhysicalDataList.get(i);
                int val = data.getCore() + data.getLungCapacity() + data.getStrengthDown() + data.getStrengthUp() + data.getStrengthEndurance();
                secondEntryList.add(new Entry(i + 1, val, xVal.get(i)));
            }

            fakeData = childPhysicalDataList.get(count - 1);
            fakeVal = fakeData.getCore() + fakeData.getLungCapacity() + fakeData.getStrengthDown() + fakeData.getStrengthUp() + fakeData.getStrengthEndurance();

            secondEntryList.add(new Entry(count + 1, fakeVal));
            xVal.add("");

            XAxis x = mChart.getXAxis();
            x.setValueFormatter(new IndexAxisValueFormatter(xVal));

            ChildPhysicalData lastScoreData = childPhysicalDataList.get(count - 1);
            int lastScore = lastScoreData.getCore() + lastScoreData.getLungCapacity() + lastScoreData.getStrengthDown() + lastScoreData.getStrengthUp() + lastScoreData.getStrengthEndurance();
            setComment(lastScore);
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            if (type == 1) {
                set1.setValues(firstEntryList);
            } else
                set1.setValues(secondEntryList);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            if (type == 1) {
                set1 = new LineDataSet(firstEntryList, "DataSet 1");
                set1.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return (int) value + " KCal";
                    }
                });
            } else {
                set1 = new LineDataSet(secondEntryList, "DataSet 1");
                set1.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return (int) value + " Point";
                    }
                });
            }
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setLineWidth(0f);
            set1.setCircleRadius(8f);
            set1.setCircleHoleRadius(6f);
            set1.setCircleColor(Color.rgb(255, 255, 255));
            set1.setCircleColorHole(Color.rgb(85, 137, 49));
            set1.setFillDrawable(getResources().getDrawable(R.drawable.chart_gradient_color));

            set1.setHighLightColor(Color.rgb(0, 0, 217));
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets

            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(true);

            // set data
            mChart.setData(data);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_bluetooth).setVisible(false);
//        menu.findItem(R.id.menu_rank).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);

        if (isFriend)
            ((NavigationActivity) getActivity()).showToolbarTextView(name + "의 기록");
    }

    @Override
    public void onChange(Child child) {
        System.out.println(child.getNickName());
        exerciseTime.clear();
        childPhysicalDataList.clear();
        readData(child.getUserName(), child.getUid());
    }

    private void setComment(int score) {
        if(score > 80)
            commentTextView.setText("그레잇!!! 완벽할 만큼 강한 체력이네요. 조금 더 강력한 트레이닝으로 체력를 높여보아요.^^");
        else if (score > 70)
            commentTextView.setText("와우~! 조금만 더 힘을 내 봐요~! 이대로라면 1등은 문제 없겠는걸요.^^");
        else if (score > 60)
            commentTextView.setText("잘하는데요.^^ 하지만 체력이 아직 부족한 부분이 있어요. 규칙적 운동으로 높일 수 있어요. 함께 해 보아요.^^");
        else
            commentTextView.setText("체력이 아직 낮아요ㅠㅠ 우리 꾸준한 운동으로 체력을 높여보아요.^^");
    }

    private void readData(String name, String uid) {
        Query firstQuery = firebaseFirestore.collection("Exercise_Time").whereEqualTo("uid", uid)
                .whereEqualTo("userName", name);

        Query secondQuery = firebaseFirestore.collection("Child_Physical_Data").whereEqualTo("uid", uid)
                .whereEqualTo("userName", name);

        Task<QuerySnapshot> firstTask = firstQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()) {
                    ExerciseTime data = new ExerciseTime();
                    data.setTime(0);
                    SimpleDateFormat dayTime = new SimpleDateFormat("yyMMdd", Locale.KOREA);
                    data.setDate(dayTime.format(System.currentTimeMillis()));
                    exerciseTime.add(data);
                    return;
                }
                for (DocumentSnapshot d : documentSnapshots) {
                    exerciseTime.add(d.toObject(ExerciseTime.class));
                }
            }
        });

        Task<QuerySnapshot> secondTask = secondQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()) {
                    ChildPhysicalData data = new ChildPhysicalData();
                    data.setStrengthUp(0);
                    data.setDate(System.currentTimeMillis());
                    childPhysicalDataList.add(data);

                    return;
                }

                for (DocumentSnapshot d : documentSnapshots) {
                    childPhysicalDataList.add(d.toObject(ChildPhysicalData.class));
                }

            }
        });


        Task<Void> t = Tasks.whenAll(firstTask, secondTask);
        t.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                initChart(chart1, 1);
                initChart(chart2, 2);
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onResume() {
        ((NavigationActivity) getActivity()).showToolbarSpinner();
        ((NavigationActivity) getActivity()).setNavigationBackListener();
        super.onResume();
    }

    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    public static RecordFragment newInstance(Child child) {
        RecordFragment fragment = new RecordFragment();

        Bundle bundle = new Bundle();
        bundle.putString("NAME", child.getUserName());
        bundle.putString("UID", child.getUid());

        boolean isFriend = !Objects.equals(child.getUid(), FirebaseAuth.getInstance().getUid());
        bundle.putBoolean("FRIEND", isFriend);

        fragment.setArguments(bundle);
        return fragment;
    }
}
