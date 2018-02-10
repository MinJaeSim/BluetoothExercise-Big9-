package cc.foxtail.funkey.util;

public class ExerciseCounter23 extends ExerciseCounter {
    private boolean first;
    private boolean second;
    private boolean third;
    private boolean rightArm;
    private boolean leftArm;

    public ExerciseCounter23() {
        first = second = false;
        third = false;
        leftArm = false;
        rightArm = false;
    }

    @Override
    public void counting() {
        boolean motion1 = countHelper.isLegDown();
        boolean motion2 = countHelper.isTiltFront();
        boolean motion3 = countHelper.isLeftArmUpStretch();
        boolean motion3_2 = countHelper.isRightArmUpStretch();
        boolean motion4 = countHelper.isLegUp();

        if (motion1) {
            first = true;
            System.out.println("동작23 앉음");
        }

        if (first && motion2) {
            second = true;
            System.out.println("동작23 앞으로 숙임");
        }

        if (second && motion3) {
            leftArm = true;
            System.out.println("동작23 왼팔");
        }

        if (second && motion3_2) {
            rightArm = true;
            System.out.println("동작23 오른팔");
        }


        if (rightArm && leftArm && motion4) {
            third = true;
            System.out.println("동작23 일어남");
        }

        if (third) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작23 성공!");
            first = second = false;
            third = false;
            leftArm = false;
            rightArm = false;
        }

    }

    @Override
    public void init() {
        super.init();
        first = second = false;
        third = false;
        leftArm = false;
        rightArm = false;
    }
}