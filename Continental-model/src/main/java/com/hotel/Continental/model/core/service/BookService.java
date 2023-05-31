package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IBookService;
import com.hotel.Continental.model.core.dao.BookDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy
@Service("BookService")
public class BookService implements IBookService {
    @Autowired
    private BookDao bookDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Override
    public EntityResult bookQuery(Map<?,?> keyMap, List<?> attrList) {
        return this.daoHelper.query(this.bookDao, keyMap, attrList);
    }

    public EntityResult bookInsert(Map<String, Object> attrMap) {
        String initialDateString = attrMap.remove(BookDao.STARTDATE).toString();
        String finalDateString = attrMap.remove(BookDao.ENDDATE).toString();
        Date initialDate = null;
        Date finalDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            initialDate = formatter.parse(initialDateString);
            finalDate = formatter.parse(finalDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        attrMap.put(BookDao.STARTDATE, initialDate);
        attrMap.put(BookDao.ENDDATE, finalDate);
        return this.daoHelper.insert(bookDao, attrMap);
    }

    public EntityResult bookDelete(Map<?, ?> keyMap) {
        return this.daoHelper.delete(this.bookDao,keyMap);
    }

    @Override
    public EntityResult bookUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        String initialDateString = attrMap.remove(BookDao.STARTDATE).toString();
        String finalDateString = attrMap.remove(BookDao.ENDDATE).toString();
        Date initialDate = null;
        Date finalDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            initialDate = formatter.parse(initialDateString);
            finalDate = formatter.parse(finalDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        attrMap.put(BookDao.STARTDATE, initialDate);
        attrMap.put(BookDao.ENDDATE, finalDate);
        return this.daoHelper.update(this.bookDao, attrMap, keyMap);
    }
}
