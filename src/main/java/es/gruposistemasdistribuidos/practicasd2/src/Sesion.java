/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.gruposistemasdistribuidos.practicasd2.src;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.licenses.LicensesInterface;
import com.flickr4java.flickr.photos.upload.Ticket;
import com.flickr4java.flickr.photos.upload.UploadInterface;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import es.gruposistemasdistribuidos.practicasd2.auth.AutorizacionesFlickr;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private List<String> fotosSubidas;

    public Flickr getMiFlickr() {
        return miFlickr;
    }

    public Sesion() {

        autorizacion = new AutorizacionesFlickr();
        fotosSubidas = new ArrayList();
        miFlickr = new Flickr(autorizacion.getApi_key(), autorizacion.getSecret(), new REST());
        miFlickr.setAuth(autorizacion.getAuth());
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

    public void uploadFolder(MetaData metaData) throws FlickrException {

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

        if (metaData.getTitulo() != null) {
            metaDataFlickr.setTitle(metaData.getTitulo());
        }

        if (!metaData.getEtiquetas().isEmpty()) {
            metaDataFlickr.setTags(metaData.getEtiquetas());
        }

        if (metaData.getDescripcion() != null) {
            metaDataFlickr.setDescription(metaData.getDescripcion());
        }

        metaDataFlickr.setAsync(true);

        if (metaData.getVisibilidad() == 0) {
            metaDataFlickr.setHidden(false);
        } else if (metaData.getVisibilidad() == 1) {
            metaDataFlickr.setHidden(true);
        }

        if (!metaData.getSeguridad().equals("")) {
            metaDataFlickr.setSafetyLevel(metaData.getSeguridad());
        }
        RequestContext.getRequestContext().setAuth(miFlickr.getAuth());
        Uploader uploader = miFlickr.getUploader();
        LicensesInterface licenser = miFlickr.getLicensesInterface();
        PeopleInterface peopler = miFlickr.getPeopleInterface();

        Set<String> tickets = new HashSet<String>();
        for (File f : metaData.getCarpeta().listFiles()) {
            if (f.isFile()) {
                try {
                    String ticketName = uploader.upload(f, metaDataFlickr);
                    tickets.add(ticketName);
                } catch (FlickrException ex) {
                    Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        UploadInterface inter = miFlickr.getUploadInterface();
        boolean completada = false;
        while (!completada) {
            boolean estaCompletada = true;
            for (Ticket t : inter.checkTickets(tickets)) {
                estaCompletada = estaCompletada && (t.getStatus() > 0);
            }
            completada = estaCompletada;
        }
        for (Ticket t : inter.checkTickets(tickets)) {
            String fotoId = t.getPhotoId();
            fotosSubidas.add(fotoId);
            try {
                if (metaData.getLicencia() != -1) {
                    licenser.setLicense(fotoId, metaData.getLicencia());
                }
            } catch (FlickrException ex) {
                Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
            }
            String userID;
            for (String s : metaData.getPersonas()) {
                try {
                    userID = null;
                    User usuario = peopler.findByEmail(s);
                    if (usuario != null) {
                        userID = usuario.getId();
                    } else {
                        usuario = peopler.findByUsername(s);
                        if (usuario != null) {
                            userID = usuario.getId();
                        }

                    }
                    peopler.add(fotoId, autorizacion.getUserID(), null);
                } catch (FlickrException ex) {
                    Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        if (!metaData.getPersonas().isEmpty()) {
            // ...
        }
    }

}
