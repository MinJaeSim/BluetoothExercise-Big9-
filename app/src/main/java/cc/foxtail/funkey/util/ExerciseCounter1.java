package cc.foxtail.funkey.util;


public class ExerciseCounter1 extends ExerciseCounter {

    private boolean firstMotion;

    public ExerciseCounter1() {
        canCounting = true;
        firstMotion = false;
    }

    @Override
    public void counting() {

        boolean motion = (countHelper.isLeftArmUpStretch() || countHelper.isLeftArmDownStretch()) && (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch());

//            System.out.println("동작1 : " + firstMotion + " cancounting : " + canCounting);

        if (motion && canCounting)
            firstMotion = true;

        if (firstMotion) {
            System.out.println("동작 1");

            onExerciseCorrectListener.onExcellent();
//
//                System.out.println("동작1 : " + countHelper.isRightArmUpStretch());
//                System.out.println("동작1 : " + countHelper.isRightArmDownStretch());
//                System.out.println("동작1 : " + countHelper.isLeftArmUpStretch());
//                System.out.println("동작1 : " + countHelper.isLeftArmDownStretch());
//                System.out.println("동작1 :  + -------------");
//                System.out.println("동작1 : " + countHelper.isRightArmUpFold());
//                System.out.println("동작1 : " + countHelper.isRightArmDownFold());
//                System.out.println("동작1 : " + countHelper.isLeftArmUpFold());
//                System.out.println("동작1 : " + countHelper.isLeftArmDownFold());
//
            canCounting = false;
            firstMotion = false;
        }


        if ((countHelper.isRightArmUpFold() || countHelper.isRightArmDownFold())
                && (countHelper.isLeftArmUpFold() || countHelper.isLeftArmDownFold())) {
            canCounting = true;
//                System.out.println("cancounting init");
        }
    }


    @Override
    public void init() {
        super.init();
//        canCounting = true;
        firstMotion = false;
//        countHelper.initArmFlag();
    }
}