package message;

import com.google.gson.Gson;
import mpush.PushMessage;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    public static String managerMsg(String userId,String toUserId,JSONObject msg){
        MessageResponse response=new MessageResponse();
        response.setMsg(msg.toString());
        response.setError("");
        String str= new Gson().toJson(response);
        List toUserList=new ArrayList();
        toUserList.add(toUserId);
        PushMessage.sendMessageToPush(userId,toUserList,str);
        return "Success";
    }
}
