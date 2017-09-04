package univpm.korm;

import android.util.Log;

import com.orm.SugarRecord;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TabPunti extends SugarRecord{

    public String codice;
    public String x;
    public String y;
    public String quota;
    public String address;
    public String data;

    public TabPunti(){}

    public TabPunti(String codice, String x, String y, String quota,String address,String data){

        this.codice = codice;
        this.x=x;
        this.y=y;
        this.quota=quota;
        this.address = address;
        this.data = data;

    }

    public int SalvaPunti(String codice, String x, String y, String quota,String address,String data){

        if(x.isEmpty() || y.isEmpty()){
            return 0;
        }else if (quota.isEmpty()){
            return 1;
        }else{
            TabPunti Punti=new TabPunti(codice,x,y,quota,address,data);
            Punti.save();
            return 2;
        }

    }


    public void Aggiornadati(String codice, String x, String y, String quota,String address,String data)
    {
        List<TabPunti> tabPunti = TabPunti.find(TabPunti.class,"codice=?",codice);
        TabPunti punti=tabPunti.get(0);
        punti.codice=codice;
        punti.address=address;
        punti.x=x;
        punti.y=y;
        punti.quota=quota;
        punti.data=data;
        punti.save();
    }


    public void AggiornaoldDatabase(String codice, String x, String y, String quota,String address,String data){

        List<TabPunti> tabPunti = TabPunti.find(TabPunti.class,"codice=?",codice);
        TabPunti punti=tabPunti.get(0);
        DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.ITALIAN);
        Date dataold = null;
        Date datanew= null;

        try {
            dataold = format.parse(punti.data);
            datanew= format.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(datanew.after(dataold))
        {
            punti.codice=codice;
            punti.address=address;
            punti.x=x;
            punti.y=y;
            punti.quota=quota;
            punti.data=data;
            punti.save();
        }

    }

    public TabPunti TrovaCoordQuotaModel(String address)
    {
        List<TabPunti> tabPunti = TabPunti.find(TabPunti.class,"address=?",address);
        return tabPunti.get(0);
    }

    public List<TabPunti> TrovaCoordPericoloQuotaModel(String[] address)
    {
        List<TabPunti> tabPunti = TabPunti.find(TabPunti.class,"address=?",address[0]);

        for (int i=1;i<address.length;i++)
        {
            tabPunti.add(i,TabPunti.find(TabPunti.class,"address=?",address[i]).get(0));

            Log.e("tabpunti",address[i]);
        }
        return tabPunti;
    }

    public int contaPunti(){
        return (int) TabPunti.count(TabPunti.class);
    }


}
