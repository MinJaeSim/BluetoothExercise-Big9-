package cc.foxtail.funkey.util;

public class ExerciseCounter8 extends ExerciseCounter {
    private boolean arm;
    private boolean legDown;
    private boolean legUp;
    private boolean motion;
    private boolean firstMotion;

    public ExerciseCounter8() {
        arm = false;
        legDown = false;
        legUp = false;
        firstMotion = false;
        motion = false;

        canCounting = true;
    }

    @Override
    public void counting() {
        boolean motionArmStretch = (countHelper.isLeftArmDownStretch() || countHelper.isLeftArmUpStretch())
                && (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch());
        boolean motionDown = countHelper.isLegDown();
        boolean motionUp = countHelper.isLegUp();


        if (motionArmStretch && canCounting) {
            System.out.println("동작 1");
            arm = true;
        }

        if (motionDown) {
            System.out.println("동작 2");
            legDown = true;
        }

        if (motionUp && arm) {
            System.out.println("동작 3");
            legUp = true;
        }

        if (arm && legDown && legUp)
            motion = true;

        if (motion && !firstMotion) {
            motion = false;
            motionArmStretch = motionDown = motionUp = false;
            arm = legDown = legUp = false;
            firstMotion = true;
            canCounting = false;
            return;
        }

        if (firstMotion && motion) {
            onExerciseCorrectListener.onExcellent();
            System.out.println("동작 성공!");
            arm = false;
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
        arm = false;
        legDown = false;
        legUp = false;
        firstMotion = false;
        motion = false;
        canCounting = false;
    }

}
