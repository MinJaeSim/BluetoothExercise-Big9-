package cc.foxtail.funkey.childInfo;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.ChildScoreData;

public class ChildDetailInfoFragment extends Fragment {

    private ImageView calorieGraph;
    private ImageView scoreGraph;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private List<DocumentSnapshot> data;
    private List<ChildScoreData> scoreData;
    private JSONArray jsonArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_detail_info, container, false);
        setHasOptionsMenu(true);

        String name = getArguments().getString("NAME");
        progressDialog = new ProgressDialog(getContext());
        showProgressDialog("잠시만 기다려 주세요");

        firebaseFirestore = FirebaseFirestore.getInstance();

        calorieGraph = view.findViewById(R.id.calorie_graph);
        scoreGraph = view.findViewById(R.id.score_graph);

        readData(name);

        return view;
    }


    private void readData(String name) {
        Query query = firebaseFirestore.collection("Score").whereEqualTo("userName", name);

        Task<QuerySnapshot> task = query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                data = documentSnapshots.getDocuments();
                scoreData = new ArrayList<>();

                for (DocumentSnapshot d : data)
                    scoreData.add(d.toObject(ChildScoreData.class));

                jsonArray = new JSONArray();

                for (ChildScoreData data : scoreData) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("User", data.getUserName());
                        jsonObject.put("Date", data.getDate());
                        jsonObject.put("Score", data.getScore());
                        jsonObject.put("Stamina", data.getStamina());
                        jsonObject.put("flexibility", data.getFlexibility());
                        jsonObject.put("balance", data.getBalance());
                        jsonObject.put("strength", data.getStrength());
                        jsonObject.put("quick", data.getQuick());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);
                }
                String scoreUrl = "file:///android_asset/child_detail_info_fragment/detail_info_up.html";
                String calorieUrl = "file:///android_asset/child_detail_info_fragment/detail_info_down.html";
//                initWebView(calorieGraph, calorieUrl, jsonArray.toString());
//                initWebView(scoreGraph, scoreUrl, jsonArray.toString());
                dismissProgressDialog();

            }
        });
        Tasks.whenAll(task);
    }

    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    public static ChildDetailInfoFragment newInstance(String name) {
        ChildDetailInfoFragment fragment = new ChildDetailInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
}
