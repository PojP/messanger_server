import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import DatabaseLogic.Database;
import cl.client.Package.PackageMethods;
import cl.client.Package.requests.*;
import cl.client.Package.requests.Message;
import conversation_with_client.RequestMethods;


public class ServerThread extends Thread {
    private PackageMethods PackageBuilder;
    private String UserID;
    private int LastMessage;
    private RequestMethods requestMethods;

    public ServerThread(Socket socket) throws IOException {
        this.PackageBuilder = new PackageMethods(socket);
        UserID = null;
        requestMethods = new RequestMethods();
        start();
    }

    @Override
    public void run() {
        while (true) {
            System.out.println();
            Requestable request = null;
            request = PackageBuilder.ReadDeserializedPackage();
            if(request==null)
            {
                break;
            }
            System.out.println(request.GetType());
            String token;
            switch (request.GetType()) {
                case AllMessages:
                        AllMessages mess= requestMethods.getMessages(UserID);
                        if(!mess.GetMessages().isEmpty())
                        {
                            PackageBuilder.SendSerializedPackage(mess);
                        }
                        else
                        {
                            PackageBuilder.SendSerializedPackage(new BAD_REQ("there is not new messages"));
                        }
                    break;
                case Message:
                    System.out.println(UserID);
                    if (UserID!=null)
                    {
                        Message message = (Message) request;
                        String to_usr = message.GetMessage().get(1);
                        if(requestMethods.CheckUser(to_usr)) {
                            try {
                                requestMethods.SendMessageToDB(UserID, message.GetMessage().get(3), to_usr);
                            } catch (Exception e) {
                                //PackageBuilder.SendSerializedPackage(new BAD_REQ("NO SUCH ACCOUNT"));
                            }
                        }
                        //PackageBuilder.SendSerializedPackage(new GOOD_REQ("good"));
                    }/*
                    else
                    {
                        PackageBuilder.SendSerializedPackage(new BAD_REQ("log in please"));
                    }*/
                    break;
                case Login:
                    Login login = (Login) request;
                    token = requestMethods.UserLogin(login.GetLogin(), login.GetPWD());
                    if (token != null) {
                        UserID=login.GetLogin();
                        PackageBuilder.SendSerializedPackage(new Token(token));
                    } else {
                        PackageBuilder.SendSerializedPackage(new BAD_REQ("BAD DATA"));
                    }
                    break;
                case Registrate:
                    Registrate reg = (Registrate) request;
                    token = requestMethods.UserReg(reg.GetLName(), reg.GetLogin(), reg.GetPWD());
                    if (token != null) {
                        UserID = reg.GetLogin();
                        PackageBuilder.SendSerializedPackage(new Token(token));
                    }
                    else
                        PackageBuilder.SendSerializedPackage(new BAD_REQ("SAME DATA IS ALREADY EXIST"));
                    break;
                case LogByToken:
                    if(UserID==null) {
                        LogByToken lbtoken = (LogByToken) request;
                        String recieved_token = lbtoken.GetToken();
                        if (recieved_token != null) {
                            UserID = requestMethods.GetLoginByToken(recieved_token);
                            if (UserID == null) {
                                PackageBuilder.SendSerializedPackage(new BAD_REQ("THERE IS NO A SUCH TOKEN"));
                            }
                            else PackageBuilder.SendSerializedPackage(new GOOD_REQ("ALL OK"));
                        } else {
                            PackageBuilder.SendSerializedPackage(new BAD_REQ("THERE IS NO A SUCH TOKEN"));
                        }
                    }
                    else {
                        PackageBuilder.SendSerializedPackage(new BAD_REQ("you already logged in"));
                    }
                    break;
                case Nickname:
                    Nickname nickname_login = (Nickname) request;
                    String nickname = requestMethods.GetNickname(nickname_login.getLogin());
                    PackageBuilder.SendSerializedPackage(new Nickname(nickname_login.getLogin(),nickname));
                    break;
                    case default:

            }
        }
    }
}
