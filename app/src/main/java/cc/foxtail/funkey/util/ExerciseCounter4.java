package cc.foxtail.funkey.util;


public class ExerciseCounter4 extends ExerciseCounter {
    private boolean first;
    private boolean second;
    private boolean second_2;

    public ExerciseCounter4() {
        first = false;
        second = false;
        second_2 = false;
    }

    @Override
    public void counting() {

        boolean motion1 = countHelper.isLegDown();
        boolean motion2 = countHelper.isLegUp();
        boolean motion2_2 = countHelper.isLeftArmUpStretch() && countHelper.isRightArmUpStretch();

        if (motion1) {
            first = true;
        }

        if (first && motion2) {
            second = true;
        }

        if (first && motion2_2) {
            second_2 = true;
        }

        if (second && second_2) {
            System.out.println("동작4 성공!");
            first = false;
            second = false;
            second_2 = false;
            onExerciseCorrectListener.onExcellent();
        }
    }


    @Override
    public void init() {
        super.init();
        first = false;
        second = false;
        second_2 = false;
    }
}
