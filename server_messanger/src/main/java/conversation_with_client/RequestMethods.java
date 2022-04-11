package conversation_with_client;
import DatabaseLogic.Database;
import Session.SessionToken;
import cl.client.Package.requests.AllMessages;
import cl.client.Package.requests.Message;

import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class RequestMethods {
    private int last_msg_id;
    public RequestMethods()
    {
        last_msg_id=-1;
    }
    public boolean CheckDataForUser(String UserName, String UserLogin, String HashedPassword)
    {
        System.out.println(UserName.length());
        System.out.println(UserLogin.length());
        System.out.println(HashedPassword.length());
        System.out.println();
        System.out.println(UserName);
        System.out.println(UserLogin);
        System.out.println(HashedPassword);

        boolean LoginSize= UserLogin.length()<15 & UserLogin.length()>7;
        boolean UserNameSize=  UserName.length()<20;
        //boolean HashPWD_Size=HashedPassword.length()<20 & HashedPassword.length()>12;

        System.out.println(LoginSize);
        System.out.println(UserNameSize);
        //System.out.println(HashPWD_Size);

        if (LoginSize& UserNameSize)
        {
            System.out.println("CHECKDATA: DATA IS GOOD");
            return Database.CheckData("users","login", "login", UserLogin);
        }
        else
        {
            System.out.println("CHECKDATA: DATA IS BAD");
            return true;
        }
    }
    public String GetNickname(String login)
    {
        return Database.GetString("users","username","login", login);
    }
    public boolean CheckDataForUser(String UserLogin, String HashedPassword)
    {
        boolean LoginSize= UserLogin.length()<15 & UserLogin.length()>7;
        boolean HashPWD_Size=HashedPassword.length()==20;
        if (LoginSize& HashPWD_Size& Database.CheckData("users","login", "login", UserLogin ) )
        {
            System.out.println("CHECKDATA: DATA IS EXIST");
            return true;
        }
        else
        {
            System.out.println("CHECKDATA: DATA DOESNT EXIST");
            return false;
        }
    }

    public String UserReg(String UserName, String UserLogin, String HashedPassword)
    {
        if(!CheckDataForUser(UserName, UserLogin, HashedPassword)) {
            ArrayList<String> rows = new ArrayList<String>();
            rows.add("login");
            rows.add("username");
            rows.add("pwd_hash");

            ArrayList<String> values = new ArrayList<String>();
            values.add(UserLogin);
            values.add(UserName);
            values.add(HashedPassword);

            SessionToken.GenerateToken(HashedPassword, UserLogin);

            Database.WriteToDB("users", rows, values);
            return SessionToken.GenerateToken(HashedPassword, UserLogin);
        }
        else
        {
            System.out.println("Same data is already exist");
            return null;
        }
    }
    public boolean IsUserFriend(String user1, String user2)
    {
        if(user1==null | user2==null){return false;}
        ArrayList<String> chk1 =Database.GetArrString("friend_users","UserID", "UserFriendID", user1);
        ArrayList<String> chk2= Database.GetArrString("friend_users","UserID", "UserFriendID", user2);

        System.out.println(chk1);
        System.out.println(chk2);

        for(String user : chk1)
        {
            return user.contentEquals(user2);
        }
        for(String user :chk2)
        {
            return user.contentEquals(user1);
        }
        return false;
    }
    public String GetLoginByToken(String token)
    {
        if (Database.CheckData("sessions","SESSTOKEN", "SESSTOKEN", token))
        {
            return Database.GetString("sessions","loginID","SESSTOKEN",token);
        }
        return null;
    }
    public String UserLogin(String UserLogin, String HashedPassword)
    {
        if(CheckDataForUser(UserLogin,HashedPassword))
        {
            return SessionToken.GenerateToken(HashedPassword, UserLogin);
        }
        else
        {
            return null;
        }
    }
    public AllMessages getMessages(String login) {
        ArrayList<String> users = Database.GetArrString_Bigger("messages", "user_id", "id", String.valueOf(last_msg_id+1));
        ArrayList<String> messages = Database.GetArrString_Bigger("messages", "data", "id", String.valueOf(last_msg_id+1));
        ArrayList<String> date = Database.GetArrString_Bigger("messages", "date", "id", String.valueOf(last_msg_id+1));
        ArrayList<String> to_users = Database.GetArrString_Bigger("messages", "user_get_id", "id", String.valueOf(last_msg_id+1));

        System.out.println(to_users.size());
        System.out.println(last_msg_id);
        if (to_users.size()>0) {
            last_msg_id+=to_users.size();

            ArrayList<Integer> id_user_messages = new ArrayList<>();

            int i = 0;
            for (String user : users) {
                if (login.equals(user)) {
                    id_user_messages.add(i);
                }
                i++;
            }
            ArrayList<Integer> id_other_messages = new ArrayList<>();
            int j = 0;
            for (String user2 : to_users) {
                if (login.equals(user2)) {
                    id_other_messages.add(j);
                }
                j++;
            }

            ArrayList<Message> messageArrayList = new ArrayList<>();
            ArrayList<Integer> all_message_elem = new ArrayList<>();
            all_message_elem.addAll(id_other_messages);
            all_message_elem.addAll(id_user_messages);
            Collections.sort(all_message_elem);

            System.out.println("USER MESSAGES: " + id_user_messages);
            System.out.println("OTHER MESSAGES:" + id_other_messages);
            System.out.println(all_message_elem);
            for (int k : all_message_elem) {
                if (id_other_messages.contains(k)) {
                    System.out.println("K IN OTHER MESSAGES");
                    ArrayList<String> msg = new ArrayList<>();
                    msg.add(users.get(k));
                    msg.add(login);
                    msg.add(date.get(k));
                    msg.add(messages.get(k));
                    System.out.println("MESSAGE::" + msg);
                    messageArrayList.add(new Message(msg));
                } else if (id_user_messages.contains(k)) {
                    System.out.println("K IN USER MESSAGES");
                    ArrayList<String> msg = new ArrayList<>();
                    msg.add(login);
                    msg.add(to_users.get(k));
                    msg.add(date.get(k));
                    msg.add(messages.get(k));
                    System.out.println("MESSAGE::" + msg);
                    messageArrayList.add(new Message(msg));
                }
            }
            System.out.println(messageArrayList);

            return new AllMessages(messageArrayList);

        }
        else
        {

            return new AllMessages(new ArrayList<Message>());
        }
    }
    public void setLast_message_id(int id)
    {
        last_msg_id=id;
    }
    public void SendMessageToDB(String login, String message, String to_user)
    {
        ArrayList<String> rows = new ArrayList<String>();
        ArrayList<String> data = new ArrayList<String>();

        rows.add("user_id");
        rows.add("data");
        rows.add("date");
        rows.add("user_get_id");

        data.add(login);
        data.add(message);
        data.add(new SimpleDateFormat("yyyy dd/MM HH:mm").format(new Date()));
        data.add(to_user);
        Database.WriteToDB("messages", rows,data);
    }

    public boolean CheckUser(String to_usr) {
        if(Database.CheckData("users", "login","login",to_usr))
        {
            return true;
        }
        return false;
    }
}
