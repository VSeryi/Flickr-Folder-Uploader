/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.gruposistemasdistribuidos.practicasd2.src;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import es.gruposistemasdistribuidos.practicasd2.auth.AutorizacionesFlickr;

/**
 *
 * @author S.Valeror
 */
public class Sesion {
    private AutorizacionesFlickr autorizacion;
    private boolean permiso;
    private Flickr miFlickr;

    public Sesion() {
        // CrearFichAutorizaciones.main();
        autorizacion = new AutorizacionesFlickr();
        miFlickr = new Flickr(autorizacion.getApi_key(), autorizacion.getSecret(), new REST());
        Auth auth = autorizacion.getAuth();
        if (auth == null) {
            permiso = false;
        }
        Permission perm = auth.getPermission();
        if ((perm.getType() == Permission.WRITE_TYPE) || (perm.getType() == Permission.DELETE_TYPE))
            permiso = true;
        else {
            permiso = false;
        }
        
        permiso = false;
        
        
    }

    public void setPermiso(boolean permiso) {
        this.permiso = permiso;
    }

    public boolean isPermiso() {
        return permiso;
    }
    
    
    
}
