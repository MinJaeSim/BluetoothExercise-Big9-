package cc.foxtail.funkey.util;


public class ExerciseCounter6 extends ExerciseCounter {

    private boolean firstMotion;

    public ExerciseCounter6() {
        firstMotion = false;
        canCounting = true;
    }

    @Override
    public void counting() {
        boolean motion1 = (countHelper.isLeftArmDownStretch() || countHelper.isLeftArmUpStretch())
                && (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch());

        if (motion1 && !firstMotion) {
            firstMotion = true;
            canCounting = false;
        }

        if (firstMotion && motion1 && canCounting) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작6 성공!");
            firstMotion = false;
        }

        if ((countHelper.isRightArmUpFold() || countHelper.isRightArmDownFold())
                && (countHelper.isLeftArmUpFold() || countHelper.isLeftArmDownFold())) {
            canCounting = true;
        }
    }


    @Override
    public void init() {
        super.init();
        firstMotion = false;
        canCounting = true;
    }
}
