package cc.foxtail.funkey.util;


public class ExerciseCounter9 extends ExerciseCounter {

    private boolean first;
    private boolean second;
    private boolean third;
    private boolean third_2;
    private boolean fourth;

    public ExerciseCounter9() {
        first = second = false;
        third = third_2 = false;
        fourth = false;
    }

    @Override
    public void counting() {
        boolean motion1 = countHelper.isLegDown();
        boolean motion2 = countHelper.isTiltBack();
        boolean motion3 = (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch())
                && (countHelper.isLeftArmUpStretch() || countHelper.isLeftArmDownStretch());
        boolean motion3_2 = countHelper.isTiltFront();
        boolean motion4 = countHelper.isLegUp();

        if (motion1) {
            first = true;
            System.out.println("동작9 앉음");
        }

        if (first && motion2) {
            second = true;
            System.out.println("동작9 누음");
        }

        if (second && motion3) {
            third = true;
            System.out.println("동작9 앞으로");
        }

        if (second && motion3_2) {
            third_2 = true;
            System.out.println("동작9 팔 펼침");
        }

        if (third && third_2 && motion4) {
            fourth = true;
            System.out.println("동작9 일어남");
        }


        if (fourth) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작9 성공!");
            first = second = false;
            third = third_2 = false;
            fourth = false;
        }
    }


    @Override
    public void init() {
        super.init();
        first = second = false;
        third = third_2 = false;
        fourth = false;
    }
}
