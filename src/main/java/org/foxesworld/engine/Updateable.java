package org.foxesworld.engine;

public interface Updateable {

    /**
     * Вызывается при каждом обновлении с заданным значением tpf.
     * @param tpf Время, прошедшее с предыдущего обновления.
     */
    void update(float tpf);
}