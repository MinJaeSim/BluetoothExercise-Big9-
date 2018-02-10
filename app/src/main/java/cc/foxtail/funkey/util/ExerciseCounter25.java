package cc.foxtail.funkey.util;

public class ExerciseCounter25 extends ExerciseCounter {
    private boolean firstArm;
    private boolean secondArm;
    private boolean thirdArm;
    private boolean legDown;
    private boolean legUp;
    private boolean motion;

    public ExerciseCounter25() {
        firstArm = secondArm = thirdArm = false;
        legDown = false;
        legUp = false;
        motion = false;

        canCounting = true;
    }

    @Override
    public void counting() {
        boolean motionDown = countHelper.isLegDown();
        boolean motionUp = countHelper.isLegUp();
        boolean motionArmStretch = (countHelper.isLeftArmDownStretch() || countHelper.isLeftArmUpStretch())
                && (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch());


        if (motionArmStretch && canCounting) {
            System.out.println("동작 1");
            firstArm = true;
        }

        if (motionDown) {
            System.out.println("동작 2");
            legDown = true;
        }

        if (motionUp && firstArm) {
            System.out.println("동작 3");
            legUp = true;
        }

        if (firstArm && legDown && legUp) {
            motion = true;
            canCounting = false;
        }

        if (motion) {

            if (motionArmStretch && canCounting) {
                System.out.println("동작 4");
                secondArm = true;
                canCounting = false;
                return;
            }

            if (secondArm && canCounting) {
                onExerciseCorrectListener.onExcellent();
                System.out.println("동작 성공!");
                legDown = false;
                legUp = false;
                motion = false;
                firstArm = secondArm = thirdArm = false;
            }
        }

        if ((countHelper.isRightArmUpFold() || countHelper.isRightArmDownFold())
                && (countHelper.isLeftArmUpFold() || countHelper.isLeftArmDownFold())) {
            canCounting = true;
        }
    }

    @Override
    public void init() {
        super.init();
        legDown = false;
        legUp = false;
        firstArm = secondArm = thirdArm = false;
        motion = false;
        canCounting = false;
    }

}
