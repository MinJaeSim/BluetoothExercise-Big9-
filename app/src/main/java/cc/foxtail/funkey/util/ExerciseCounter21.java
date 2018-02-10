package cc.foxtail.funkey.util;

public class ExerciseCounter21 extends ExerciseCounter {
    private boolean first;
    private boolean second;
    private boolean second_2;
    private boolean third;

    public ExerciseCounter21() {
        first = false;
        second = false;
        second_2 = false;
        third = false;
    }

    @Override
    public void counting() {
        boolean motion1 = countHelper.isLegDown();
        boolean motion2 = countHelper.isLegUp();
        boolean motion2_2 = (countHelper.isLeftArmDownStretch() || countHelper.isLeftArmUpStretch())
                && (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch());

        if (motion1) {
            first = true;
//                System.out.println("동작21 1");
        }

        if (first && motion2) {
            second = true;
//                System.out.println("동작21 2");
        }

        if (first && motion2_2) {
            second_2 = true;
            System.out.println("동작21 3");
        }

        if (second && second_2 && (countHelper.isRightArmUpFold() || countHelper.isRightArmDownFold())
                && (countHelper.isLeftArmUpFold() || countHelper.isLeftArmDownFold())) {
            third = true;
            System.out.println("동작21 4");
        }

        if (second_2 && third) {
            System.out.println("동작21 성공!");
            first = false;
            second = false;
            second_2 = false;
            third = false;
            onExerciseCorrectListener.onExcellent();
        }
    }

    @Override
    public void init() {
        super.init();
        first = false;
        second = false;
        second_2 = false;
        third = false;
    }
}
