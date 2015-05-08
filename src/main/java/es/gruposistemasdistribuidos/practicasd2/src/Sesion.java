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
import com.flickr4java.flickr.groups.pools.PoolsInterface;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.licenses.LicensesInterface;
import com.flickr4java.flickr.photos.upload.Ticket;
import com.flickr4java.flickr.photos.upload.UploadInterface;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import es.gruposistemasdistribuidos.practicasd2.auth.AutorizacionesFlickr;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author S.Valeror
 */
public class Sesion {

    private AutorizacionesFlickr autorizacion;
    private boolean permiso;
    private Flickr miFlickr;
    private List<String> fotosSubidas;

    public static class UploadTask extends SwingWorker<Void, Void> {

        private int completados = 0;
        private Sesion sesion;
        private MetaData metaData;
        private int error = 0;

        public UploadTask(Sesion sesion, MetaData metaData) {
            this.sesion = sesion;
            this.metaData = metaData;
        }

        public void setCompletados(int completados) {
            int oldCompletados = this.completados;
            this.completados = completados;
            this.getPropertyChangeSupport().firePropertyChange("completados",
                    oldCompletados, completados);
        }

        public int getError() {
            return error;
        }

        public void setError(String error) {
            int oldError = this.error;
            this.getPropertyChangeSupport().firePropertyChange("error",
                    oldError, error);
        }

        public int
                getCompletados() {
            return completados;
        }

        @Override
        public Void doInBackground() throws FlickrException, PropertyVetoException {
            Flickr miFlickr = sesion.getMiFlickr();

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
            String miError = "No se han podido subir los siguientes archivos: \n";
            String errorOriginal = miError;
            RequestContext.getRequestContext().setAuth(miFlickr.getAuth());
            Uploader uploader = miFlickr.getUploader();
            LicensesInterface licenser = miFlickr.getLicensesInterface();
            PeopleInterface peopler = miFlickr.getPeopleInterface();
            Set<String> ticketsNames = new HashSet<>();
            List<File> files = new ArrayList();
            for (File f : metaData.getCarpeta().listFiles()) {
                String name = f.getName();
                if (isValidSuffix(name)) {
                    String ticketName = uploader.upload(f, metaDataFlickr);
                    ticketsNames.add(ticketName);
                    files.add(f);
                    setCompletados(completados + 1);
                } else {
                    miError = miError + "  - " + f.getName() + ": Formato incompatible.\n";
                }
            }
            UploadInterface inter = miFlickr.getUploadInterface();
            List<Ticket> tickets;
            int numTickets = ticketsNames.size();
            while (!(completados >= (numTickets * 2))) {
                tickets = inter.checkTickets(ticketsNames);
                for (Ticket t : tickets) {
                    if (t.getStatus() > 0) {
                        ticketsNames.remove(t.getTicketId());
                        setCompletados(completados + 1);
                        if (t.getStatus() == 2) {
                            miError = miError + "  - " + files.get(tickets.indexOf(t)).getName() + ": Error en la subida.\n";
                        } else {
                            sesion.getFotosSubidas().add(t.getPhotoId());
                        }
                    }
                }
            }
            for (String fotoId : sesion.getFotosSubidas()) {
                try {
                    if (metaData.getLicencia() != -1) {
                        licenser.setLicense(fotoId, metaData.getLicencia());
                    }
                } catch (FlickrException ex) {
                    Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
                }
                String userID = null;
                miError = miError + "No se han podido agregar a las siguientes personas:\n";
                for (String s : metaData.getPersonas()) {
                    User usuario = null;
                    boolean encontrado = false;
                    try {
                        usuario = peopler.findByEmail(s);
                        userID = usuario.getId();
                        encontrado = true;
                        peopler.add(fotoId, sesion.getAutorizacion().getUserID(), null);
                    } catch (FlickrException ex) {

                    }
                    if (!encontrado) {
                        try {
                            usuario = peopler.findByUsername(s);
                            userID = usuario.getId();
                            encontrado = true;
                            peopler.add(fotoId, sesion.getAutorizacion().getUserID(), null);
                        } catch (FlickrException ex2) {
                           
                        }
                    }
                    if(!encontrado){
                        miError = miError + "  -: " + s + ": No Existe.\n";
                    }
                     
                }
            }
            if (!miError.equals(errorOriginal)) {
                setError(miError);
            }
            setCompletados(completados + 1);
            return null;
        }

        @Override
        public void done() {

            // llamar a gui pa decr q hemos terminado
        }
    }

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

    public AutorizacionesFlickr getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(AutorizacionesFlickr autorizacion) {
        this.autorizacion = autorizacion;
    }

    public List<String> getFotosSubidas() {
        return fotosSubidas;
    }

    public void setFotosSubidas(List<String> fotosSubidas) {
        this.fotosSubidas = fotosSubidas;
    }

    public void setPermiso(boolean permiso) {
        this.permiso = permiso;
    }

    public boolean isPermiso() {
        return permiso;
    }

    public void createAlbum(String title, String decription) throws FlickrException {
        RequestContext.getRequestContext().setAuth(miFlickr.getAuth());
        PhotosetsInterface photoSeters = miFlickr.getPhotosetsInterface();
        Photoset photoSet = photoSeters.create(title, decription, fotosSubidas.get(0));

        if (fotosSubidas.size() > 1) {
            for (int i = 1; i < fotosSubidas.size(); i++) {
                photoSeters.addPhoto(photoSet.getId(), fotosSubidas.get(i));
            }
        }
    }

    public void addToPool(String groupId) throws FlickrException {
        PoolsInterface poolers = miFlickr.getPoolsInterface();
        for (String s : fotosSubidas) {
            poolers.add(s, groupId);
        }
    }

    public static boolean isValidSuffix(String basefilename) {
        String[] photoSuffixes = {"jpg", "jpeg", "png", "gif", "bmp", "tif", "tiff"};
        String[] videoSuffixes = {"3gp", "3gp", "avi", "mov", "mp4", "mpg", "mpeg", "wmv", "ogg", "ogv", "m2v"};
        if (basefilename.lastIndexOf('.') <= 0) {
            return false;
        }
        String suffix = basefilename.substring(basefilename.lastIndexOf('.') + 1).toLowerCase();
        for (int i = 0; i < photoSuffixes.length; i++) {
            if (photoSuffixes[i].equals(suffix)) {
                return true;
            }
        }
        for (int i = 0; i < videoSuffixes.length; i++) {
            if (videoSuffixes[i].equals(suffix)) {
                return true;
            }
        }
        return false;
    }

}
