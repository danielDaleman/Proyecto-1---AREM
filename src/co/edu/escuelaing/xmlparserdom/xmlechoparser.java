/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.escuelaing.xmlparserdom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;

/**
 *
 * @author 2107990
 */
public class xmlechoparser {
    static final String outputEncoding = "UTF-8";
    static boolean bool=false;
    static boolean boolobj=false;
    static ArrayList<ArrayList<String>> objetos  = new ArrayList<ArrayList<String>>();
    static ArrayList<ArrayList<String>> lanes = new ArrayList<ArrayList<String>>();
    

    private static void usage() {
        System.out.println("Usage: xmlechoparser file1");
    }
      
    public static void main(String[] args) throws Exception {
        String filename = "src/ProcesoActual - 1.0.bpmn";

        // Retire el false para hacer un programa que se innvoque desde la linea de comandos
        if (args.length < 1 && false) {
            usage();
        } else {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filename));
            objetos.add(new ArrayList<String>());
            objetos.get(0).add("Objeto");
            objetos.get(0).add("Referencia");
            objetos.get(0).add("nombre");
            objetos.get(0).add("descripcion");
            navigate(doc);
            htmlCreator();
            for (int i = 0; i < objetos.size(); i++) {
                System.out.println(objetos.get(i));
            }
            FileOutputStream archivo;
            PrintStream p;
            archivo= new FileOutputStream("hola.html");
            p = new PrintStream(archivo);
            for (int i = 1; i < objetos.size(); i++) {
                p.println(objetos.get(i)+ "\n");
                
            }
            p.close();
            }
    }
    private static void htmlCreator(){
        boolean b=false;
        for (int i=0;i<objetos.size();i++){
            b=false;
            for(int z=0;z<lanes.size();z++){
                if(lanes.get(z).contains(objetos.get(i).get(1))){
                    objetos.get(i).set(1, lanes.get(z).get(0));
                    b=true;
                }
            }
            if (b==false){
                objetos.get(i).remove(1);
            }    
        }
        
        
    }
    private static void navigate(Node n) {
        navigate(n, "");
    }
    private static boolean tipoObjeto(Node n){
        boolobj=false;
        if(n.getNodeName().equals("model:startEvent")|| n.getNodeName().equals("model:userTask") || n.getNodeName().equals("model:exclusiveGateway") || n.getNodeName().equals("model:endEvent")
                || n.getNodeName().equals("model:boundaryEvent")|| n.getNodeName().equals("model:participant")||  n.getNodeName().equals("model:process")
                ){
            boolobj=true; 
        }
        return boolobj;
        
    }
    private static boolean validarNodo(Node n) {
        bool=false;
        if(
                 n.getNodeName().equals("model:flowNodeRef")|| n.getNodeName().equals("model:lane")|| n.getNodeName().equals("model:laneSet")
                || n.getNodeName().equals("model:documentation")|| n.getNodeName().equals("model:collaboration")|| tipoObjeto(n)
                || n.getNodeName().equals("model:definitions") || n.getNodeName().equals("#document") ||  n.getNodeName().equals("name")|| n.getNodeName().equals("id")|| n.getNodeName().equals("#text")){
            bool=true; 
        }
        return bool;
    }
        
    private static void navigate(Node n, String prefix) {
        if ((!getNodeTypeName(n.getNodeType()).equals("other type"))&&validarNodo(n) ){
            NodeList childnodes = n.getChildNodes();
            // Navegar los atributos del nodo
            if (tipoObjeto(n)){
                objetos.add(new ArrayList<String>());
                objetos.get(objetos.size()-1).add(n.getNodeName());
                NamedNodeMap childAttributes1 = n.getAttributes();
                if (childAttributes1 != null) {
                    for (int i = 0; i < childAttributes1.getLength(); i++) {
                        if (childAttributes1.item(i).getNodeName().equals("name")||childAttributes1.item(i).getNodeName().equals("id")){
                            objetos.get(objetos.size()-1).add (childAttributes1.item(i).getNodeValue());
                        }
                 }
                }
                else{
                    objetos.get(objetos.size()-1).add("");
                    objetos.get(objetos.size()-1).add("");
                }
                if (childnodes != null ) {
                    for (int i = 0; i < childnodes.getLength(); i++) {
                        if( childnodes.item(i).getNodeName().equals("model:documentation")){
                            NodeList childnodes1 = childnodes.item(i).getChildNodes();
                            objetos.get(objetos.size()-1).add(childnodes1.item(0).getNodeValue());
                        }
                    }  
                }   
            }
            if (n.getNodeName().equals("model:lane")){
                lanes.add(new ArrayList<String>());
                NamedNodeMap childAttributes2 = n.getAttributes();
                if (childAttributes2 != null) {
                    for (int i = 0; i < childAttributes2.getLength(); i++) {
                        if (childAttributes2.item(i).getNodeName().equals("name")){
                            lanes.get(lanes.size()-1).add (childAttributes2.item(i).getNodeValue());
                        }
                    }
                }
                if (childnodes != null ) {
                    for (int i = 0; i < childnodes.getLength(); i++) {
                        if( childnodes.item(i).getNodeName().equals("model:flowNodeRef")){
                            NodeList childnodes2 = childnodes.item(i).getChildNodes();
                            lanes.get(lanes.size()-1).add(childnodes2.item(0).getNodeValue());
                        }
                    }  
                }   
                
                
            }
            
            
        
            NamedNodeMap childAttributes = n.getAttributes();
            if (childAttributes != null) {
                for (int i = 0; i < childAttributes.getLength(); i++) {
                    navigate(childAttributes.item(i), prefix + "|a----");
                }
            }
        
            //Navegar los nodos hijo del nodo actual
            for (int i = 0; i < childnodes.getLength(); i++) {
                navigate(childnodes.item(i), prefix + "|-----");
            }
    }
    }

    private static String getNodeTypeName(short nodeType) {
        String respuesta = "";
        switch (nodeType) {
            case Node.ATTRIBUTE_NODE:
                respuesta = "ATTRIBUTE_NODE";
                break;
            case Node.DOCUMENT_NODE:
                respuesta = "DOCUMENT_NODE";
                break;
            
            case Node.ELEMENT_NODE:
                respuesta = "ELEMENT_NODE";
                break;
        
            case Node.TEXT_NODE:
                respuesta = "TEXT_NODE";
                break;
            default:
                respuesta = "other type";
                break;
        }

        return respuesta;
    }
}
