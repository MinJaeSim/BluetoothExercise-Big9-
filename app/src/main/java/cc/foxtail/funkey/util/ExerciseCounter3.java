package cc.foxtail.funkey.util;


public class ExerciseCounter3 extends ExerciseCounter {
    private boolean first;
    private boolean second;
    private boolean tilt;

    public ExerciseCounter3() {
        first = false;
        second = false;
        tilt = false;
        canCounting = true;
    }

    @Override
    public void counting() {

        boolean motion1 = countHelper.isLeftArmUpStretch() && countHelper.isRightArmUpStretch();
        boolean motion2 = (countHelper.isLeftArmDownStretch() || countHelper.isLeftArmUpStretch())
                && (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch());
        boolean motion3 = countHelper.isTiltFront();

        if (motion1 && !first) {
            System.out.println("동작 1");
            first = true;
            canCounting = false;
        }

        if (first && motion1 && canCounting) {
            System.out.println("동작 2");
            second = true;
        }

        if (second && motion3) {
            System.out.println("동작 기울");
            tilt = true;
        }

        if (second && tilt && motion2) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작 3!");
            first = false;
            second = false;
            tilt = false;
        }

        if ((countHelper.isRightArmUpFold() || countHelper.isRightArmDownFold())
                && (countHelper.isLeftArmUpFold() || countHelper.isLeftArmDownFold())) {
            canCounting = true;
        }
    }


    @Override
    public void init() {
        super.init();
        first = false;
        second = false;
        tilt = false;
        canCounting = true;
    }
}
