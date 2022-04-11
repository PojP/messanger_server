package cl.client.Package;

import cl.client.Package.requests.Requestable;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import static java.lang.Thread.sleep;

public class PackageMethods {
    private ObjectOutputStream oostream;
    private InputStream inputStream;
    private ObjectInputStream oistream;
    private OutputStream outputStream;
    public PackageMethods(Socket socket) throws IOException {
        inputStream  =socket.getInputStream();
        outputStream =socket.getOutputStream();

        oostream = new ObjectOutputStream(outputStream);
        oistream = new ObjectInputStream(inputStream);
    }

    private PackageFootballBall CreatePackage(Requestable Req)
    {
        PackageFootballBall CrPackage = new PackageFootballBall(Req);
        return CrPackage;
    }
    public void SendSerializedPackage(Requestable Req)
    {
        System.out.println("Sending messages to the ServerSocket");
        try {
            oostream.writeObject(new PackageFootballBall(Req));
            oostream.flush();
            oostream.reset();
            System.out.println("PACKAGE SEND::   OK");
        } catch (IOException e) {
            System.out.println("PACKAGE SEND::   ERROR");
            e.printStackTrace();
        }
    }
    public Requestable ReadDeserializedPackage()
    {
        try {
            //System.out.println(oistream.read());
            PackageFootballBall pack = (PackageFootballBall) oistream.readObject();
            //oistream.reset();
            System.out.println(pack.GetReq());
            return pack.GetReq();
        }
        catch (SocketException e)
        {
            return null;
        }
        catch (EOFException e)
        {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
