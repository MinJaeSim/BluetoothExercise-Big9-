package cc.foxtail.funkey.util;


public class ExerciseCounter99 extends ExerciseCounter {

    // 근지구력 용 알고리즘

    private boolean first;

    public ExerciseCounter99() {
        first = false;
    }

    @Override
    public void counting() {
        long exerciseTime = System.currentTimeMillis() - startTime;
        if (exerciseTime > exerciseLimitTime) {
            System.out.println("EXERCISE TIME " + exerciseTime);
            System.out.println("EXERCISE LIMIT TIME " + exerciseLimitTime);

            onExerciseCorrectListener.onFail();
        } else {
            boolean motion1 = (countHelper.isRightArmUpStretch() || countHelper.isRightArmDownStretch())
                                && (countHelper.isLeftArmUpStretch() || countHelper.isLeftArmDownStretch());

            if (canCounting) {
                if (motion1)
                    first = true;
                if (first) {
                    onExerciseCorrectListener.onExcellent();
                    System.out.println("동작 9!");
                    first = false;
                    canCounting = false;
                }
            }

            if (!canCounting && (countHelper.isLeftArmUpFold() || countHelper.isLeftArmDownFold())
                    && (countHelper.isRightArmUpFold() || countHelper.isRightArmDownFold())) {
                canCounting = true;
                countHelper.initArmFlag();
            }
        }
    }
}
