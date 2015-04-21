/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.gruposistemasdistribuidos.practicasd2.auth;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.util.AuthStore;
import com.flickr4java.flickr.util.FileAuthStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

/**
 *
 * @author Margarita Martínez
 *
 */
public class CrearFichAutorizaciones {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        AuthStore authStore;

        try {
            String api_key;
            String secret;
            AuthInterface authInterface;
            Token requestToken;
            String tokenKey;
            try ( //*******************************************************
            //**** Obtención de las contraseñas de la aplicación ****
            //*******************************************************
                    Scanner scanner = new Scanner(System.in)) {
                System.out.print("Introduce el api_key: ");
                api_key = scanner.nextLine();
                System.out.print("Introduce el secret: ");
                secret = scanner.nextLine();
                Flickr flickr = new Flickr(api_key, secret, new REST());
                authInterface = flickr.getAuthInterface();
                //****************************************************************
                // **** Obtención de la autorizacion en la cuenta del usuario ****
                //****************************************************************
                // Obtiene el frob (requesToken)
                requestToken = authInterface.getRequestToken();
                System.out.println("RequestToken: " + requestToken);
                System.out.println();
                // Dirige al usuario a la página de Flickr donde confirma los permisos
                String url = authInterface.getAuthorizationUrl(requestToken, Permission.DELETE);
                System.out.println("Abre esta URL en un navegador para autorizar a la aplicación acceso a la cuenta Flickr");
                System.out.println(url);
                System.out.print("Introduce aquí el código que muestra la página: ");
                // Obtine el token_key
                tokenKey = scanner.nextLine();
            }
            System.out.println();

            // 
            Token accessToken = authInterface.getAccessToken(requestToken, new Verifier(tokenKey));
            Auth auth = authInterface.checkToken(accessToken);

            System.out.println("Autorización finalizada con éxito:");
            System.out.println("__________________________________");
            System.out.println("Token: " + accessToken.getToken());
            System.out.println("tokenSecret: " + accessToken.getSecret());
            System.out.println("UserID: " + auth.getUser().getId());
            System.out.println("Usuario: " + auth.getUser().getUsername());
            System.out.println("Nombre real: " + auth.getUser().getRealName());
            System.out.println("Nivel de permisos: " + auth.getPermission().getType());

            //********************************************
            //**** Almacenamiento de las contraseñas   ***
            //**** y datos de usuario en ficheros      ***
            //********************************************
            
            String RUTA = System.getProperty("user.home") + "\\" + "flickrAuth";
            String PROPSFICH = RUTA + "\\setup.properties";
            
            File authsDir = new File(RUTA);
            authsDir.mkdir();

            // Fichero con las contraseñas de la aplicación (api_key y secret) y el userID
            FileOutputStream osProperties = new FileOutputStream(PROPSFICH);

            Properties properties = new Properties();
            properties.setProperty("api_key", api_key);

            properties.setProperty("secret", secret);
            properties.setProperty("userID", auth.getUser().getId());
            properties.store(osProperties, "");

            // Fichero con los datos y autorizaciones de la cuenta del usuario
            authStore = new FileAuthStore(authsDir);
            authStore.store(auth);

        } catch (FlickrException | IOException e) {
        }
    }

}
