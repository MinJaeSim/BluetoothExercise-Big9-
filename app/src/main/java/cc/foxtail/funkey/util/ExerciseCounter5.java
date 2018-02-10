package cc.foxtail.funkey.util;


public class ExerciseCounter5 extends ExerciseCounter {
    private boolean first;
    private boolean second;

    public ExerciseCounter5() {
        first = false;
        second = false;
    }

    @Override
    public void counting() {

        boolean motion1 = countHelper.isLegDown();
        boolean motion1_2 = (countHelper.isLeftArmDownStretch() || countHelper.isLeftArmUpStretch())
                && (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch());
        boolean motion2 = countHelper.isLegUp();

        if (motion1) {
            first = true;
            System.out.println("동작5 1");
        }

        if (first && motion1_2) {
            second = true;
            System.out.println("동작5 2");
        }


        if (motion2 && second) {
            System.out.println("동작5 성공!");
            first = false;
            second = false;
            onExerciseCorrectListener.onExcellent();
        }
    }


    @Override
    public void init() {
        super.init();
        first = false;
        second = false;
    }
}