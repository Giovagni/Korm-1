package univpm.korm.Connessione;


import android.content.Context;
import android.content.SharedPreferences;

public class Sessione {
    SharedPreferences log;
    SharedPreferences.Editor editor;
    Context ctx;

    public Sessione(Context ctx){
        this.ctx = ctx;
        log = ctx.getSharedPreferences("ProveLogin", Context.MODE_PRIVATE);
        editor = log.edit();
    }

    public void UtenteLoggato(boolean loggedin,String user){
        editor.putBoolean("loggedInmode",loggedin);
        editor.putString("user",user);
        editor.commit();
    }

    public void DatiServer(String ip,String porta)
    {
        editor.putString("ip",ip);
        editor.putString("porta",porta);
        editor.commit();
    }

    public void DatiSoglia(int soglia)
    {
        editor.putInt("soglia",soglia);
        editor.commit();
    }

    public void eliminaUser()
    {
        editor.remove("user");
        editor.putString("user","logout");
        editor.commit();
    }

    public boolean loggedin(){
        return log.getBoolean("loggedInmode", false);
    }

    public String ip(){
        return log.getString("ip","null");
    }

    public String porta(){
        return log.getString("porta","null");
    }

    public int soglia(){return log.getInt("soglia", 0);}

    public String user(){
        return log.getString("user","null");
    }

    public void UtenteGuest(boolean loggedin,String user){
        editor.putBoolean("loggedInmode",loggedin);
        editor.putString("user",user);
        editor.commit();
    }


}
