package cc.foxtail.funkey;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.foxtail.funkey.exercise.StageButtonClickListener;
import cc.foxtail.funkey.data.Stage;

import static cc.foxtail.funkey.navigation.NavigationActivity.soundId;
import static cc.foxtail.funkey.navigation.NavigationActivity.soundPool;

public class StageButtonAdapter extends RecyclerView.Adapter<StageButtonAdapter.StageButtonViewHolder> {

    private List<Stage> stageList = new ArrayList<>();
    private List<Integer> scoreList = new ArrayList<>();
    private StageButtonClickListener stageButtonClickListener;

    public StageButtonAdapter(List<Stage> stageList, List<Integer> scoreList, StageButtonClickListener stageButtonClickListener) {
        this.stageButtonClickListener = stageButtonClickListener;
        this.stageList = stageList;
        this.scoreList = scoreList;
    }

    @Override
    public StageButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StageButtonViewHolder(inflater.inflate(R.layout.stage_button, parent, false), stageButtonClickListener);
    }


    @Override
    public void onBindViewHolder(StageButtonViewHolder holder, int position) {
        holder.bindStageInfo(stageList.get(position), scoreList.get(position + 1));
    }

    @Override
    public int getItemCount() {
        return stageList.size();
    }

    public void setStageList(List<Stage> stageList) {
        this.stageList = stageList;
    }

    public void setScoreList(List<Integer> scoreList) {
        this.scoreList = scoreList;
    }

    public static class StageButtonViewHolder extends RecyclerView.ViewHolder {

        private TextView stageNameTextView;
        private RatingBar stageRatingBar;
        private int stageNumber;
        private Stage stage;

        public StageButtonViewHolder(View itemView, final StageButtonClickListener stageButtonClickListener) {
            super(itemView);

            stageNameTextView = itemView.findViewById(R.id.stage_text_view);
            stageRatingBar = itemView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    soundPool.play(soundId, 1, 1, 0, 0, 1);
                    stageButtonClickListener.onClick(stage);
                }
            });
        }

        public void bindStageInfo(Stage stage, int score) {
            this.stage = stage;
            stageNameTextView.setText(stage.getStageName());
            stageNumber = stage.getStageNumber();
            stageRatingBar.setRating(score);


            if (score == 0) {
                itemView.setEnabled(true);
//                System.out.println(stage.getStageName() + " score : " + score);
            } else {
                itemView.setEnabled(false);
                stageNameTextView.setText("운동 완료!");
//                System.out.println(stage.getStageName() + " score : " + score);
            }
        }

    }
}
