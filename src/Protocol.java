import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 프로토콜 구성 영역
 * magicNumber : 4byte
 * opcode : 1byte
 * length : 4byte
 * body : length 값만큼의 byte
 * 위 값에 따라 allocate 수행하며, body는 setBody 메소드 호출시 allocate
 * @author yhkim
 */
public class Protocol {
  private ByteBuffer header = ByteBuffer.allocate(9);
  private ByteBuffer body = null;

  /**
   * OPCODE에 대해서 각 연산이 정의 되어있음
   * MESSGE(0x00) : 메세지 송수신
   * REGISTER(0x01) : 클라이언트 접속
   * EXIT(0x02) : 클라이언트 접속 종료
   */
  public static final byte[] MESSAGE = new byte[] {0x00};
  public static final byte[] CONNECT = new byte[] {0x01};
  public static final byte[] EXIT = new byte[] {0x02};

  /**
   * header(magicNumber, opcode, length)를 default 값으로 초기화
   * body의 경우 length에 따라 크기가 다르기 때문에 초기화 x
   */
  Protocol() {
    header.put(new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03});
    header.put((byte) 0x00);
    header.put(new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
  }

  /**
   * set Opcode
   * @param opcode
   * offset을 지정하는 put 메소드가 byte[] 형만을 지원하여 byte[] 형태로 받음
   */
  public void setOpcode(byte[] opcode) {
    header.position(4);
    header.put(opcode, 0, 1);
  }

  /**
   * set Length
   * @param length
   * offset을 지정하는 put 메소드가 byte[] 형만을 지원하여 byte[] 형태로 받음
   */
  public void setLength(byte[] length) {
    header.position(5);
    header.put(length, 0, 4);
  }

  /**
   * set Body
   * @param body
   * header bytebuffer에서 length를 가져온 후, length 값에 맞게 body bytebuffer를 allocate
   */
  public void setBody(byte[] body) {
    byte[] len = new byte[4];
    header.position(5);
    header.get(len, 0, 4);
    ByteBuffer wrappedLength = ByteBuffer.wrap(len);
    this.body = ByteBuffer.allocate(wrappedLength.getInt());
    this.body.put(body);
  }

  /**
   * header와 body로 구성된 Bytebuffer Array를 리턴하는 메소드
   * SocketChannel의 write 메소드는 ByteBuffer Array 또한 write를 지원하기 때문에,
   * ByteBuffer를 concat하지 않아도 됨
   * @return ByteBuffer[] {header, body}
   */
  public ByteBuffer[] getCompleteMessage() {
    header.rewind();
    body.rewind();
    return new ByteBuffer[] {header, body};
  }
}