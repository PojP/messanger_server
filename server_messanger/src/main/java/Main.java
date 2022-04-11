import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import DatabaseLogic.Database;
import Session.SessionToken;
import conversation_with_client.RequestMethods;

public class Main  {
    public static void main(String[] args) throws IOException {
        Database db = new Database("src/main/resources/", "server.db");
        db.OpenDB();
        ServerCore server = new ServerCore(9090, 100);
        server.GetConnections();
        //SimpleDateFormat parser = new SimpleDateFormat("YY:DD:HH:mm:ss");
        //System.out.println(Database.GetArrString_Bigger("messages","user_get_id","id","1"));
        //String time= new SimpleDateFormat("yyyy dd/MM HH:mm").format(new Date());
        //System.out.println(time);
        //System.out.println(SessionToken.GenerateToken("asdfhaskldhghs", "fasdflasjfj"));
        db.CloseDB();
    }
}
