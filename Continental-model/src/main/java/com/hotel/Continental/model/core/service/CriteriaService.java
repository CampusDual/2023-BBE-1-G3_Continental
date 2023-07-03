package com.hotel.continental.model.core.service;


import com.hotel.continental.api.core.service.ICriteriaService;
import com.hotel.continental.model.core.dao.CriteriaDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Lazy
@Service("CriteriaService")
public class CriteriaService implements ICriteriaService {
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private CriteriaDao criteriaDao;
    /**
     * Metodo que devuelve un EntityResult con los datos del criterio
     *
     * @param keyMap   Mapa de claves que identifican el criterio
     * @param attrList Lista de atributos que se quieren obtener
     * @return EntityResult con los datos del criterio o un mensaje de error
     */
    @Override
    public EntityResult criteriaQuery(Map<?, ?> keyMap, List<String> attrList) {
        //si el attr tiene 1 asterisco devolver todos los atributos
        if (attrList.size() == 1 && attrList.get(0).equals("*")) {
            Field[] fields = CriteriaDao.class.getDeclaredFields();
            attrList.clear();
            for (Field field : fields) {
                attrList.add(field.getName());
            }
        }
        EntityResult er = this.daoHelper.query(this.criteriaDao, keyMap, attrList);
        //Si hay filtro y no hay datos devolver error
        if (!keyMap.isEmpty() && er.calculateRecordNumber() == 0) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.CRITERIA_NOT_EXIST);
        }
        return er;
    }
}
