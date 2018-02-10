package cc.foxtail.funkey.util;

public class ExerciseCounter27 extends ExerciseCounter {
    private boolean leftArm;
    private boolean rightArm;
    private boolean legDown;
    private boolean legUp;
    private boolean motion;
    private boolean firstMotion;

    public ExerciseCounter27() {
        leftArm = false;
        rightArm = false;
        legDown = false;
        legUp = false;
        firstMotion = false;
        motion = false;

        canCounting = true;
    }

    @Override
    public void counting() {
        boolean motionLeftArmStretch = countHelper.isLeftArmUpStretch() && countHelper.isRightArmDownStretch();
        boolean motionRightArmStretch = countHelper.isLeftArmDownStretch() && countHelper.isRightArmUpStretch();
        boolean motionDown = countHelper.isLegDown();
        boolean motionUp = countHelper.isLegUp();


        if (motionLeftArmStretch && canCounting) {
            System.out.println("동작 1");
            leftArm = true;
        }

        if (motionRightArmStretch && canCounting) {
            System.out.println("동작 1-1");
            rightArm = true;
        }

        if (motionDown) {
            System.out.println("동작 2");
            legDown = true;
        }

        if (motionUp && (leftArm || rightArm)) {
            System.out.println("동작 3");
            legUp = true;
        }

        if (leftArm && rightArm && legDown && legUp)
            motion = true;

        if (motion && !firstMotion) {
            motion = false;
            motionRightArmStretch = motionLeftArmStretch = motionDown = motionUp = false;
            rightArm = leftArm = legDown = legUp = false;
            firstMotion = true;
            canCounting = false;
            return;
        }

        if (firstMotion && motion) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작 성공!");
            leftArm = false;
            rightArm = false;
            legDown = false;
            legUp = false;
            motion = false;
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
        leftArm = false;
        rightArm = false;
        legDown = false;
        legUp = false;
        firstMotion = false;
        motion = false;
        canCounting = false;
    }

}