package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IBookService;
import com.hotel.Continental.model.core.dao.BookDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Lazy
@Service("BookService")
public class BookService implements IBookService {
    @Autowired
    private BookDao bookDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public EntityResult bookInsert(Map<?, ?> attrMap) {
        return this.daoHelper.insert(bookDao, attrMap);
    }

    public EntityResult bookDelete(Map<?, ?> keyMap) {
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put("bookdowndate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
        return this.daoHelper.update(this.bookDao, attrMap, keyMap);
    }
}
