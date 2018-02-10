package cc.foxtail.funkey.util;


public class ExerciseCounter15 extends ExerciseCounter {

    private boolean leftArm;
    private boolean rightArm;
    private boolean legDown;
    private boolean leftMotion;
    private boolean rightMotion;

    public ExerciseCounter15() {
        leftArm = false;
        rightArm = false;
        legDown = false;
        rightMotion = false;
        leftMotion = false;

        canCounting = true;
    }

    @Override
    public void counting() {
        boolean motionRightArmStretch = countHelper.isRightArmUpStretch();
        boolean motionLeftArmStretch = countHelper.isLeftArmUpStretch();
        boolean motionDown = countHelper.isLegDown();

        if (motionRightArmStretch && canCounting) {
            rightArm = true;
        }

        if (motionLeftArmStretch && canCounting) {
            leftArm = true;
        }

        if (motionDown) {
//                System.out.println("동작 2");
            legDown = true;
        }


        if (leftArm && legDown) {
            leftMotion = true;
            legDown = false;
            System.out.println("동작 1 왼팔");
        }

        if (rightArm && legDown) {
            rightMotion = true;
            legDown = false;
            System.out.println("동작 1 오른팔");
        }

//            if (leftMotion && !firstMotion) {
//                rightMotion = false;
//                leftMotion = false;
//                motionLeftArmStretch = motionRightArmStretch = motionDown = false;
//                leftArm = rightArm = legDown = legUp = false;
//                firstMotion = true;
//                canCounting = false;
//                return;
//            }

        if (rightMotion && leftMotion) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작 성공!");
            leftArm = false;
            rightArm = false;
            legDown = false;
            leftMotion = false;
            rightMotion = false;
        }

        if ((countHelper.isRightArmUpFold() || countHelper.isRightArmDownFold())
                && (countHelper.isLeftArmUpFold() || countHelper.isLeftArmDownFold())) {
            canCounting = true;
        }
    }

    @Override
    public void init() {
        super.init();
        leftArm = false;
        rightArm = false;
        legDown = false;
        rightMotion = false;
        leftMotion = false;
        canCounting = false;
    }
}
