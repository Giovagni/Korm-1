package univpm.korm;


public class LoginController {


    private TabPunti tabPunti =new TabPunti();


    public int SalvaPuntiController(String codice, String x, String y, String quota,String address,String data){
        return tabPunti.SalvaPunti(codice,x,y,quota,address,data);
    }

    public void AggiornadatiController(String codice, String x, String y, String quota,String address,String data){
        tabPunti.Aggiornadati(codice,x,y,quota,address,data);
    }

    public void AggiornaoldDatabaseController(String codice, String x, String y, String quota,String address,String data){
        tabPunti.AggiornaoldDatabase(codice,x,y,quota,address,data);
    }

    public int contaPuntiController() {
        return tabPunti.contaPunti();
    }

}

