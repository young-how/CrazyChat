package org.DUT.command_script;

import org.DUT.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class cutPoint {
//    public String processCutPoint(String order){
//        String regex="/cutpoint_id:(.*)&&";  //提取id号码
//        Pattern p=Pattern.compile(regex);
//        Matcher m=p.matcher(order);  //匹配
//        if(m.find()){
//            String content=messageWin.getText();  //保存消息
//            String content_id= m.group(1);//截取到内容的id
//            String regex_content="(.*)/cutpoint_id:";  //提取id号码
//            if(!insert_index.containsKey(content_id)){
//                //没有匹配到插入点
//                //StyledDocument doc = messageWin.getStyledDocument();
//                int insert_ind=doc.getLength(); //最末端的标记
//                insert_index.put(content_id,insert_ind);  //保存插入点的标记
//            }
//            Pattern p2=Pattern.compile(regex_content);
//            Matcher m2=p2.matcher(order);  //匹配
//            if(m2.find()){
//                //匹配到内容
//                String info_content=m2.group(1);
//                int idx=insert_index.get(content_id);  //获取插入点
//                insertMessage(info_content,idx,content_id,messageWin.getStyle(Constants.SYS_STYLE));  //在对应的插入点插入消息
//            }
//            appendMessage(content);   //保存消息
//        }
//        order="";//丢弃了原来的消息
//        return order;
//    }
}
