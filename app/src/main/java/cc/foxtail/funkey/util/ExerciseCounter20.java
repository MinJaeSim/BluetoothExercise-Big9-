package cc.foxtail.funkey.util;

public class ExerciseCounter20 extends ExerciseCounter {
    private boolean first;
    private boolean second;
    private boolean third;
    private boolean rightArm;
    private boolean leftArm;

    public ExerciseCounter20() {
        first = second = false;
        third = false;
        leftArm = false;
        rightArm = false;
    }

    @Override
    public void counting() {
        boolean motion1 = countHelper.isLegDown();
        boolean motion2 = countHelper.isTiltFront();
        boolean motion3 = countHelper.isLeftArmDownStretch();
        boolean motion3_2 = countHelper.isRightArmDownStretch();
        boolean motion4 = countHelper.isLegUp();

        if (motion1) {
            first = true;
            System.out.println("동작20 앉음");
        }

        if (first && motion2) {
            second = true;
            System.out.println("동작20 앞으로 숙임");
        }

        if (second && motion3) {
            leftArm = true;
            System.out.println("동작20 왼팔");
        }

        if (second && motion3_2) {
            rightArm = true;
            System.out.println("동작20 오른팔");
        }


        if (rightArm && leftArm && motion4) {
            third = true;
            System.out.println("동작20 일어남");
        }

        if (third) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작20 성공!");
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
