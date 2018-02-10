package cc.foxtail.funkey.util;

import java.util.Arrays;

import static cc.foxtail.funkey.data.ProtocolConstants.ARM_LEFT_DOWN_FOLD;
import static cc.foxtail.funkey.data.ProtocolConstants.ARM_LEFT_DOWN_STRETCH;
import static cc.foxtail.funkey.data.ProtocolConstants.ARM_LEFT_UP_FOLD;
import static cc.foxtail.funkey.data.ProtocolConstants.ARM_LEFT_UP_STRETCH;
import static cc.foxtail.funkey.data.ProtocolConstants.ARM_RIGHT_DOWN_FOLD;
import static cc.foxtail.funkey.data.ProtocolConstants.ARM_RIGHT_DOWN_STRETCH;
import static cc.foxtail.funkey.data.ProtocolConstants.ARM_RIGHT_UP_FOLD;
import static cc.foxtail.funkey.data.ProtocolConstants.ARM_RIGHT_UP_STRETCH;


public class ExerciseCountHelper {

    private boolean rightArmUpStretch = false;
    private boolean rightArmUpFold = true;
    private boolean leftArmUpStretch = false;
    private boolean leftArmUpFold = true;

    private boolean rightArmDownStretch = false;
    private boolean rightArmDownFold = true;
    private boolean leftArmDownStretch = false;
    private boolean leftArmDownFold = true;

    private boolean legDown;
    private boolean legUp;
    private boolean rotateRight;
    private boolean rotateLeft;
    private boolean tiltFront;
    private boolean tiltBack;
    private boolean tiltRight;
    private boolean tiltLeft;

    private float angleX;
    private float angleY;
    private float angleZ;
    private float filteredX;
    private float filteredY;
    private float filteredZ;

    private boolean countingTiltX;
    private boolean countingTiltY;
    private boolean countingTiltZ;

    private KalManFilter kalmanAngleX;
    private KalManFilter kalmanAngleY;
    private KalManFilter kalmanAngleZ;

    private int velocityX;

    private static final float ANGLE_X_SENSITIVITY = 1; // 값이 작을수록 민감해짐, 최대 2
    private static final double ANGLE_Y_SENSITIVITY = 0.4; // 값이 작을수록 민감해짐, 최대 1
    private static final double ANGLE_Z_SENSITIVITY = 0.2; // 값이 작을수록 민감해짐, 최대 1

    private static final double VELOCITY_SENSITIVITY = 6; // 값이 작을수록 민감해짐
    private static final double ALPHA = 0.7; // 앉았다 일어났다 민감도 조절 알파값에 비례, 최대 1

    public ExerciseCountHelper() {
        this.initArmFlag();
        this.initLegFlag();
        rightArmUpFold = true;
        leftArmUpFold = true;
        rightArmDownFold = true;
        leftArmDownFold = true;
        angleX = -3;
        angleY = 0;
        angleZ = 0;
        kalmanAngleX = new KalManFilter(0);
        kalmanAngleY = new KalManFilter(0);
        kalmanAngleZ = new KalManFilter(0);

        velocityX = 0;
    }

    public void checkProtocol(byte[] commandProtocol) {
        if (Arrays.equals(commandProtocol, ARM_RIGHT_UP_STRETCH)) {
            System.out.println("오른팔 위로 펼침");
            rightArmUpStretch = true;
            rightArmUpFold = false;
            rightArmDownFold = false;
        } else if (Arrays.equals(commandProtocol, ARM_RIGHT_UP_FOLD)) {
            System.out.println("오른팔 접음");
            if (rightArmUpStretch || rightArmDownStretch) {
                rightArmUpFold = true;
//                rightArmDownFold = true;
                rightArmUpStretch = false;
            }
        } else if (Arrays.equals(commandProtocol, ARM_LEFT_UP_STRETCH)) {
            System.out.println("왼팔 위로 펼침");
            leftArmUpStretch = true;
            leftArmUpFold = false;
            leftArmDownFold = false;
        } else if (Arrays.equals(commandProtocol, ARM_LEFT_UP_FOLD)) {
            System.out.println("왼팔 접음");
            if (leftArmUpStretch || leftArmDownStretch) {
                leftArmUpFold = true;
                leftArmUpStretch = false;
            }
        }

        if (Arrays.equals(commandProtocol, ARM_RIGHT_DOWN_STRETCH)) {
            System.out.println("오른팔 아래로 펼침");
            rightArmDownStretch = true;
            rightArmDownFold = false;
            rightArmUpFold = false;
        } else if (Arrays.equals(commandProtocol, ARM_RIGHT_DOWN_FOLD)) {
            System.out.println("오른팔 접음");
            if (rightArmDownStretch || rightArmUpStretch) {
                rightArmDownFold = true;
                rightArmDownStretch = false;
            }
        } else if (Arrays.equals(commandProtocol, ARM_LEFT_DOWN_STRETCH)) {
            System.out.println("왼팔 아래로 펼침");
            leftArmDownStretch = true;
            leftArmDownFold = false;
            leftArmUpFold = false;
        } else if (Arrays.equals(commandProtocol, ARM_LEFT_DOWN_FOLD)) {
            System.out.println("왼팔 접음");
            if (leftArmDownStretch || leftArmUpStretch) {
                leftArmDownFold = true;
                leftArmDownStretch = false;
            }
        }
    }

    public void setAngleX(float aX) {
        angleX = aX;
        if (kalmanAngleX != null && (angleX > 0 || angleX < -1))
            filteredX = (float) kalmanAngleX.update(angleX);
        this.angleX = (float) (-3 + (filteredX * 0.15));
        filteredX = 0;
        checkTiltX();
    }

