/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.gruposistemasdistribuidos.practicasd2.src;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.licenses.LicensesInterface;
import com.flickr4java.flickr.photos.upload.Ticket;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import es.gruposistemasdistribuidos.practicasd2.auth.AutorizacionesFlickr;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author S.Valeror
 */
public class Sesion {

    private AutorizacionesFlickr autorizacion;
    private boolean permiso;
    private Flickr miFlickr;

    public Sesion() {

        autorizacion = new AutorizacionesFlickr();
        miFlickr = new Flickr(autorizacion.getApi_key(), autorizacion.getSecret(), new REST());
        Auth auth = autorizacion.getAuth();
        if (auth == null) {
            permiso = false;
        } else {
            Permission perm = auth.getPermission();
            if ((perm.getType() == Permission.WRITE_TYPE) || (perm.getType() == Permission.DELETE_TYPE)) {
                permiso = true;
            } else {
                permiso = false;
            }

        }
    }

    public void setPermiso(boolean permiso) {
        this.permiso = permiso;
    }

    public boolean isPermiso() {
        return permiso;
    }

    public void uploadFolder(MetaData metaData) {

        UploadMetaData metaDataFlickr = new UploadMetaData();

        //1 : Public 2 : Friends only 3 : Family only 4 : Friends and Family 5 : Private
        if (metaData.getPrivacidad() == 1) {
            metaDataFlickr.setPublicFlag(true);
        }
        if (metaData.getPrivacidad() == 2 || metaData.getPrivacidad() == 4) {
            metaDataFlickr.setFriendFlag(true);
        }
        if (metaData.getPrivacidad() == 3 || metaData.getPrivacidad() == 4) {
            metaDataFlickr.setFamilyFlag(true);
        }

        metaDataFlickr.setContentType(metaData.getTipoContenido());
        
        if (!metaData.getTitulo().equals("")) {
            metaDataFlickr.setTitle(metaData.getTitulo());
        }

        if (!metaData.getEtiquetas().isEmpty()) {
            metaDataFlickr.setTags(metaData.getEtiquetas());
        }

        if (!metaData.getDescripcion().equals("")) {
            metaDataFlickr.setDescription(metaData.getDescripcion());
        }

        metaDataFlickr.setAsync(true);

        if (metaData.getVisibilidad()== 0) {
            metaDataFlickr.setHidden(false);
        } else if (metaData.getVisibilidad() == 1) {
            metaDataFlickr.setHidden(true);
        }
        
        if (!metaData.getSeguridad().equals("")) {
            metaDataFlickr.setSafetyLevel(metaData.getSeguridad());
        }
        
        Uploader uploader = miFlickr.getUploader();
        LicensesInterface licenser = miFlickr.getLicensesInterface();
        PeopleInterface peopler = miFlickr.getPeopleInterface();
       
        List<Ticket> tickets = new ArrayList();
        for (File f:metaData.getRuta().listFiles()) {
            if (f.isFile()) {
                try {
                    String ticketName = uploader.upload(f,metaDataFlickr);
                    Ticket ticket = new Ticket();
                    ticket.setTicketId(ticketName);
                    tickets.add(ticket);
                } catch (FlickrException ex) {
                    Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (Ticket t:tickets) {
            while (!t.hasCompleted()) {
                
            }
            String fotoId = t.getPhotoId();
            try {
                licenser.setLicense(fotoId, metaData.getLicencia());
            } catch (FlickrException ex) {
                Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
            }
            String userID;
            for(String s:metaData.getPersonas()){
                userID = null;
                User usuario = peopler.findByEmail(s);
                if (usuario != null){
                    userID = usuario.getId();
                } else{
                    usuario = peopler.findByUsername(s);
                    if(usuario != null){
                        userID = usuario.getId();
                    }
                    
                }
                if (peopler.findByEmail(s) != null){
                    userID = peopler.findByEmail(s).getID;
                    
                }
                peopler.findByEmail(s);
                peopler.findByUsername(s)
                peopler.add(fotoId, autorizacion.getUserID(), null);
            }
            
        }
        
        
        if (!metaData.getPersonas().isEmpty()) {
            // ...
        }
    }

}
