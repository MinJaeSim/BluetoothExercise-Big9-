package cc.foxtail.funkey.util;


public class ExerciseCounter18 extends ExerciseCounter {
    private boolean first;
    private boolean second;
    private boolean third;
    private boolean armFirstMotion;
    private boolean armSecondMotion;

    public ExerciseCounter18() {
        first = second = false;
        third = false;
        armFirstMotion = armSecondMotion = false;
    }

    @Override
    public void counting() {
        boolean motion1 = countHelper.isLegDown();
        boolean motion2 = countHelper.isTiltFront();
        boolean motion3 = countHelper.isLegUp();
        boolean motion5 = countHelper.isRightArmUpStretch() && countHelper.isLeftArmDownStretch();
        boolean motion6 = countHelper.isRightArmDownStretch() && countHelper.isLeftArmUpStretch();

        if (motion1) {
            first = true;
            System.out.println("동작18 앉음");
        }

        if (first && motion2) {
            second = true;
            System.out.println("동작18 앞으로 숙임");
        }

        if (second && motion3) {
            third = true;
            System.out.println("동작18 일어남");
        }

        if (first && second && third && motion5) {
            armFirstMotion = true;
        }

        if (first && second && third && motion6) {
            armSecondMotion = true;
        }

        if (armFirstMotion && armSecondMotion) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작18 성공!");
            first = second = false;
            third = false;
            armFirstMotion = armSecondMotion = false;
        }
    }

    @Override
    public void init() {
        super.init();
        first = second = false;
        third = false;
        armFirstMotion = armSecondMotion = false;
    }
}
