package com.hotel.continental.model.core.tools;

import com.hotel.continental.model.core.dao.UserDao;
import com.ontimize.jee.common.services.user.UserInformation;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

public class Auth {
    /**
     * Comprueba que el usuario que esta intentando hacer la operacion es el mismo que el que esta logueado o es admin
     * @param keyMap Mapa con los datos del usuario
     * @param necesaryRole rol necesario para hacer la operacion
     * @throws NoAllowedException Excepcion que se lanza si el usuario no tiene permisos para hacer la operacion
     */
    public static void checkUserPermission(Map<?, ?> keyMap,String necesaryRole) throws NoAllowedException {
        List<GrantedAuthority> userRole = (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        //compruebo que entre todos los roles que tiene el usuario, el que necesita para hacer la operacion es uno de ellos,
        for (GrantedAuthority x : userRole) {
            //Si tiene el rol necesario para hacer la operacion, compruebo que el usuario que esta intentando hacer la operacion es el mismo que el que esta logueado o es admin
            if (x.getAuthority().compareTo(necesaryRole) != 0) {
                UserInformation user = ((UserInformation) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                if (user.getLogin().compareTo((String) keyMap.get(UserDao.USER_)) != 0) {
                    throw new NoAllowedException();
                }
            }else{
                return;
            }
        }
    }
    /**
     * Metodo que comprueba que el usuario que esta intentando hacer la operacion no esta dado de baja
     *
     */
    public static void checkUserStatus() throws NoAllowedException {
        UserInformation user = ((UserInformation) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        //compruebo que el usuario no este dado de baja
        //la other data no se actualiza con la bbdd, se inicializa con la aplicacion de tal forma que si se cambia en la bbdd no se actualiza
        //por lo que hay que hacer una consulta a la bbdd para comprobar que el usuario no este dado de baja
        if (user.getOtherData().get(UserDao.USERBLOCKED) != null) {
            throw new NoAllowedException();
        }
    }
}
