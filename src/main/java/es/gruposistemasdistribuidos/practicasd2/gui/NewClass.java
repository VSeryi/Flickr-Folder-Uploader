/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.gruposistemasdistribuidos.practicasd2.gui;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.uploader.UploadMetaData;
import java.io.File;
import java.io.FilenameFilter;
import static java.lang.reflect.Array.set;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Paula
 */
public class NewClass {

    public void cosa() {
        SwingWorker worker = new SwingWorker() {
            private String getContentType() {
                if (jRadioButton6.isSelected()) {
                    return Flickr.CONTENTTYPE_PHOTO;
                } else if (jRadioButton7.isSelected()) {
                    return Flickr.CONTENTTYPE_SCREENSHOT;
                } else {
                    return Flickr.CONTENTTYPE_OTHER;
                }
            }

            private String getSafetyLevel() {

                if (jRadioButton1.isSelected()) {
                    return Flickr.SAFETYLEVEL_SAFE;
                } else if (jRadioButton2.isSelected()) {
                    return Flickr.SAFETYLEVEL_MODERATE;
                } else {
                    return Flickr.SAFETYLEVEL_RESTRICTED;
                }
            }

            @Override
            protected Object doInBackground() throws Exception {
                flickr = new FlickrInterface();
                File carpeta = new File(labelRuta.getText());
                FileNameExtensionFilter filtro = new FileNameExtensionFilter("Filtro blabla", "jpg", "png", "gif", "mp4");
                List<File> arrayArchivos = Arrays.asList(carpeta.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return ((name.toLowerCase().endsWith(".jpg")) || (name.toLowerCase().endsWith(".bmp"))
                                || (name.toLowerCase().endsWith(".png")) || (name.toLowerCase().endsWith(".gif"))
                                || (name.toLowerCase().endsWith(".mp4")) || (name.toLowerCase().endsWith(".avi")));
                    }
                }));
                UploadMetaData md = new UploadMetaData();
                md.setAsync(true);
                md.setContentType(this.getContentType());
                md.setDescription("Archivos de la carpeta " + carpeta.getName());
                md.setPublicFlag(jRadioButton4.isSelected());
                md.setHidden(jRadioButton5.isSelected());
                if (jRadioButton5.isEnabled()) {
                    md.setFamilyFlag(jCheckBox3.isSelected());
                    md.setFriendFlag(jCheckBox4.isSelected());
                }
                md.setSafetyLevel(this.getSafetyLevel());

                int archivos = arrayArchivos.size();
                System.out.println(set);

                jProgressBar1.setMaximum(archivos);

                ThreadMideTiempo th;
                th = new ThreadMideTiempo(archivos);
                th.start();

                for (File f : arrayArchivos) {
                    md.setFilename(f.getName().substring(0, f.getName().length() - 4));
                    md.setTitle(f.getName().substring(0, f.getName().length() - 4));

                    try {
                        String ticketString = flickr.subirPhoto(f, md);
                        set.add(ticketString);
                        System.out.println("main" + flickr.checkTickets(set).size());
                        //no se cuando es necesario esperar o cuando no, no tengo ni puta idea
                    } catch (FlickrException ex) {
                        Logger.getLogger(PruebaVentanas.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                return null;

            }
        };
        worker.execute();
    }
}
