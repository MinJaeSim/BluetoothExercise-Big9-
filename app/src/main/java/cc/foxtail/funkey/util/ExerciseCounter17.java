package cc.foxtail.funkey.util;


public class ExerciseCounter17 extends ExerciseCounter {
    private boolean first;
    private boolean second;
    private boolean third;

    public ExerciseCounter17() {
        first = second = false;
        third = false;
    }

    @Override
    public void counting() {
        boolean motion1 = countHelper.isLegDown();
        boolean motion2 = countHelper.isTiltFront();
        boolean motion3 = countHelper.isLegUp();
        boolean motion4 = (countHelper.isLeftArmUpStretch() || countHelper.isLeftArmDownStretch()) &&
                (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch());

        if (motion1) {
            first = true;
            System.out.println("동작17 앉음");
        }

        if (first && motion2) {
            second = true;
            System.out.println("동작17 앞으로 숙임");
        }

        if (second && motion3) {
            third = true;
            System.out.println("동작17 일어남");
        }

        if (third && motion4) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작17 성공!");
            first = second = false;
            third = false;
        }
    }

    @Override
    public void init() {
        super.init();
        first = second = false;
        third = false;
    }
}
