/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.gruposistemasdistribuidos.practicasd2.src;

/**
 *
 * @author Paula
 */
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.photos.licenses.LicensesInterface;
import com.flickr4java.flickr.photos.upload.Ticket;
import com.flickr4java.flickr.photos.upload.UploadInterface;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgressMonitorDemo extends JPanel
        implements ActionListener,
        PropertyChangeListener {

    private ProgressMonitor progressMonitor;
    private final Sesion sesion;
    private final MetaData metaData;

//    private JButton startButton;
//    private JTextArea taskOutput;
    private Task task;

    class Task extends SwingWorker<Void, Void> {

        private int completados = 0;

        public void setCompletados(int completados) {
            int oldCompletados = this.completados;
            this.completados = completados;
            this.getPropertyChangeSupport().firePropertyChange("completados",
                    oldCompletados, completados);
        }

          public int
            getCompletados() {
        return completados;
    }
            
            
            
        @Override
        public Void doInBackground() throws FlickrException, PropertyVetoException {
//            Random random = new Random();
//            int progress = 0;
//            setProgress(0);
//            try {
//                Thread.sleep(1000);
//                while (progress < 100 && !isCancelled()) {
//                    //Sleep for up to one second.
//                    Thread.sleep(random.nextInt(1000));
//                    //Make random progress.
//                    progress += random.nextInt(10);
//                    setProgress(Math.min(progress, 100));
//                }
//            } catch (InterruptedException ignore) {
//            }

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
            RequestContext.getRequestContext().setAuth(miFlickr.getAuth());
            Uploader uploader = miFlickr.getUploader();
            LicensesInterface licenser = miFlickr.getLicensesInterface();
            PeopleInterface peopler = miFlickr.getPeopleInterface();

            java.util.List<File> archivosValidos = new ArrayList();

            for (File f : metaData.getCarpeta().listFiles()) {

                archivosValidos.add(f);
            }
            Set<String> tickets = new HashSet<String>();
            int numFotos = archivosValidos.size();
//        progressBar.setMaximum(numFotos);
//        progressBar.setString("Subidos " + completados + " de " + numFotos + " archivos.");
            for (File f : archivosValidos) {

                try {
                    String ticketName = uploader.upload(f, metaDataFlickr);
                    tickets.add(ticketName);

                } catch (FlickrException ex) {
                    Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            UploadInterface inter = miFlickr.getUploadInterface();
            int numTickets = tickets.size();
            System.out.println(numTickets);
//        int completados = 0;
//        progressBar.setMaximum(numTickets);
//        progressBar.setString("Subidos "+completados+" de "+numTickets+" archivos.");
            while (!(completados >= numTickets)) {
                for (Ticket t : inter.checkTickets(tickets)) {
                    if (t.getStatus() > 0) {
                        System.out.println("DEBERIA MOSTRARSE AQUI");
                        setCompletados(completados + 1);
//                    progressBar.setValue(completados);
//                    progressBar.setString("Subidos " + completados + " de " + numTickets + " archivos.");

                    };
                }
            }
            return null;
        }

        @Override
        public void done() {
            // llamar a gui pa decr q hemos terminado
        }
    }

    public ProgressMonitorDemo(MetaData metaData, Sesion sesion) {
//        super(new BorderLayout());
        this.metaData = metaData;
        this.sesion = sesion;

        progressMonitor = new ProgressMonitor(ProgressMonitorDemo.this,
                "Running a Long Task",
                "", 0, 100);
        progressMonitor.setProgress(0);
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
//        startButton.setEnabled(false);
        //Create the demo's UI.
//        startButton = new JButton("Start");
//        startButton.setActionCommand("start");
//        startButton.addActionListener(this);
// 
//        taskOutput = new JTextArea(5, 20);
//        taskOutput.setMargin(new Insets(5,5,5,5));
//        taskOutput.setEditable(false);
// 
//        add(startButton, BorderLayout.PAGE_START);
//        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
//        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {

    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Se a completado " + evt.toString());
        if ("completados" == evt.getPropertyName()) {

            int completado = (Integer) evt.getNewValue();
            System.out.println("Se a completado " + completado);
            progressMonitor.setProgress(completado);
            String message
                    = String.format("Completed %d%%.\n", completado);
            progressMonitor.setNote(message);
//            taskOutput.append(message);
            if (progressMonitor.isCanceled() || task.isDone()) {
                Toolkit.getDefaultToolkit().beep();
                if (progressMonitor.isCanceled()) {
                    task.cancel(true);
//                    taskOutput.append("Task canceled.\n");
                } else {
//                    taskOutput.append("Task completed.\n");
                }
//                startButton.setEnabled(true);
            }
        }

    }

//    /**
//     * Create the GUI and show it. For thread safety, this method should be
//     * invoked from the event-dispatching thread.
//     */
//    private static void createAndShowGUI() {
//        //Create and set up the window.
//        JFrame frame = new JFrame("ProgressMonitorDemo");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        //Create and set up the content pane.
//        JComponent newContentPane = new ProgressMonitorDemo();
//        newContentPane.setOpaque(true); //content panes must be opaque
//        frame.setContentPane(newContentPane);
//
//        //Display the window.
//        frame.pack();
//        frame.setVisible(true);
//    }
//
//    public static void main(String[] args) {
//        //Schedule a job for the event-dispatching thread:
//        //creating and showing this application's GUI.
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                createAndShowGUI();
//            }
//        });
//    }
}
