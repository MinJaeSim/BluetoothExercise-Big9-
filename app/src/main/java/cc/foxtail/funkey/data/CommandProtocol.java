package cc.foxtail.funkey.data;

//public class CommandProtocol {
//    private byte[] byteArray;
//
//    public CommandProtocol(byte[] bytes) {
//        byteArray = bytes;
//    }
//
//    public byte[] getProtocol() {
//        return byteArray;
//    }
//
//    public String byteToHexCode() {
//        StringBuilder sb = new StringBuilder(byteArray.length * 2);
//
//
//        for (byte b : byteArray)
//            sb.append(String.format("%02x ", b));
//
//        return sb.toString();
//    }
//
//    public float getSensorData() {
//        return byteArray[1];
//    }
//
//    public String getProtocolHeader() {
//        return String.format("%02x", byteArray[0]);
//    }
//}
