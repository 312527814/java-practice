package com.my.pojo;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class FactoryBeanRoom implements FactoryBean<Room> {
    @Override
    public Room getObject() throws Exception {
        Room room = new Room();
        return room;
    }

    @Override
    public Class<?> getObjectType() {
        return Room.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
