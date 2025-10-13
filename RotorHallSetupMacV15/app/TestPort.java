
import com.fazecast.jSerialComm.SerialPort;

public class TestPort {

    public static void main(String[] args) {
        // Port nomi (MacOS uchun "cu." varianti ishlatiladi)
        String portName = "/dev/cu.usbmodem48CA432E18EC2";

        SerialPort comPort = SerialPort.getCommPort(portName);
        comPort.setBaudRate(115200);

        if (comPort.openPort()) {
            System.out.println("Port ochildi: " + portName);
        } else {
            System.out.println("Portni ochib bo'lmadi!");
            return;
        }

        // O'qish oqimi
        try {
            while (true) {
                while (comPort.bytesAvailable() > 0) {
                    byte[] buffer = new byte[comPort.bytesAvailable()];
                    comPort.readBytes(buffer, buffer.length);
                    System.out.print(new String(buffer));
                }
                Thread.sleep(20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            comPort.closePort();
        }
    }
}
