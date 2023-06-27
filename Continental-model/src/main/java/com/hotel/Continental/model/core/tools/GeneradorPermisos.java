package com.hotel.continental.model.core.tools;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GeneradorPermisos {
    private static final String API_PACKAGE_PATH = "com.hotel.continental.api.core.service";
    private static final String PERMISSION_TABLE_NAME = "tserver_permission";
    private static final String ROLE_PERMISSION_TABLE_NAME = "trole_server_permission";

    public static void main(String[] args) {
        String path = ".\\Continental-api\\src\\main\\java\\com\\hotel\\continental\\api\\core\\service";
        System.out.println(new File(path).getAbsolutePath());
        List<Class<?>> interfaceList = listarInterfaces(new File(path).listFiles());
        String role = "client";

        for (Class<?> interfaceClass : interfaceList) {
            generateInsertStatements(interfaceClass);
        }

        int generarPermisos = JOptionPane.showConfirmDialog(
                null,
                "¿Deseas generar los permisos?",
                "Generar permisos",
                JOptionPane.YES_NO_OPTION
        );

        if (generarPermisos == JOptionPane.YES_OPTION) {
            for (Class<?> interfaceClass : interfaceList) {
                Method[] methods = interfaceClass.getDeclaredMethods();
                System.out.println("------------------------" + interfaceClass.getName() + "--------------------------");

                for (Method method : methods) {
                    int permiso = JOptionPane.showConfirmDialog(
                            null,
                            "¿Tiene " + role + " permiso para: " + interfaceClass.getSimpleName() + "/" + method.getName(),
                            "Generar permisos",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (permiso == JOptionPane.YES_OPTION) {
                        String permissionName = interfaceClass.getName() + "/" + method.getName();
                        System.out.println(generateInsertsPermissions(role, permissionName) + "\n");
                    }
                }
            }
        }
    }

    private static List<Class<?>> listarInterfaces(File[] files) {
        List<Class<?>> interfaceList = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                interfaceList.addAll(listarInterfaces(file.listFiles()));
            } else {
                String fileName = file.getName();

                if (fileName.endsWith(".java")) {
                    String className = fileName.substring(0, fileName.length() - 5);

                    try {
                        Class<?> clazz = Class.forName(API_PACKAGE_PATH + "." + className);

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

    private static void generateInsertStatements(Class<?> interfaceClass) {
        Method[] methods = interfaceClass.getDeclaredMethods();

        for (Method method : methods) {
            String permissionName = interfaceClass.getName() + "/" + method.getName();
            String insertStatement = "INSERT INTO " + PERMISSION_TABLE_NAME + " (permission_name) " +
                    "SELECT '" + permissionName + "' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM " + PERMISSION_TABLE_NAME + " WHERE permission_name = '" + permissionName + "');";
            System.out.println(insertStatement);
        }
    }

    private static String generateInsertsPermissions(String roleName, String permissionName) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(ROLE_PERMISSION_TABLE_NAME).append(" (id_rolename, id_server_permission)\n");
        sb.append("SELECT tr.id_rolename, ts.id_server_permission\n");
        sb.append("FROM trole tr\n");
        sb.append("CROSS JOIN ").append(PERMISSION_TABLE_NAME).append(" ts\n");
        sb.append("WHERE tr.rolename = '").append(roleName).append("' ");
        sb.append("AND ts.permission_name = '").append(permissionName).append("' ");
        sb.append("AND NOT EXISTS (");
        sb.append("SELECT 1 FROM ").append(ROLE_PERMISSION_TABLE_NAME).append(" tsp ");
        sb.append("WHERE tsp.id_rolename = tr.id_rolename ");
        sb.append("AND tsp.id_server_permission = ts.id_server_permission");
        sb.append(");");
        return sb.toString();
    }

}

