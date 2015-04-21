package es.gruposistemasdistribuidos.practicasd2.auth;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.util.AuthStore;
import com.flickr4java.flickr.util.FileAuthStore;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.exit;
import java.util.Properties;

/**
 *
 * @author Margarita Martinez
 */
public class AutorizacionesFlickr {

    private String api_key;
    private String secret;
    private String userID;
    private Auth auth;

    public AutorizacionesFlickr() {
        String RUTA = System.getProperty("user.home") + "\\" + "flickrAuth";
        String PROPSFICH = RUTA + "\\setup.properties";

        InputStream is = null;
        AuthStore authStore = null;

        try {
            // Carpeta con los ficheros de autorizaciones y authstore
            File authsDir = new File(RUTA);

            if (authsDir.exists()) {
                authStore = new FileAuthStore(authsDir);
            } else {
                System.out.println("ERROR AutorizacionesFlickr: No existe la carpeta "
                        + RUTA);
                exit(1);
            }

            // Fichero con las contraseñas de la aplicación (api_key y secret) y userID
            File propsFich = new File(PROPSFICH);

            if (propsFich.exists()) {
                is = new FileInputStream(propsFich);
            } else {
                System.out.println("ERROR AutorizacionesFlickr: No existe el fichero "
                        + PROPSFICH);
                exit(1);
            }

            // Carga de las propiedades
            Properties properties = new Properties();
            properties.load(is);

            this.api_key = properties.getProperty("api_key");
            if (this.api_key.isEmpty()) {
                System.out.println("ERROR AutorizacionesFlickr: El fichero "
                        + PROPSFICH + " no contiene una propiedad api_key");
                exit(1);
            }

            this.secret = properties.getProperty("secret");
            if (this.secret.isEmpty()) {
                System.out.println("ERROR AutorizacionesFlickr: El fichero "
                        + PROPSFICH + " no contiene una propiedad secret");
                exit(1);
            }

            this.userID = properties.getProperty("userID");
            if (this.userID.isEmpty()) {
                System.out.println("ERROR AutorizacionesFlickr: El fichero "
                        + PROPSFICH + " no contiene una propiedad userID");
                exit(1);
            }

            // Carga de los datos y autorizaciones de la cuenta del usuario
            Auth a = authStore.retrieve(this.userID);
            if (a != null) {
                this.auth = a;
            } else {

                System.out.println("ERROR AutorizacionesFlickr: No existe el fichero "
                        + RUTA + this.userID + ".auth");
                exit(1);
            }

        } catch (FlickrException | IOException e) {
        }
    }

    public String getApi_key() {
        return api_key;
    }

    public String getSecret() {
        return secret;
    }

    public String getUserID() {
        return userID;
    }

    public Auth getAuth() {
        return auth;
    }

}
