package me.qiwu.colorqq.activity;

import java.util.ArrayList;
import java.util.List;

public class CreateDic {
    private int BitNum;
    private String Str;
    public void setBitNum(int num) {
        BitNum=num;
    }

    public void setStr(String str) {
        Str=str;
    }

    public int getBitNum(){
        return BitNum;
    }

    public String getStr(){
        return Str;
    }

    public List<String> getDic(){
        int[] tmparray=new int[BitNum];
        List<String> final_list=new ArrayList<String>();
        String result="";
        for(int i=0;i<BitNum;i++)
            tmparray[i]=0;
        while(true) {
            result="";
            for(int i=0;i<BitNum;i++) {
                result+=Str.charAt(tmparray[i]);
            }
            System.out.println(result);
            final_list.add(result+"\r\n");
            //开始进行下一轮循环
            int length=Str.length();
            int mark=0;
            for(int j=BitNum-1;j>=0;j--) {
                if(tmparray[j]==length-1){
                    if(j!=0){
                        continue;
                    }
                    else{
                        mark=1;
                        break;
                    }
                }
                else{
                    tmparray[j]++;
                    for(int k=j+1;k<BitNum;k++)
                    {
                        tmparray[k]=0;
                    }
                    break;
                }
            }
            if(mark==1){
                break;
            }
        }
        return final_list;
    }
}