    public void setAngleY(float angleY) {
        this.angleY = angleY;
        if (kalmanAngleY != null && (angleY > 0 || angleY < -1))
            filteredY = (float) kalmanAngleY.update(angleY);
        this.angleY = (float) (filteredY * 0.15);
        filteredY = 0;
        checkTiltY();
    }

    public void setAngleZ(float angleZ) {
        this.angleZ = angleZ;
        if (kalmanAngleZ != null && (angleZ > 0 || angleZ < -1))
            filteredZ = (float) kalmanAngleZ.update(angleZ);
        this.angleZ = (float) (filteredZ * 0.15);
        filteredZ = 0;
        checkTiltZ();
    }

    private boolean isAngleXEqualization = false;
    private float standardAngleX = 0;
    private int angleXCount = 0;

    private void checkTiltX() {
        while (!isAngleXEqualization) {
            standardAngleX += angleX;
            angleXCount++;

            if (angleXCount == 25) {
                isAngleXEqualization = true;
                standardAngleX /= 25;
            } else {
                return;
            }
        }

        if (countingTiltX) {
            if (angleX - standardAngleX > 1) {
                rotateRight = false;
                rotateLeft = true;
                countingTiltX = false;
            }
            if (angleX - standardAngleX < -1) {
                rotateRight = true;
                rotateLeft = false;
                countingTiltX = false;
            }
        } else if (-0.5 < angleX - standardAngleX && angleX - standardAngleX < 0.5) {
            countingTiltX = true;
            rotateLeft = false;
            rotateRight = false;
        }
    }

    private boolean isAngleYEqualization = false;
    private float standardAngleY = 0;
    private int angleYCount = 0;


    private void checkTiltY() {

        while (!isAngleYEqualization) {
            standardAngleY += angleY;
            angleYCount++;

            if (angleYCount == 25) {
                isAngleYEqualization = true;
                standardAngleY /= 25;
            } else {
                return;
            }
        }

        if (countingTiltY) {
            if (angleY - standardAngleY > ANGLE_Y_SENSITIVITY) {
//                System.out.println("앞으로 숙임");
                tiltFront = true;
                tiltBack = false;
                countingTiltY = false;
            }
            if (angleY - standardAngleY < -ANGLE_Y_SENSITIVITY) {
//                System.out.println("뒤로 숙임");
                rotateRight = false;
                tiltBack = true;
                countingTiltY = false;
            }
        } else if (ANGLE_Y_SENSITIVITY > angleY - standardAngleY && angleY - standardAngleY > -ANGLE_Y_SENSITIVITY) {
            countingTiltY = true;
            tiltFront = false;
            tiltBack = false;
        }
    }

    private void checkTiltZ() {
        if (countingTiltZ) {
            if (angleZ > ANGLE_Z_SENSITIVITY) {
//                System.out.println("좌로 기움");
                tiltLeft = true;
                tiltRight = false;
                countingTiltZ = false;
            }
            if (angleZ < -ANGLE_Z_SENSITIVITY) {
//                System.out.println("우로 기움");
                tiltLeft = false;
                tiltRight = true;
                countingTiltZ = false;
            }
        } else if (0.1 > angleZ && angleZ > -0.1) {
            countingTiltZ = true;
            tiltFront = false;
            tiltBack = false;
        }
    }

    public void calculateVelocity(int accX) {
        if (!(3 < accX && accX < 5)) {
            double gravity = ALPHA * 4 + (1 - ALPHA) * accX;
            velocityX = (int) (accX - gravity);
        }
//        System.out.println("acc : " + accX + " vel : " + velocityX);

        checkUpDown();

    }

    private void checkUpDown() {

        if (velocityX > VELOCITY_SENSITIVITY) {
//            System.out.println("TEST 앉음");
            legUp = false;
            legDown = true;
        }

        if (velocityX < -VELOCITY_SENSITIVITY) {
//            System.out.println("TEST 일어남");
            legUp = true;
            legDown = false;
        }
    }


    public boolean isRightArmUpStretch() {
        return rightArmUpStretch;
    }

    public boolean isRightArmUpFold() {
        return rightArmUpFold;
    }

    public boolean isLeftArmUpStretch() {
        return leftArmUpStretch;
    }

    public boolean isLeftArmUpFold() {
        return leftArmUpFold;
    }

    public boolean isRightArmDownStretch() {
        return rightArmDownStretch;
    }

    public boolean isRightArmDownFold() {
        return rightArmDownFold;
    }

    public boolean isLeftArmDownStretch() {
        return leftArmDownStretch;
    }

    public boolean isLeftArmDownFold() {
        return leftArmDownFold;
    }

    public boolean isLegDown() {
        return legDown;
    }

    public boolean isLegUp() {
        return legUp;
    }

    public boolean isRotateRight() {
        return rotateRight;
    }

    public boolean isRotateLeft() {
        return rotateLeft;
    }

    public boolean isTiltFront() {
        return tiltFront;
    }

    public boolean isTiltBack() {
        return tiltBack;
    }

    public boolean isTiltRight() {
        return tiltRight;
    }

    public boolean isTiltLeft() {
        return tiltLeft;
    }

    public void initArmFlag() {
        rightArmUpFold = true;
        rightArmUpStretch = false;
        rightArmDownFold = true;
        rightArmDownStretch = false;

        leftArmUpFold = true;
        leftArmUpStretch = false;
        leftArmDownFold = true;
        leftArmDownStretch = false;
    }

    public void initLegFlag() {
        legDown = false;
        legUp = false;
        rotateLeft = false;
        rotateRight = false;
        tiltLeft = false;
        tiltRight = false;
        velocityX = 0;
    }
}
