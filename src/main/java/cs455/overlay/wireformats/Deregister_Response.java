package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPSender;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class Deregister_Response implements Event {

    //TODO DISABLE DEBUG TOGGLE
    private byte[] marshaledBytes;

    //RECEIVES RESPONSE
    Deregister_Response(byte[] marshaledBytes, MessagingNode node) throws IOException {
        this.marshaledBytes = marshaledBytes;

        //Incoming message data
        byte STATUS;

        ByteArrayInputStream baInputStream =
            new ByteArrayInputStream(marshaledBytes);
        DataInputStream din =
            new DataInputStream(new BufferedInputStream(baInputStream));

        //Throws away message type data;
        din.readByte();

        //Stores the Request Response's status code
        STATUS = din.readByte();

        //Stores the Request Response's additional info
        int statusCodeLength = din.readInt();
        byte[] identifierBytes = new byte[statusCodeLength];
        din.readFully(identifierBytes);

        //Final clean up)
        baInputStream.close();
        din.close();


        if (STATUS == 1) {
            System.out.println("DEREGISTERED AND EXITING PROGRAM");
            System.exit(1);
        }else {
            System.out.println("DEREGISTERATION FAILURE");
            System.out.println("THIS SHOULD NOT HAVE HAPPENED");
        }
    }

    //SENDS RESPONSE
    Deregister_Response(String NODE_ADDRESS, int NODE_PORT, byte STATUS, String INFO)  throws IOException {

        //creates Request message byte array
        byte[] marshaledBytes;

        //creates socket to server
        Socket REG_SOCKET = new Socket(NODE_ADDRESS, NODE_PORT);

        //Initialize used streams
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout =
            new DataOutputStream(new BufferedOutputStream(baOutputStream));

        //insert the deregister request protocol
        dout.writeByte(4);
        dout.writeByte(STATUS);

        //insert the Address then the port of the node
        byte[] ADDITIONAL_INFO = (INFO).getBytes();
        int elementLength = ADDITIONAL_INFO.length;
        dout.writeInt(elementLength);
        dout.write(ADDITIONAL_INFO);

        //records payload and cleans up
        dout.flush();
        marshaledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        //sends request
        TCPSender sender = new TCPSender(REG_SOCKET);
        sender.sendData(marshaledBytes);
    }

    public int getType(){ return 1; }
    public byte[] getBytes(){ return this.marshaledBytes; }
}
