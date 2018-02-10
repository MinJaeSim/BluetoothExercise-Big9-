package cc.foxtail.funkey.util;

public class ExerciseCounter2 extends ExerciseCounter {

    private boolean firstMotion;
    private boolean secondMotion;


    public ExerciseCounter2() {
        firstMotion = false;
        secondMotion = false;
    }

    @Override
    public void counting() {

        boolean motion1 = countHelper.isRightArmUpStretch() && countHelper.isLeftArmDownStretch();
        boolean motion2 = countHelper.isRightArmDownStretch() && countHelper.isLeftArmUpStretch();

        if (motion1) {
            firstMotion = true;
        }

        if (motion2) {
            secondMotion = true;
        }

        if (firstMotion && secondMotion) {
            onExerciseCorrectListener.onExcellent();

            System.out.println("동작 2!");
            firstMotion = false;
            secondMotion = false;
        }
    }


    @Override
    public void init() {
        super.init();
        firstMotion = false;
        secondMotion = false;
    }
}
