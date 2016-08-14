package NioServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioChatServer implements Runnable {
    private final int port;
    private ServerSocketChannel ssc;
    private Selector selector;
    private ByteBuffer buf = ByteBuffer.allocate(256);
    private int numOnline;
    private SelectionKey acceptKey;

    public NioChatServer(int port) {
        this.port = port;
    }

    public int getOnlineNum() {
        return numOnline;
    }

    @Override
    public void run() {
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(port));
            ssc.configureBlocking(false);
            selector = Selector.open();
            acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server starting on port " + port);

            Iterator<SelectionKey> iter;
            SelectionKey key;
            while (ssc.isOpen()) {
                selector.select();
                iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    key = iter.next();
                    iter.remove();

                    if (key.isAcceptable()) handleAccept(key);
                    if (key.isReadable()) handleRead(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccept(SelectionKey key) {
        try {
            SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
            int address = sc.socket().getPort();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ, address);
            numOnline++;
            System.out.println(address + " is online;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRead(SelectionKey key) {
        SocketChannel ch = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();

        buf.clear();
        int read = 0;
        try {
            while ((read = ch.read(buf)) > 0) {
                buf.flip();
                byte[] bytes = new byte[buf.limit()];
                buf.get(bytes);
                sb.append(new String(bytes));
                buf.clear();
            }
            String msg = new String(sb);
            //System.out.println(msg);
            broadcast(msg);
        } catch (Exception e) {
            try {
                key.cancel();
                ch.close();
                ch.socket().close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            numOnline--;
            System.out.println(key.attachment() + " left the chat.\n");
        }
    }

    private void broadcast(String msg) {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key != acceptKey) {
                try {
                    SocketChannel sch = (SocketChannel) key.channel();
                    sch.write(msgBuf);
                    msgBuf.rewind();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}
