package Session;
import DatabaseLogic.Database;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;


public class SessionToken {
    private final static String SESS_ID = "loginID";
    private final static String SESS_TOKEN = "SESSTOKEN";
    private final static String SESS_TABLE = "sessions";

    public static String GetLogin(String Token)
    {
        return Database.GetString(SESS_TABLE, SESS_ID, SESS_TOKEN, Token);
    }

    public static String GenerateToken(String password,String login)
    {
        Date date = new Date();
        String Token = "";
        Token+=date.toString();
        Token+="  ";
        String sha256hex_login_pwd = Hashing.sha256()
                .hashString(password+login, StandardCharsets.UTF_8)
                .toString();
        Token+=sha256hex_login_pwd;

        if(CheckLogin(login))   {UpdateToken(Token, login);}
        else                  {CreateTokenDB(Token, login);}

        return Token;
    }
    private static void UpdateToken(String Token, String login)
    {
        Database.UpdateDB(SESS_TABLE,SESS_TOKEN, Token, SESS_ID, login);
    }
    private static void CreateTokenDB(String Token, String login)
    {
        ArrayList<String> rows = new ArrayList<String>(2);
        rows.add(SESS_ID);
        rows.add(SESS_TOKEN);

        ArrayList<String> values = new ArrayList<String>(2);
        values.add(login);
        values.add(Token);
        Database.WriteToDB(SESS_TABLE, rows, values);
    }

    private static boolean CheckLogin(String Login)
    {
        return Database.CheckData(SESS_TABLE, SESS_ID, SESS_ID, Login);
    }
}
