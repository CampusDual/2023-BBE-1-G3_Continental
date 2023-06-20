package com.hotel.continental.model.core.tools;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class GeneradorPermisos {
    public static void main(String[] args) {
        String path = "C:\\Users\\artai\\Documents\\GitHub\\2023-BBE-1-G3\\Continental-api\\src\\main\\java\\com\\hotel\\continental\\api\\core\\service";
        List<Class<?>> interfaceList = listarInterfaces(new File(path).listFiles());
        String role = "manager";
        for (Class<?> interfaceClass : interfaceList) {
            generateInsertStatements(interfaceClass);
        }
        for (Class<?> interfaceClass : interfaceList) {
            Method[] methods = interfaceClass.getDeclaredMethods();
            System.out.println("------------------------"+interfaceClass.getName()+"--------------------------");
            for (Method method : methods) {
                int permiso=JOptionPane.showConfirmDialog(null, "Tiene "+role+" permiso para: " + interfaceClass.getSimpleName() + "/" + method.getName(), "Generar permisos", JOptionPane.YES_NO_OPTION);
                if(permiso==0){
                    String permissionName = interfaceClass.getName() + "/" + method.getName();
                    System.out.println(generateInsertsPermisions("admin", permissionName)+"\n");
                }
            }
        }
    }

    public static List<Class<?>> listarInterfaces(File[] files) {
        List<Class<?>> interfaceList = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                listarInterfaces(file.listFiles());
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".java")) {
                    String className = fileName.substring(0, fileName.length() - 5);
                    try {
                        Class<?> clazz = Class.forName("com.hotel.continental.api.core.service." + className);
                        if (clazz.isInterface() && Modifier.isPublic(clazz.getModifiers())) {
                            interfaceList.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return interfaceList;
    }

    public static void generateInsertStatements(Class<?> interfaceClass) {
        String tableName = "tserver_permission"; // Nombre de la tabla en la que deseas insertar los datos

        Method[] methods = interfaceClass.getDeclaredMethods();

        for (Method method : methods) {
            String permissionName = interfaceClass.getName() + "/" + method.getName();
            String insertStatement = "INSERT INTO " + tableName + " (permission_name) " +
                    "SELECT '" + permissionName + "' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM " + tableName + " WHERE permission_name = '" + permissionName + "');";
            System.out.println(insertStatement);

        }
    }

    public static String generateInsertsPermisions(String roleName, String permissionName) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO trole_server_permission (id_rolename, id_server_permission)\n");
        sb.append("SELECT tr.id_rolename, ts.id_server_permission\n");
        sb.append("FROM trole tr\n");
        sb.append("CROSS JOIN tserver_permission ts\n");
        sb.append("WHERE tr.rolename = '").append(roleName).append("' ");
        sb.append("AND ts.permission_name = '").append(permissionName).append("' ");
        sb.append("AND NOT EXISTS (");
        sb.append("SELECT 1 FROM trole_server_permission tsp ");
        sb.append("WHERE tsp.id_rolename = tr.id_rolename ");
        sb.append("AND tsp.id_server_permission = ts.id_server_permission");
        sb.append(");");
        return sb.toString();
    }
}
