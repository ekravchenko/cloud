package com.uawebchallenge.cloud.node;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.NodeException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.Assert.*;

public class P2PNodeTest {

    private final static Logger logger = LoggerFactory.getLogger(P2PNodeTest.class);

    @Test
    public void testCreatePeer() throws NodeException {
        Node node = new P2PNode(Optional.of(4003));
        assertNotNull(node);

        node.shutdown();
    }

    @Test
    public void testConnectViaIp() throws InterruptedException, DataException, NodeException {
        final String key = "key";
        final String value = "Hello world";

        NodeInThread nodeInThread = new NodeInThread(4004, key, value);
        nodeInThread.start();
        Thread.sleep(1000);

        Node node = new P2PNode(Optional.of(4010));
        node.connectViaIp("localhost", 4004);

        Optional<Object> valueOptional = node.get(key);
        assertNotNull(valueOptional);
        assertTrue(valueOptional.isPresent());
        assertEquals(value, valueOptional.get());

        node.shutdown();
        nodeInThread.shutdown();
        Thread.sleep(1000);
    }

    @Ignore
    @Test
    public void testConnectViaBroadcast() throws InterruptedException, DataException, NodeException {
        final String key = "key";
        final String value = "Hello world";

        NodeInThread nodeInThread = new NodeInThread(4004, key, value);
        nodeInThread.start();
        Thread.sleep(1000);

        Node node = new P2PNode(Optional.of(4010));
        node.connectViaBroadcast(4004);

        Optional<Object> valueOptional = node.get(key);
        assertNotNull(valueOptional);
        assertTrue(valueOptional.isPresent());
        assertEquals(value, valueOptional.get());

        node.shutdown();
        nodeInThread.shutdown();
        Thread.sleep(1000);
    }

    private class NodeInThread extends Thread {

        private final int port;
        private final String key;
        private final String value;
        private Node node;


        private NodeInThread(int port, String key, String value) {
            this.port = port;
            this.key = key;
            this.value = value;
        }

        @Override
        public void run() {
            try {
                node = new P2PNode(Optional.of(port));
                node.put(key, value);
            } catch (DataException | NodeException e) {
                logger.error(e.getMessage(), e);
            }
        }

        public void shutdown() {
            if (node != null) {
                node.shutdown();
            }
        }
    }
}