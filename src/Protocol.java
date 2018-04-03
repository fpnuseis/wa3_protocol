import java.nio.ByteBuffer;

/**
 * 프로토콜 구성 영역
 * magicNumber : 4byte
 * opcode : 1byte
 * length : 4byte
 * body : length 값만큼의 byte
 * 위 값에 따라 allocate 수행하며, body는 setBody 메소드 호출시 allocate
 *
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
   * header(magicNumber, opcode, length) 초기화
   * body의 경우 length에 따라 크기가 다르기 때문에 초기화 x
   */
  Protocol() {
    header.put(new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03});
    header.put((byte) 0x00);
    header.put(new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
  }

  /**
   * set Opcode
   */
  public void setOpcode(byte[] opcode) {
    header.put(opcode, 4, 1);
  }

  /**
   * set Length
   */
  public void setLength(byte[] length) {
    header.put(length, 5, 4);
  }

  /**
   * set body
   * length의 값에 맞춰 allocate를 수행한 후, body를 set
   */
  public void setBody(byte[] body) {
    byte[] len = new byte[4];
    header.get(len, 5, 4);
    ByteBuffer wrappedLength = ByteBuffer.wrap(len);
    this.body = ByteBuffer.allocate(wrappedLength.getInt());
    this.body.put(body);
  }

  /**
   * CompleteMessage
   */
  public ByteBuffer[] getCompleteMessage() {
    header.flip();
    body.flip();
    return new ByteBuffer[] {header, body};
  }
}