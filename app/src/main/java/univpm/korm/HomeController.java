package univpm.korm;


import java.util.List;

public class HomeController {

    private TabDatiAmbientali tabDatiAmbientali =new TabDatiAmbientali();

    private TabPunti tabPunti=new TabPunti();



    public void updatesaveBeacon(String address, String datetime,String temperature,String humidity){

         tabDatiAmbientali = tabDatiAmbientali.getTabBeacon(address);

         if(tabDatiAmbientali.address.equals("0")){
             tabDatiAmbientali =new TabDatiAmbientali(address,datetime,temperature,humidity);
             tabDatiAmbientali.save();
         }else
         {
             tabDatiAmbientali.dateTime=datetime;
             tabDatiAmbientali.temperature=temperature;
             tabDatiAmbientali.humidity=humidity;
             tabDatiAmbientali.save();
         }
          }

    public TabPunti TrovaCoordQuota(String address){
       return tabPunti.TrovaCoordQuotaModel(address);
    }

    public List<TabPunti> TrovaCoordQuotaPericolo(String[] address){
        return tabPunti.TrovaCoordPericoloQuotaModel(address);
    }
}
