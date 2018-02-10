package cc.foxtail.funkey.data;

public class ProtocolConstants {

    public static final byte[] CONNECTION_TEST = new byte[]{0x61, 0x6f, 0x10};
    public static final byte[] DISCONNECT = new byte[]{0x61, 0x66, 0x10};
    public static final byte[] START_ARM_EXERCISE = new byte[]{0x46, 0x31, 0x30, 0x30, 0x77};
    public static final byte[] START_LEG_EXERCISE = new byte[]{0x46, 0x40, 0x30, 0x30, 0x06};
    public static final byte[] START_ARM_LEG_EXERCISE = new byte[]{0x46, 0x54, 0x30, 0x30, 0x12};
    public static final byte[] STOP_EXERCISE = new byte[]{0x46, 0x5b, 0x30, 0x30, 0x1d};


    //오른팔 운동
    public static final byte[] ARM_RIGHT_UP_STRETCH = new byte[]{0x32, 0x30, 0x30, 0x74};
    public static final byte[] ARM_RIGHT_UP_FOLD = new byte[]{0x50, 0x30, 0x30, 0x16};
    public static final byte[] ARM_RIGHT_DOWN_STRETCH = new byte[]{0x34, 0x30, 0x30, 0x72};
    public static final byte[] ARM_RIGHT_DOWN_FOLD = new byte[]{0x52, 0x30, 0x30, 0x14};

    //왼팔운동
    public static final byte[] ARM_LEFT_UP_STRETCH = new byte[]{0x33, 0x30, 0x30, 0x75};
    public static final byte[] ARM_LEFT_UP_FOLD = new byte[]{0x51, 0x30, 0x30, 0x17};
    public static final byte[] ARM_LEFT_DOWN_STRETCH = new byte[]{0x35, 0x30, 0x30, 0x73};
    public static final byte[] ARM_LEFT_DOWN_FOLD = new byte[]{0x53, 0x30, 0x30, 0x15};

    public static final byte[] REMOVE_EXERCISE_COUNT = new byte[]{0x36, 0x30, 0x30, 0x70};

    //코인 관련
    public static final byte[] COIN_CHECK_PASSWORD_1 = new byte[]{0x37, 0x30, 0x31, 0x70};
    public static final byte[] COIN_CHECK_PASSWORD_2 = new byte[]{0x38, 0x30, 0x32, 0x7c};
    public static final byte[] COIN_CHECK_PASSWORD_3 = new byte[]{0x39, 0x30, 0x33, 0x7c};
    public static final byte[] COIN_CHECK_PASSWORD_4 = new byte[]{0x3A, 0x30, 0x34, 0x78};
    public static final byte[] SEPARATE_COIN = new byte[]{0x3B, 0x30, 0x30, 0x7d};

    //다리운동
    public static final byte[] SIT_DOWN = new byte[]{0x41, 0x30, 0x30, 0x07};
    public static final byte[] STAND_UP = new byte[]{0x42, 0x30, 0x30, 0x04};
    public static final byte[] TILT_LEFT = new byte[]{0x43, 0x30, 0x30, 0x05};
    public static final byte[] TILT_RIGHT = new byte[]{0x44, 0x30, 0x30, 0x02};

    //앱설정값에 의한


    //배터리 관련
    public static final byte[] REQUEST_BATTERY = new byte[]{0x46, 0x3F, 0x30, 0x30, 0x79};


}
