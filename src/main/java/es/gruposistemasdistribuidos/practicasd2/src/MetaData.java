/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.gruposistemasdistribuidos.practicasd2.src;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Paula
 */
public class MetaData {
    
    //Atributos obligatorios
    private File carpeta;
    private String seguridad, tipoContenido;
    private int privacidad;
    //Atributos opcionales
    private String titulo, descripcion;
    private int visibilidad;
    private int licencia;
    private Collection <String> etiquetas, personas; //Dudas?
    
    //Constructor
    public MetaData (File ruta, String seguridad, String tipoContenido, int privacidad){
        
        this.etiquetas = new ArrayList();
        this.personas = new ArrayList();
        this.carpeta = ruta;
        this.seguridad = seguridad;
        this.tipoContenido = tipoContenido;
        this.privacidad = privacidad; 
        
        this.visibilidad = -1;
        this.licencia = -1;
        
    }
    
    //Getters & Setters

    public File getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(File ruta) {
        this.carpeta = ruta;
    }

    public String getSeguridad() {
        return seguridad;
    }

    public void setSeguridad(String seguridad) {
        this.seguridad = seguridad;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public int getPrivacidad() {
        return privacidad;
    }

    public void setPrivacidad(int privacidad) {
        this.privacidad = privacidad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(int visibilidad) {
        this.visibilidad = visibilidad;
    }

    public int getLicencia() {
        return licencia;
    }

    public void setLicencia(int licencia) {
        this.licencia = licencia;
    }

    public Collection<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(Collection<String> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public Collection<String> getPersonas() {
        return personas;
    }

    public void setPersonas(Collection<String> personas) {
        this.personas = personas;
    }
    
    
}
