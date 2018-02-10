package cc.foxtail.funkey.util;


public class ExerciseCounter7 extends ExerciseCounter {

    private boolean tiltFront;
    private boolean tiltBack;
    private boolean armFirtstStretch;
    private boolean armSecondStretch;

    public ExerciseCounter7() {
        tiltFront = false;
        tiltBack = false;
        armFirtstStretch = false;
        armSecondStretch = false;
        canCounting = true;
    }

    @Override
    public void counting() {
        boolean motion1 = ((countHelper.isLeftArmUpStretch() || countHelper.isLeftArmDownStretch()) && (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch()));
        boolean motion2_1 = countHelper.isTiltFront();
        boolean motion2_2 = countHelper.isTiltBack();

        if (motion2_1) {
            tiltFront = true;
            System.out.println("동작 7 앞으로");
        }

        if (motion2_2) {
            tiltBack = true;
            tiltFront = false;
            System.out.println("동작 7 뒤로");
        }

        if (canCounting && tiltFront && motion1) {
            armFirtstStretch = true;
            canCounting = false;
            System.out.println("동작 7 팔뻗기1");
        }

        if (armFirtstStretch && canCounting && tiltBack && motion1) {
            armSecondStretch = true;
            System.out.println("동작 7 팔뻗기2");
        }


        if (armFirtstStretch && armSecondStretch) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작7 성공!");
            tiltFront = false;
            tiltBack = false;
            armFirtstStretch = false;
            armSecondStretch = false;
        }

        if ((countHelper.isRightArmUpFold() || countHelper.isRightArmDownFold())
                && (countHelper.isLeftArmUpFold() || countHelper.isLeftArmDownFold())) {
            canCounting = true;
        }
    }


    @Override
    public void init() {
        super.init();
        tiltFront = false;
        tiltBack = false;
        armFirtstStretch = false;
        armSecondStretch = false;
        canCounting = true;
    }
}
