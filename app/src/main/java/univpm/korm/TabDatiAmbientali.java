package univpm.korm;

import com.orm.SugarRecord;
import java.util.List;

public class TabDatiAmbientali extends SugarRecord{

    public String address;
    public String dateTime;
    public String temperature;
    public String humidity;

    public TabDatiAmbientali(){}

    public TabDatiAmbientali(String address, String dateTime, String temperature, String humidity){
        this.address = address;
        this.dateTime = dateTime;
        this.temperature=temperature;
        this.humidity=humidity;

    }


    public TabDatiAmbientali getDati(String address){

        List<TabDatiAmbientali> list = TabDatiAmbientali.find(TabDatiAmbientali.class,"address=?",address);

        if (list.size()>0)
            return list.get(0);
        else
            return new TabDatiAmbientali("0","0","0","0");

    }

    public TabDatiAmbientali getTabBeacon(String address){
        TabDatiAmbientali tabDatiAmbientali =new TabDatiAmbientali();
        return tabDatiAmbientali.getDati(address);
    }

}
